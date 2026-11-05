package tn.nightbeam.christmasgingerhouse.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.function.Supplier;

public final class ModDataComponents {

    /** Name of the player who wrapped a present; travels between the block and the item. */
    public static final Supplier<DataComponentType<String>> SENDER = Services.PLATFORM.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, "sender",
            () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    private ModDataComponents() {
    }

    public static void init() {
    }
}
