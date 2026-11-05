package tn.nightbeam.christmasgingerhouse.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Registers {@code factory} under {@code christmas_ginger_house:name}. Fabric registers
     * immediately, Forge defers to the matching registry event.
     */
    <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory);




    /** Vanilla's tab output type is protected, so building the tab's contents happens in loader code. */
    CreativeModeTab createTab(Component title, Supplier<ItemStack> icon, Supplier<List<ItemStack>> items);

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);
}
