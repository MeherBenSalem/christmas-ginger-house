package tn.nightbeam.christmasgingerhouse;

import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import tn.nightbeam.christmasgingerhouse.client.ForgeClientSetup;
import tn.nightbeam.christmasgingerhouse.entity.FestiveSpawning;
import tn.nightbeam.christmasgingerhouse.entity.GingerbreadMan;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;
import tn.nightbeam.christmasgingerhouse.item.FuelItem;
import tn.nightbeam.christmasgingerhouse.platform.ForgePlatformHelper;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;

@Mod(Constants.MOD_ID)
public class ChristmasGingerHouseMod {

    public ChristmasGingerHouseMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ChristmasGingerHouse.init();
        ForgePlatformHelper.attach(modBus);

        modBus.addListener(this::onAttributes);
        modBus.addListener(this::onSpawnPlacements);
        modBus.addListener(this::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onFuel);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClientSetup.init(modBus);
        }
    }

    private void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GINGERBREAD_MAN.get(), GingerbreadMan.createAttributes().build());
        event.put(ModEntities.REINDEER.get(), Reindeer.createAttributes().build());
        event.put(ModEntities.KRAMPUS.get(), Krampus.createAttributes().build());
    }

    private void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.GINGERBREAD_MAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FestiveSpawning::checkFestiveCreature, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.REINDEER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FestiveSpawning::checkFestiveCreature, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.KRAMPUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ChristmasGingerHouse::commonSetup);
    }

    private void onFuel(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() instanceof FuelItem fuel) {
            event.setBurnTime(fuel.burnTime());
        }
    }
}
