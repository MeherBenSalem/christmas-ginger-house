package tn.nightbeam.christmasgingerhouse.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import tn.nightbeam.christmasgingerhouse.Constants;

/** Spawn rules and the biome tags that decide where everything appears. All of it is data-pack overridable. */
public final class FestiveSpawning {

    public static final TagKey<Block> SPAWNABLE_ON = TagKey.create(Registries.BLOCK, Constants.id("festive_mobs_spawnable_on"));
    public static final TagKey<Biome> SPAWNS_GINGERBREAD_MEN = biome("spawns_gingerbread_men");
    public static final TagKey<Biome> SPAWNS_REINDEER = biome("spawns_reindeer");
    public static final TagKey<Biome> SPAWNS_KRAMPUS = biome("spawns_krampus");
    public static final TagKey<Biome> HAS_WILD_GINGER = biome("has_wild_ginger");

    private FestiveSpawning() {
    }

    private static TagKey<Biome> biome(String name) {
        return TagKey.create(Registries.BIOME, Constants.id(name));
    }

    /** Creatures: on snow or grass, in daylight-level light. */
    public static <T extends Mob> boolean checkFestiveCreature(EntityType<T> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(SPAWNABLE_ON) && level.getRawBrightness(pos, 0) > 8;
    }
}
