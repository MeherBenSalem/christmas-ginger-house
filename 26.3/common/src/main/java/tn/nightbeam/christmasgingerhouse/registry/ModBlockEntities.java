package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tn.nightbeam.christmasgingerhouse.block.PresentBlockEntity;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.function.Supplier;

public final class ModBlockEntities {

    public static final Supplier<BlockEntityType<PresentBlockEntity>> PRESENT = Services.PLATFORM.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, "present",
            () -> Services.PLATFORM.createBlockEntityType(PresentBlockEntity::new,
                    ModBlocks.PRESENTS.stream().map(Supplier::get).toArray(Block[]::new)));

    private ModBlockEntities() {
    }

    public static void init() {
    }
}
