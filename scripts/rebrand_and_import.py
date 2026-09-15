#!/usr/bin/env python3
"""Rebrand fisherman-house MultiLoader skeleton to Christmas Ginger House and import jar datapack."""
from __future__ import annotations

import json
import shutil
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
EXTRACT = Path("/tmp/ginger_extract")
VERSIONS = ["1.20.1", "1.21.1", "26.1.2", "26.2"]

OLD_PKG = "tn/nightbeam/fishermanhouse"
NEW_PKG = "tn/nightbeam/christmasgingerhouse"
OLD_PKG_DOT = "tn.nightbeam.fishermanhouse"
NEW_PKG_DOT = "tn.nightbeam.christmasgingerhouse"


def replace_in_file(path: Path) -> None:
    try:
        text = path.read_text(encoding="utf-8")
    except (UnicodeDecodeError, IsADirectoryError, OSError):
        return
    orig = text
    reps = [
        ("The Fisherman House", "Christmas Ginger House"),
        ("the-fisherman-house", "christmas-ginger-house"),
        ("The-Fisherman-House", "Christmas-Ginger-House"),
        ("fisherman_house", "christmas_ginger_house"),
        ("Fisherman House", "Christmas Ginger House"),
        ("FishermanHouse", "ChristmasGingerHouse"),
        ("fishermanhouse", "christmasgingerhouse"),
        ("FISHERMAN_HOUSE", "CHRISTMAS_GINGER_HOUSE"),
        (OLD_PKG_DOT, NEW_PKG_DOT),
        ("MODRINTH_PROJECT_ID=B5T1jHAp", "MODRINTH_PROJECT_ID=Ddgd2w2n"),
        ("CURSEFORGE_PROJECT_ID=1406628", "CURSEFORGE_PROJECT_ID=1409805"),
        ("B5T1jHAp", "Ddgd2w2n"),
        ("1406628", "1409805"),
    ]
    for a, b in reps:
        text = text.replace(a, b)
    # Remaining bare "fisherman" (rare) — do not touch after package rename
    text = text.replace("tn.nightbeam.christmas_gingerhouse", NEW_PKG_DOT)
    text = text.replace("tn/nightbeam/christmas_gingerhouse", NEW_PKG)
    text = text.replace("ChristmasGingerHouseHouse", "ChristmasGingerHouse")
    if text != orig:
        path.write_text(text, encoding="utf-8")


def rename_java_packages() -> None:
    for ver in VERSIONS:
        for loader in ("common", "fabric", "forge", "neoforge"):
            base = ROOT / ver / loader / "src" / "main"
            if not base.exists():
                continue
            old_java = base / "java" / OLD_PKG
            new_java = base / "java" / NEW_PKG
            if old_java.exists():
                new_java.parent.mkdir(parents=True, exist_ok=True)
                if new_java.exists():
                    shutil.rmtree(new_java)
                shutil.move(str(old_java), str(new_java))
            services = base / "resources" / "META-INF" / "services"
            if services.exists():
                for svc in list(services.iterdir()):
                    new_name = svc.name.replace("fishermanhouse", "christmasgingerhouse")
                    if new_name != svc.name:
                        target = services / new_name
                        if target.exists():
                            target.unlink()
                        svc.rename(target)
            res = base / "resources"
            if res.exists():
                for mix in list(res.glob("*fisherman*")):
                    new_name = mix.name.replace("fisherman_house", "christmas_ginger_house").replace(
                        "fishermanhouse", "christmasgingerhouse"
                    )
                    if new_name != mix.name:
                        mix.rename(mix.with_name(new_name))


def rename_java_files() -> None:
    mapping = {
        "FishermanHouseMod.java": "ChristmasGingerHouseMod.java",
        "FishermanHouse.java": "ChristmasGingerHouse.java",
        "FishermanHouseCommon.java": "ChristmasGingerHouseCommon.java",
    }
    for path in list(ROOT.rglob("*.java")):
        new = mapping.get(path.name)
        if new and path.name != new:
            dest = path.with_name(new)
            if dest.exists():
                dest.unlink()
            path.rename(dest)


