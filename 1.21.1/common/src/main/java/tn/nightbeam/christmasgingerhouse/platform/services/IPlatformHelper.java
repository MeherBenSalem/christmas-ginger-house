package tn.nightbeam.christmasgingerhouse.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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

    /** Like {@link #register} but returns a registry holder, needed for armor materials. */
    <T> Holder<T> registerForHolder(Registry<T> registry, String name, Supplier<T> factory);

    Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight, Item.Properties properties);

    /** Armor items; the hat gets a custom model on the client. */
    Item createFestiveArmor(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties);

    CreativeModeTab.Builder creativeTabBuilder();

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);
}
