package tn.nightbeam.christmasgingerhouse;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import tn.nightbeam.christmasgingerhouse.client.NeoForgeClientSetup;
import tn.nightbeam.christmasgingerhouse.entity.FestiveSpawning;
import tn.nightbeam.christmasgingerhouse.entity.GingerbreadMan;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;
import tn.nightbeam.christmasgingerhouse.platform.NeoForgePlatformHelper;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;

/** Fuel and compost values come from data maps in data/neoforge/data_maps. */
@Mod(Constants.MOD_ID)
public class ChristmasGingerHouseMod {

    public ChristmasGingerHouseMod(IEventBus modBus) {
        ChristmasGingerHouse.init();
        NeoForgePlatformHelper.attach(modBus);
        modBus.addListener(this::onAttributes);
        modBus.addListener(this::onSpawnPlacements);
        modBus.addListener(this::onCommonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeClientSetup.init(modBus);
        }
    }

    private void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GINGERBREAD_MAN.get(), GingerbreadMan.createAttributes().build());
        event.put(ModEntities.REINDEER.get(), Reindeer.createAttributes().build());
        event.put(ModEntities.KRAMPUS.get(), Krampus.createAttributes().build());
    }

    private void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.GINGERBREAD_MAN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FestiveSpawning::checkFestiveCreature, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.REINDEER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FestiveSpawning::checkFestiveCreature, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.KRAMPUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ChristmasGingerHouse::commonSetup);
    }
}