def clear_fisherman_datapack() -> None:
    for ver in VERSIONS + ["shared"]:
        if ver == "shared":
            bases = [ROOT / "shared" / "resources"]
        else:
            bases = [ROOT / ver / "common" / "src" / "main" / "resources"]
        for base in bases:
            if not base.exists():
                continue
            for d in list(base.rglob("fisherman_house")):
                if d.is_dir():
                    shutil.rmtree(d, ignore_errors=True)


def write_structure_json(biomes) -> dict:
    return {
        "type": "minecraft:jigsaw",
        "start_pool": "christmas_ginger_house:christmas_ginger_house",
        "size": 1,
        "max_distance_from_center": 64,
        "spawn_overrides": {},
        "step": "surface_structures",
        "terrain_adaptation": "none",
        "start_height": {"absolute": 0},
        "project_start_to_heightmap": "WORLD_SURFACE_WG",
        "biomes": biomes,
        "use_expansion_hack": False,
    }


def write_template_pool() -> dict:
    return {
        "name": "christmas_ginger_house:christmas_ginger_house",
        "fallback": "minecraft:empty",
        "elements": [
            {
                "weight": 1,
                "element": {
                    "element_type": "minecraft:single_pool_element",
                    "location": "christmas_ginger_house:house_ginger",
                    "projection": "rigid",
                    "processors": {
                        "processors": [
                            {
                                "processor_type": "minecraft:block_ignore",
                                "blocks": [
                                    {"Name": "minecraft:structure_block"},
                                    {"Name": "minecraft:air"},
                                    {"Name": "minecraft:void_air"},
                                    {"Name": "minecraft:cave_air"},
                                ],
                            }
                        ]
                    },
                },
            }
        ],
    }


def write_structure_set() -> dict:
    return {
        "structures": [{"structure": "christmas_ginger_house:christmas_ginger_house", "weight": 1}],
        "placement": {
            "type": "minecraft:random_spread",
            "spacing": 150,
            "separation": 100,
            "salt": 49742572,
        },
    }


def write_biome_tag() -> dict:
    return {
        "replace": False,
        "values": [
            "minecraft:snowy_slopes",
            "minecraft:plains",
            "minecraft:snowy_plains",
            "minecraft:snowy_taiga",
            "#minecraft:is_snowy",
        ],
    }


def install_datapack(target: Path, *, use_biome_tag: bool) -> None:
    nbt_src = EXTRACT / "data" / "christmas_ginger_house" / "structures" / "house_ginger.nbt"
    if not nbt_src.exists():
        raise SystemExit(f"Missing NBT at {nbt_src}")

    data = target / "data" / "christmas_ginger_house"
    if data.exists():
        shutil.rmtree(data)

    struct_dir = data / "structures"
    struct_dir.mkdir(parents=True)
    shutil.copy2(nbt_src, struct_dir / "house_ginger.nbt")

    (data / "worldgen" / "structure").mkdir(parents=True)
    (data / "worldgen" / "structure_set").mkdir(parents=True)
    (data / "worldgen" / "template_pool").mkdir(parents=True)

    if use_biome_tag:
        biomes = "#christmas_ginger_house:has_structure/christmas_ginger_house"
        tag_path = data / "tags" / "worldgen" / "biome" / "has_structure" / "christmas_ginger_house.json"
        tag_path.parent.mkdir(parents=True, exist_ok=True)
        tag_path.write_text(json.dumps(write_biome_tag(), indent=2) + "\n", encoding="utf-8")
    else:
        biomes = [
            "minecraft:snowy_slopes",
            "minecraft:plains",
            "minecraft:snowy_plains",
            "minecraft:snowy_taiga",
        ]

    (data / "worldgen" / "structure" / "christmas_ginger_house.json").write_text(
        json.dumps(write_structure_json(biomes), indent=2) + "\n", encoding="utf-8"
    )
    (data / "worldgen" / "template_pool" / "christmas_ginger_house.json").write_text(
        json.dumps(write_template_pool(), indent=2) + "\n", encoding="utf-8"
    )
    (data / "worldgen" / "structure_set" / "christmas_ginger_house.json").write_text(
        json.dumps(write_structure_set(), indent=2) + "\n", encoding="utf-8"
    )

    lang_src = EXTRACT / "assets" / "christmas_ginger_house" / "lang" / "en_us.json"
    assets = target / "assets" / "christmas_ginger_house" / "lang"
    assets.mkdir(parents=True, exist_ok=True)
    lang = {
        "modmenu.nameTranslation.christmas_ginger_house": "Christmas Ginger House",
        "structure.christmas_ginger_house.christmas_ginger_house": "Christmas Ginger House",
    }
    if lang_src.exists():
        try:
            loaded = json.loads(lang_src.read_text(encoding="utf-8"))
            if isinstance(loaded, dict) and loaded:
                lang.update(loaded)
        except json.JSONDecodeError:
            pass
    (assets / "en_us.json").write_text(json.dumps(lang, indent=2) + "\n", encoding="utf-8")


