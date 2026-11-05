package tn.nightbeam.christmasgingerhouse;

import net.minecraft.world.level.block.ComposterBlock;
import tn.nightbeam.christmasgingerhouse.platform.Services;
import tn.nightbeam.christmasgingerhouse.registry.ModBlockEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModBlocks;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;
import tn.nightbeam.christmasgingerhouse.registry.ModSounds;
import tn.nightbeam.christmasgingerhouse.registry.ModTabs;

/**
 * Loader-agnostic entry point. Each loader calls {@link #init()} from its mod constructor or
 * initializer, then {@link #commonSetup()} once registries are frozen.
 */
public final class ChristmasGingerHouse {

    private ChristmasGingerHouse() {
    }

    public static void init() {
        Constants.LOG.info("Decking the halls on {}", Services.PLATFORM.getPlatformName());
        // Order matters on Fabric, where registration is immediate.
        ModSounds.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModEntities.init();
        ModItems.init();
        ModTabs.init();
    }

    public static void commonSetup() {
        ComposterBlock.COMPOSTABLES.put(ModItems.GINGER_ROOT.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.GINGERBREAD_DOUGH.get(), 0.5F);
        ComposterBlock.COMPOSTABLES.put(ModItems.GINGERBREAD.get(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModItems.GINGERBREAD_COOKIE.get(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(ModItems.WILD_GINGER.get(), 0.65F);
    }
}
