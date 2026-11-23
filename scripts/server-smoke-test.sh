#!/usr/bin/env bash
# Boots a dev server for one Minecraft version and loader and proves Christmas Ginger House works at runtime, not only
# compiles (NightBeam convention; same pattern as NoMore63AE2's scripts/server-smoke-test.sh). A datapack, with no
# player online:
#   places every structure (the gingerbread house and the Christmas tree) with /place structure,
#   places every structure template with /place template and every configured feature (wild ginger) with
#   /place feature on a prepared pad,
#   summons every entity (gingerbread man, reindeer, Krampus), checks each one is still there after a few ticks and
#   lets them run their AI for 600 ticks,
#   sets every block (presents, advent calendar, lights, ornaments, gingerbread blocks, ginger crop...) on farmland,
#   summons every item as an item entity and checks each one is still there a few ticks later
#   (an unknown or broken item is dropped as air),
#   then runs /save-all and a guarded /reload and stops.
# The mod has no commands and no mixins of its own.
# Fails on a crash report, a mixin error, an ERROR/WARN/exception line mentioning the mod, a failed command, or when
# the RAN/OK markers never show up.
#   usage: scripts/server-smoke-test.sh <1.20.1|1.21.1|26.2|26.3> <fabric|forge|neoforge>
set -u
VERSION="$1"; LOADER="$2"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/$VERSION"
MOD=christmas_ginger_house
NS=cgh_smoke
RUN="$LOADER/runs/server"
rm -rf "$RUN/world" "$RUN/crash-reports" "$RUN/logs"
PACK="$RUN/world/datapacks/$NS"
mkdir -p "$PACK"
echo "eula=true" > "$RUN/eula.txt"
cat > "$RUN/server.properties" <<PROPS
online-mode=false
function-permission-level=4
spawn-protection=0
view-distance=6
simulation-distance=6
level-seed=20261123
initial-enabled-packs=vanilla,file/$NS
PROPS
if [ "$LOADER" = forge ]; then
  # In the dev environment Forge's LAN advertiser loads a client-only class and can stop the server.
  mkdir -p "$RUN/defaultconfigs"
  printf '[server]\n\tadvertiseDedicatedServerToLan = false\n' > "$RUN/defaultconfigs/forge-server.toml"
fi

# pack.mcmeta as valid JSON, with the same format (range) as the mod's own pack (26.x uses min_format/max_format).
python3 - common/src/main/resources/pack.mcmeta "$PACK/pack.mcmeta" <<'PY'
import json, sys
src = json.load(open(sys.argv[1]))["pack"]
pack = {"description": "Christmas Ginger House smoke test"}
for key in ("pack_format", "min_format", "max_format", "supported_formats"):
    if key in src:
        pack[key] = src[key]
json.dump({"pack": pack}, open(sys.argv[2], "w"))
PY
cat "$PACK/pack.mcmeta"; echo

# What the mod adds, read from its own resources and its entity registry.
ASSETS=common/src/main/resources/assets/$MOD
DATA=common/src/main/resources/data/$MOD
# Configured features live in worldgen/configured_feature up to 26.2 and in worldgen/feature from 26.3.
if [ -d "$DATA/worldgen/configured_feature" ]; then FEATURE_DIR="$DATA/worldgen/configured_feature"; else FEATURE_DIR="$DATA/worldgen/feature"; fi
FEATURES="$(cd "$FEATURE_DIR" && find . -name '*.json' | sed 's|^\./||; s|\.json$||' | sort)"
STRUCTURES="$(ls "$DATA/worldgen/structure" | sed 's/\.json$//' | sort)"
# Structure templates: structures/ up to 1.20.1, structure/ from 1.21.
TEMPLATES="$(for d in "$DATA/structures" "$DATA/structure"; do [ -d "$d" ] && (cd "$d" && find . -name '*.nbt' | sed 's|^\./||; s|\.nbt$||'); done | sort)"
ENTITIES="$(grep -o 'register("[a-z_]*"' common/src/main/java/tn/nightbeam/christmasgingerhouse/registry/ModEntities.java | sed 's/register("//; s/"//')"
BLOCKS="$(ls "$ASSETS/blockstates" | sed 's/\.json$//' | sort)"
# 26.x has an item model definition (assets/<mod>/items) for every item; earlier versions an item model for every item.
if [ -d "$ASSETS/items" ]; then ITEM_DIR="$ASSETS/items"; else ITEM_DIR="$ASSETS/models/item"; fi
ITEMS="$(ls "$ITEM_DIR" | sed 's/\.json$//' | sort)"
FEATURE_COUNT=$(echo "$FEATURES" | grep -c .); STRUCTURE_COUNT=$(echo "$STRUCTURES" | grep -c .)
TEMPLATE_COUNT=$(echo "$TEMPLATES" | grep -c .); ENTITY_COUNT=$(echo "$ENTITIES" | grep -c .)
BLOCK_COUNT=$(echo "$BLOCKS" | grep -c .); ITEM_COUNT=$(echo "$ITEMS" | grep -c .)
echo "Features ($FEATURE_COUNT): $(echo $FEATURES)"
echo "Structures ($STRUCTURE_COUNT): $(echo $STRUCTURES)"
echo "Templates ($TEMPLATE_COUNT): $(echo $TEMPLATES)"
echo "Entities ($ENTITY_COUNT): $(echo $ENTITIES)"
echo "Blocks ($BLOCK_COUNT), items ($ITEM_COUNT)"
[ "$FEATURE_COUNT" -gt 0 ] && [ "$STRUCTURE_COUNT" -gt 0 ] && [ "$ENTITY_COUNT" -gt 0 ] && [ "$BLOCK_COUNT" -gt 0 ] && [ "$ITEM_COUNT" -gt 0 ] \
  || { echo "::error::could not read the mod's features, structures, entities, blocks or items"; exit 1; }