def import_datapack() -> None:
    # shared + older roots: explicit biome list
    for target in [ROOT / "shared" / "resources"] + [
        ROOT / ver / "common" / "src" / "main" / "resources" for ver in ("1.20.1", "1.21.1")
    ]:
        target.mkdir(parents=True, exist_ok=True)
        install_datapack(target, use_biome_tag=False)

    # 26.x: biome tag including #minecraft:is_snowy
    for ver in ("26.1.2", "26.2"):
        target = ROOT / ver / "common" / "src" / "main" / "resources"
        target.mkdir(parents=True, exist_ok=True)
        install_datapack(target, use_biome_tag=True)


def write_oss_docs() -> None:
    (ROOT / "NOTICE").write_text(
        "Christmas Ginger House\n"
        "Copyright 2026 NightBeam Studio\n\n"
        "This product includes software developed at NightBeam Studio.\n"
        "Licensed under the Apache License, Version 2.0.\n",
        encoding="utf-8",
    )
    version_file = ROOT / "VERSION"
    if version_file.exists() or True:
        version_file.write_text("1.1.0\n", encoding="utf-8")
    (ROOT / "CHANGELOG.md").write_text(
        "# Changelog\n\n"
        "## 1.1.0\n\n"
        "- Public Apache-2.0 MultiLoader rewrite from the original Forge 1.20.1 datapack.\n"
        "- Added Minecraft 26.1.2 and 26.2 (Fabric + NeoForge).\n"
        "- Scaffolded 1.20.1 (Fabric + Forge) and 1.21.1 (Fabric + NeoForge).\n"
        "- Replaced MCreator boilerplate with thin loader entrypoints.\n",
        encoding="utf-8",
    )
    (ROOT / "README.md").write_text(
        "# Christmas Ginger House\n\n"
        "A festive gingerbread house structure that generates in snowy and plains biomes.\n\n"
        "## Features\n\n"
        "- Jigsaw worldgen structure (`house_ginger`)\n"
        "- Spawns in snowy slopes, plains, snowy plains, and snowy taiga "
        "(plus `#minecraft:is_snowy` on 26.x)\n"
        "- MultiLoader: Fabric, Forge, and NeoForge depending on Minecraft version\n\n"
        "## Version matrix\n\n"
        "| Root | Minecraft | Loaders |\n"
        "| --- | --- | --- |\n"
        "| `1.20.1/` | 1.20.1 | Fabric, Forge |\n"
        "| `1.21.1/` | 1.21.1 | Fabric, NeoForge |\n"
        "| `26.1.2/` | 26.1.2 | Fabric, NeoForge |\n"
        "| `26.2/` | 26.2 | Fabric, NeoForge |\n\n"
        "## Build\n\n"
        "```bash\n"
        "cd 26.2   # or 26.1.2 / 1.21.1 / 1.20.1\n"
        "./gradlew build\n"
        "```\n\n"
        "JDK 25 is required for 26.x roots. JDK 21 for 1.21.1. JDK 17 for 1.20.1.\n\n"
        "## Publish IDs\n\n"
        "- Modrinth: `Ddgd2w2n` "
        "([the-gingerbread-house](https://modrinth.com/mod/the-gingerbread-house))\n"
        "- CurseForge: `1409805`\n\n"
        "Local upload (26.1.2 and 26.2):\n\n"
        "```bash\n"
        "node scripts/upload_platforms.mjs --workspace 26.1.2 --version 1.1.0\n"
        "node scripts/upload_platforms.mjs --workspace 26.2 --version 1.1.0\n"
        "```\n\n"
        "## Contributing\n\n"
        "See [CONTRIBUTING.md](CONTRIBUTING.md).\n\n"
        "## Security\n\n"
        "See [.github/SECURITY.md](.github/SECURITY.md).\n\n"
        "## License\n\n"
        "Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).\n",
        encoding="utf-8",
    )
    patch = ROOT / "The-Fisherman-House-1.1.0-PatchNotes.md"
    if patch.exists():
        patch.unlink()
    ginger_notes = ROOT / "Christmas-Ginger-House-1.1.0-PatchNotes.md"
    ginger_notes.write_text(
        "# Christmas Ginger House 1.1.0\n\n"
        "- Apache-2.0 OSS MultiLoader release\n"
        "- Minecraft 26.1.2 and 26.2 (Fabric + NeoForge)\n"
        "- Structure datapack ported from Forge 1.20.1 jar\n",
        encoding="utf-8",
    )


