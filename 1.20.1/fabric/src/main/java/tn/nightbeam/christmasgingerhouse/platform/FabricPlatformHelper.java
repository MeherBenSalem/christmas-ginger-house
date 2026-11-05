package tn.nightbeam.christmasgingerhouse.platform;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.SpawnEggItem;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.platform.services.IPlatformHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory) {
        T value = Registry.register(registry, Constants.id(name), factory.get());
        return () -> value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight, Item.Properties properties) {
        return new SpawnEggItem((EntityType<? extends Mob>) type.get(), background, highlight, properties);
    }

    @Override
    public Item createRecord(int comparatorOutput, Supplier<SoundEvent> sound, Item.Properties properties, int lengthInSeconds) {
        return new RecordItem(comparatorOutput, sound.get(), properties, lengthInSeconds);
    }

    @Override
    public Item createFestiveArmor(ArmorMaterial material, ArmorItem.Type type, Item.Properties properties) {
        return new ArmorItem(material, type, properties);
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return FabricItemGroup.builder();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::apply, blocks).build(null);
    }
}
