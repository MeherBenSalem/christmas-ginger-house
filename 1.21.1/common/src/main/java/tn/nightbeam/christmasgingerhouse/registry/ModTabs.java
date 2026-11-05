package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.function.Supplier;

public final class ModTabs {

    public static final Supplier<CreativeModeTab> CHRISTMAS = Services.PLATFORM.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, "christmas",
            () -> Services.PLATFORM.creativeTabBuilder()
                    .title(Component.translatable("itemGroup.christmas_ginger_house.christmas"))
                    .icon(() -> new ItemStack(ModItems.GINGERBREAD_COOKIE.get()))
                    .displayItems((params, output) -> ModItems.TAB_ORDER.forEach(item -> output.accept(item.get())))
                    .build());

    private ModTabs() {
    }

    public static void init() {
    }
}
