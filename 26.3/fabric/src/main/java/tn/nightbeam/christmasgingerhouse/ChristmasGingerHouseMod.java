package tn.nightbeam.christmasgingerhouse;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import tn.nightbeam.christmasgingerhouse.entity.FestiveSpawning;
import tn.nightbeam.christmasgingerhouse.entity.GingerbreadMan;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;

public class ChristmasGingerHouseMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ChristmasGingerHouse.init();

        FabricDefaultAttributeRegistry.register(ModEntities.GINGERBREAD_MAN.get(), GingerbreadMan.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.REINDEER.get(), Reindeer.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.KRAMPUS.get(), Krampus.createAttributes());

        SpawnPlacements.register(ModEntities.GINGERBREAD_MAN.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FestiveSpawning::checkFestiveCreature);
        SpawnPlacements.register(ModEntities.REINDEER.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FestiveSpawning::checkFestiveCreature);
        SpawnPlacements.register(ModEntities.KRAMPUS.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);

        // Mirrors data/christmas_ginger_house/neoforge/biome_modifier on NeoForge. Biomes come from tags, so packs can retarget them.
        addSpawn("gingerbread_men_spawns", FestiveSpawning.SPAWNS_GINGERBREAD_MEN, MobCategory.CREATURE, ModEntities.GINGERBREAD_MAN.get(), 6, 2, 3);
        addSpawn("reindeer_spawns", FestiveSpawning.SPAWNS_REINDEER, MobCategory.CREATURE, ModEntities.REINDEER.get(), 8, 2, 4);
        addSpawn("krampus_spawns", FestiveSpawning.SPAWNS_KRAMPUS, MobCategory.MONSTER, ModEntities.KRAMPUS.get(), 4, 1, 1);
        BiomeModifications.addFeature(BiomeSelectors.tag(FestiveSpawning.HAS_WILD_GINGER), GenerationStep.Decoration.VEGETAL_DECORATION,
                ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("patch_wild_ginger")));

        // Naughty coal's burn time is the vanilla cooking_fuel item component on 26.3 (see FuelItem).

        ChristmasGingerHouse.commonSetup();
    }

    /** 26.3 spawner entries carry their group size as an int provider. */
    private static void addSpawn(String name, TagKey<Biome> biomes, MobCategory category, EntityType<?> type, int weight, int min, int max) {
        BiomeModifications.create(Constants.id(name)).add(ModificationPhase.ADDITIONS, BiomeSelectors.tag(biomes),
                (selection, context) -> context.getMobSpawnSettings().addSpawn(category,
                        new MobSpawnSettings.SpawnerData(type, UniformInt.of(min, max)), weight));
    }
}
