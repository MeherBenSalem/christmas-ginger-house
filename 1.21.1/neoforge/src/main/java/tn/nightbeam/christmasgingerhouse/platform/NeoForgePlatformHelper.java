package tn.nightbeam.christmasgingerhouse.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.platform.services.IPlatformHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new LinkedHashMap<>();

    /** Hooks every deferred register created so far onto the mod event bus. */
    public static void attach(IEventBus modBus) {
        REGISTERS.values().forEach(register -> register.register(modBus));
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @SuppressWarnings("unchecked")
    private static <R> DeferredRegister<R> registerFor(Registry<R> registry) {
        return (DeferredRegister<R>) REGISTERS.computeIfAbsent(registry.key(),
                key -> DeferredRegister.create(registry.key(), Constants.MOD_ID));
    }

    @Override
    public <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory) {
        return registerFor(registry).register(name, factory);
    }

    @Override
    public <T> Holder<T> registerForHolder(Registry<T> registry, String name, Supplier<T> factory) {
        return registerFor(registry).register(name, factory);
    }

    @Override
    public Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight, Item.Properties properties) {
        return new DeferredSpawnEggItem(type, background, highlight, properties);
    }

    @Override
    public Item createFestiveArmor(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties) {
        return new ArmorItem(material, type, properties);
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::apply, blocks).build(null);
    }
}
