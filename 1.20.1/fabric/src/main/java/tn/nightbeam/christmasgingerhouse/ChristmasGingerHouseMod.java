package tn.nightbeam.christmasgingerhouse;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import tn.nightbeam.christmasgingerhouse.entity.FestiveSpawning;
import tn.nightbeam.christmasgingerhouse.entity.GingerbreadMan;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;
import tn.nightbeam.christmasgingerhouse.item.FuelItem;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;

public class ChristmasGingerHouseMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ChristmasGingerHouse.init();

        FabricDefaultAttributeRegistry.register(ModEntities.GINGERBREAD_MAN.get(), GingerbreadMan.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.REINDEER.get(), Reindeer.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.KRAMPUS.get(), Krampus.createAttributes());

        SpawnPlacements.register(ModEntities.GINGERBREAD_MAN.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FestiveSpawning::checkFestiveCreature);
        SpawnPlacements.register(ModEntities.REINDEER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FestiveSpawning::checkFestiveCreature);
        SpawnPlacements.register(ModEntities.KRAMPUS.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);

        // Mirrors data/christmas_ginger_house/forge/biome_modifier on Forge. Biomes come from tags, so packs can retarget them.
        BiomeModifications.addSpawn(BiomeSelectors.tag(FestiveSpawning.SPAWNS_GINGERBREAD_MEN), MobCategory.CREATURE,
                ModEntities.GINGERBREAD_MAN.get(), 6, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(FestiveSpawning.SPAWNS_REINDEER), MobCategory.CREATURE,
                ModEntities.REINDEER.get(), 8, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.tag(FestiveSpawning.SPAWNS_KRAMPUS), MobCategory.MONSTER,
                ModEntities.KRAMPUS.get(), 4, 1, 1);
        BiomeModifications.addFeature(BiomeSelectors.tag(FestiveSpawning.HAS_WILD_GINGER), GenerationStep.Decoration.VEGETAL_DECORATION,
                ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("patch_wild_ginger")));

        FuelItem coal = (FuelItem) ModItems.NAUGHTY_COAL.get();
        FuelRegistry.INSTANCE.add(coal, coal.burnTime());

        ChristmasGingerHouse.commonSetup();
    }
}
