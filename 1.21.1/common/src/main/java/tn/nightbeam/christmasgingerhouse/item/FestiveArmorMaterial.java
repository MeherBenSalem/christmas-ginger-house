package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.EnumMap;
import java.util.List;

/**
 * Knitted holiday wear: leather-grade protection, wool repairs, and immunity to powder snow (via tag).
 * The hat and the sweater use separate materials so each points at its own texture.
 */
public final class FestiveArmorMaterial {

    public static final Holder<ArmorMaterial> SANTA_HAT = register("santa_hat");
    public static final Holder<ArmorMaterial> FESTIVE = register("festive");

    private FestiveArmorMaterial() {
    }

    private static Holder<ArmorMaterial> register(String name) {
        return Services.PLATFORM.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, name, () -> {
            EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
            defense.put(ArmorItem.Type.HELMET, 1);
            defense.put(ArmorItem.Type.CHESTPLATE, 3);
            defense.put(ArmorItem.Type.LEGGINGS, 2);
            defense.put(ArmorItem.Type.BOOTS, 1);
            defense.put(ArmorItem.Type.BODY, 3);
            return new ArmorMaterial(defense, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(Items.WHITE_WOOL, Items.RED_WOOL, Items.GREEN_WOOL),
                    List.of(new ArmorMaterial.Layer(Constants.id(name))), 0.0F, 0.0F);
        });
    }

    public static void init() {
    }
}
