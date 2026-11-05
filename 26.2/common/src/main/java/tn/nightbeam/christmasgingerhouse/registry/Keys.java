package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tn.nightbeam.christmasgingerhouse.Constants;

/** Since 1.21.2 items, blocks and entity types must know their registry key when constructed. */
public final class Keys {

    private static final ThreadLocal<String> BUILDING = new ThreadLocal<>();

    private Keys() {
    }

    /** Runs {@code factory} with {@code name} as the entry being built, so property helpers can stamp its key. */
    public static <T> java.util.function.Supplier<T> building(String name, java.util.function.Supplier<T> factory) {
        return () -> {
            String outer = BUILDING.get();
            BUILDING.set(name);
            try {
                return factory.get();
            } finally {
                BUILDING.set(outer);
            }
        };
    }

    private static String current() {
        String name = BUILDING.get();
        if (name == null) {
            throw new IllegalStateException("No registry entry is being built");
        }
        return name;
    }

    /** Item properties carrying the key of the item being built. */
    public static Item.Properties itemProps() {
        return new Item.Properties().setId(item(current()));
    }

    /** Block properties carrying the key of the block being built. */
    public static BlockBehaviour.Properties blockProps() {
        return BlockBehaviour.Properties.of().setId(block(current()));
    }

    public static ResourceKey<Item> item(String name) {
        return ResourceKey.create(Registries.ITEM, Constants.id(name));
    }

    public static ResourceKey<Block> block(String name) {
        return ResourceKey.create(Registries.BLOCK, Constants.id(name));
    }

    public static ResourceKey<EntityType<?>> entity(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Constants.id(name));
    }

    public static Item.Properties item(String name, Item.Properties properties) {
        return properties.setId(item(name));
    }

    public static BlockBehaviour.Properties block(String name, BlockBehaviour.Properties properties) {
        return properties.setId(block(name));
    }
}
