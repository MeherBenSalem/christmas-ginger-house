package tn.nightbeam.christmasgingerhouse;

import tn.nightbeam.christmasgingerhouse.platform.Services;
import tn.nightbeam.christmasgingerhouse.registry.ModBlockEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModBlocks;
import tn.nightbeam.christmasgingerhouse.registry.ModDataComponents;
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
        ModDataComponents.init();
        tn.nightbeam.christmasgingerhouse.item.FestiveArmorMaterial.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModEntities.init();
        ModItems.init();
        ModTabs.init();
    }

    public static void commonSetup() {
        // 26.3: compost chances are the items' compostable component, set in ModItems.
    }
}
