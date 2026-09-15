package tn.nightbeam.christmasgingerhouse;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ChristmasGingerHouseMod {

    public ChristmasGingerHouseMod(IEventBus eventBus) {
        ChristmasGingerHouse.init();
    }
}