if [ "$VERSION" = 1.20.1 ]; then COUNT='Count:1b'; else COUNT='count:1'; fi
case "$VERSION" in
  26.*) DAYLIGHT=advance_time; WEATHER=advance_weather; MOBS=spawn_mobs ;;
  *) DAYLIGHT=doDaylightCycle; WEATHER=doWeatherCycle; MOBS=doMobSpawning ;;
esac

# Layout around spawn (all inside the force-loaded -96..96 square):
#   structures on the terrain at (0, 48) and (-48, -48), 64 blocks apart;
#   templates in the air at y=140, 48 blocks apart from x=-90 along z=-60;
#   feature pads (13x13, grass) at y=99 along z=-64 from x=16;
#   the entity pad at (-64, 64) and a 26x26 farmland field for the blocks at x 48..72, z 24..48;
#   items floating at y=150 above the field.
STRUCTURE_SPOTS=("0 48" "-48 -48" "64 -16" "-64 0")
[ "$STRUCTURE_COUNT" -le ${#STRUCTURE_SPOTS[@]} ] || { echo "::error::not enough structure spots"; exit 1; }
pad() { # x z
  echo "fill $(($1-6)) 97 $(($2-6)) $(($1+6)) 98 $(($2+6)) minecraft:dirt"
  echo "fill $(($1-6)) 99 $(($2-6)) $(($1+6)) 99 $(($2+6)) minecraft:grass_block"
  echo "fill $(($1-6)) 100 $(($2-6)) $(($1+6)) 125 $(($2+6)) minecraft:air"
}

FN="$PACK/data/$NS"
for dir in function functions; do
  mkdir -p "$FN/$dir" "$PACK/data/minecraft/tags/$dir"
  echo "{\"values\":[\"$NS:start\"]}" > "$PACK/data/minecraft/tags/$dir/load.json"
  check() { # marker command...
    local marker="$1"; shift
    echo "scoreboard players set #r cgh 0"
    echo "execute store success score #r cgh run $*"
    echo "execute if score #r cgh matches 1 run say CGH_OK $marker"
    echo "execute unless score #r cgh matches 1 run say CGH_FAILED $marker"
  }
  {
    echo 'scoreboard objectives add cgh dummy'
    # /reload in finish.mcfunction runs this load function again; only the first load starts the test.
    echo 'execute if score #started cgh matches 1 run say CGH_SMOKE_RELOADED'
    echo "execute unless score #started cgh matches 1 run function $NS:begin"
  } > "$FN/$dir/start.mcfunction"
  {
    echo 'scoreboard players set #started cgh 1'
    echo 'forceload add -96 -96 96 96'
    echo "gamerule $DAYLIGHT false"
    echo "gamerule $WEATHER false"
    echo "gamerule $MOBS false"
    echo 'time set noon'
    echo 'weather clear'
    echo 'say CGH_SMOKE_LOADED'
    echo "schedule function $NS:build 300t"
  } > "$FN/$dir/begin.mcfunction"

  {
    x=16
    for _ in $FEATURES; do pad "$x" -64; x=$((x + 16)); done
    pad -64 64
    echo "fill 46 97 22 74 98 50 minecraft:dirt"
    echo "fill 46 99 22 74 99 50 minecraft:farmland"
    echo "fill 46 100 22 74 125 50 minecraft:air"
    echo 'say CGH_SMOKE_BUILT'
    echo "schedule function $NS:run 20t"
  } > "$FN/$dir/build.mcfunction"

  {
    i=0
    for s in $STRUCTURES; do
      set -- ${STRUCTURE_SPOTS[$i]}; i=$((i + 1))
      check "place_structure_$s" place structure "$MOD:$s" "$1" "$2"
    done
    x=-90
    for t in $TEMPLATES; do
      check "place_template_$t" place template "$MOD:$t" "$x" 140 -60
      x=$((x + 48))
    done
    x=16
    for f in $FEATURES; do
      check "place_feature_$f" place feature "$MOD:$f" "$x" 100 -64
      x=$((x + 16))
    done
    # Every entity on its pad, kept from despawning.
    dx=-4
    for e in $ENTITIES; do
      check "summon_$e" summon "$MOD:$e" "$((-64 + dx))" 100 64 "{Tags:[\"cgh_smoke\",\"cgh_e_$e\"],PersistenceRequired:1b}"
      dx=$((dx + 4))
    done
    # Every block on the farmland field, two blocks apart.
    j=0
    for b in $BLOCKS; do
      x=$((48 + (j % 12) * 2)); z=$((24 + (j / 12) * 2)); j=$((j + 1))
      check "setblock_$b" setblock "$x" 100 "$z" "$MOD:$b"
    done
    # Every item as a floating item entity above the field.
    j=0
    for it in $ITEMS; do
      x=$((48 + (j % 20))); z=$((24 + (j / 20))); j=$((j + 1))
      check "summon_$it" summon minecraft:item "$x" 150 "$z" "{Item:{id:\"$MOD:$it\",$COUNT},Tags:[\"cgh_smoke\",\"cgh_i_$it\"],NoGravity:1b,PickupDelay:32767,Age:-32768}"
    done
    echo 'say CGH_SMOKE_RAN'
    echo "schedule function $NS:alive 20t"
  } > "$FN/$dir/run.mcfunction"

  # An entity that fails to tick, or an item that does not exist or whose stack fails to load, is gone by now.
  {
    for e in $ENTITIES; do
      check "alive_$e" execute if entity "@e[type=$MOD:$e,tag=cgh_e_$e]"
    done
    for it in $ITEMS; do
      check "item_$it" execute if entity "@e[type=minecraft:item,tag=cgh_i_$it]"
    done
    echo "schedule function $NS:finish 600t"
  } > "$FN/$dir/alive.mcfunction"

  {
    check save save-all flush
    echo 'reload'
    echo "schedule function $NS:end 100t"
  } > "$FN/$dir/finish.mcfunction"

  {
    echo 'kill @e[tag=cgh_smoke]'
    echo 'say CGH_SMOKE_OK'
    echo 'stop'
  } > "$FN/$dir/end.mcfunction"
done
EXPECTED_OK=$(cat "$FN/function/"*.mcfunction | grep -c "say CGH_OK")

LOG="$ROOT/server-smoke-$VERSION-$LOADER.log"
./gradlew ":$LOADER:runServer" --stacktrace -Porg.gradle.java.installations.fromEnv=JAVA_HOME_17_X64,JAVA_HOME_21_X64,JAVA_HOME_25_X64 < /dev/null > "$LOG" 2>&1 &
PID=$!
CRASH='This crash report has been saved|Encountered an unexpected exception|Failed to start the minecraft server|Mixin apply.*failed|InvalidInjectionException|MixinApplyError|MixinTransformerError|Mixin.*(error|failed)'
result=timeout
for _ in $(seq 1 240); do
  sleep 5
  if grep -q "CGH_SMOKE_OK" "$LOG"; then result=ok; break; fi
  if grep -q -E "$CRASH" "$LOG"; then result=crash; break; fi
  if ! kill -0 $PID 2>/dev/null; then result=exited; break; fi
done
sleep 10
pkill -P $PID 2>/dev/null; kill $PID 2>/dev/null; wait $PID 2>/dev/null

echo "===== markers"
grep -n -E "CGH_SMOKE|CGH_FAILED|Found new data pack" "$LOG" | head -80
for f in "$RUN"/crash-reports/*.txt; do [ -f "$f" ] && { echo "=== $f"; head -100 "$f"; }; done
echo "===== errors and warnings"
grep -n -E "ERROR|WARN|Exception|Caused by|[Mm]ixin.*(fail|error)|Failed to|Couldn't|Unknown" "$LOG" | grep -v -E "Download|Task :" | head -80
echo "Result: $result"

fail=0
[ "$result" = ok ] || { echo "::error::server did not reach CGH_SMOKE_OK ($result)"; fail=1; }
grep -q -E "$CRASH" "$LOG" && { echo "::error::crash or mixin error"; fail=1; }
grep -q "CGH_SMOKE_RAN" "$LOG" || { echo "::error::the smoke function never ran"; fail=1; }
grep -q "CGH_SMOKE_RELOADED" "$LOG" || { echo "::error::/reload never finished"; fail=1; }
if grep -n "CGH_FAILED" "$LOG"; then echo "::error::a command failed"; fail=1; fi
oks=$(grep -c "CGH_OK " "$LOG")
echo "Commands OK: $oks of $EXPECTED_OK"
[ "$oks" -ge "$EXPECTED_OK" ] || { echo "::error::not every command check passed"; fail=1; }
# Errors, warnings and exceptions that mention the mod (its id, its name, its package or the smoke pack).
if grep -n -E "(ERROR|WARN|Exception|Caused by|Failed|Couldn't|Unknown|Errors in)" "$LOG" \
    | grep -v -E "JAVA_TOOL_OPTIONS|Reference map '[a-z_.]*refmap.json' .* could not be read" \
    | grep -i -E "$MOD|Christmas Ginger House|tn\.nightbeam\.christmasgingerhouse|$NS"; then
  echo "::error::error or warning mentioning the mod"; fail=1
fi
exit $fail