def fix_gradle_descriptions() -> None:
    desc = "Adds a festive Christmas Ginger House structure that generates in snowy and plains biomes."
    for ver in VERSIONS:
        props = ROOT / ver / "gradle.properties"
        if not props.exists():
            continue
        lines = []
        for line in props.read_text(encoding="utf-8").splitlines():
            if line.startswith("description="):
                lines.append(f"description={desc}")
            elif line.startswith("version="):
                lines.append("version=1.1.0")
            elif line.startswith("mod_author=") or line.startswith("mod_authors="):
                lines.append("mod_author=NightBeam Studio")
            else:
                lines.append(line)
        props.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> None:
    print("1) text replace")
    for path in ROOT.rglob("*"):
        if not path.is_file():
            continue
        if any(part in {".git", ".gradle", "build", "bin", "run"} for part in path.parts):
            continue
        if path.suffix in {".nbt", ".jar", ".png", ".jpg", ".class", ".zip"}:
            continue
        if path.name == "rebrand_and_import.py":
            continue
        replace_in_file(path)

    print("2) rename packages/files")
    rename_java_packages()
    rename_java_files()

    print("3) clear old datapack")
    clear_fisherman_datapack()

    print("4) import ginger datapack")
    import_datapack()

    print("5) OSS docs + gradle")
    write_oss_docs()
    fix_gradle_descriptions()

    for path in list(ROOT.rglob("*.java")) + list(ROOT.rglob("*.toml")) + list(ROOT.rglob("*.json")):
        if any(part in {".git", ".gradle", "build"} for part in path.parts):
            continue
        if "worldgen" in path.parts or "lang" in path.parts:
            continue  # already written
        if path.suffix == ".json" and path.name not in {
            "fabric.mod.json",
            "christmas_ginger_house.mixins.json",
            "christmasgingerhouse.mixins.json",
        } and "mixins" not in path.name:
            if "services" not in str(path):
                continue
        replace_in_file(path)

    # Ensure Java sources fully rebranded after moves
    for path in ROOT.rglob("*.java"):
        replace_in_file(path)

    print("done")
    r = subprocess.run(
        [
            "rg",
            "-l",
            "fishermanhouse|FishermanHouse|fisherman_house|The Fisherman|B5T1jHAp|1406628|jauml|memory_of_the_past|All Rights Reserved",
            str(ROOT),
            "--glob",
            "!.git/**",
            "--glob",
            "!scripts/rebrand_and_import.py",
            "--glob",
            "!**/build/**",
            "--glob",
            "!**/.gradle/**",
        ],
        capture_output=True,
        text=True,
    )
    leftovers = [l for l in (r.stdout or "").splitlines() if l.strip()]
    print(f"leftover refs: {len(leftovers)}")
    for l in leftovers[:50]:
        print(" ", l)


if __name__ == "__main__":
    main()
