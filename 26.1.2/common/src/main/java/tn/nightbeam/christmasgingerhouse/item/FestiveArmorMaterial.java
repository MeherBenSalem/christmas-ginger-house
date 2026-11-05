package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import tn.nightbeam.christmasgingerhouse.Constants;

import java.util.EnumMap;
import java.util.Map;

/**
 * Knitted holiday wear: leather-grade protection, wool repairs, and immunity to powder snow (via tag).
 * The look comes from equipment assets (assets/christmas_ginger_house/equipment/*.json).
 */
public final class FestiveArmorMaterial {

    public static final TagKey<Item> REPAIRS = TagKey.create(Registries.ITEM, Constants.id("festive_armor_repairs"));
    public static final ArmorMaterial SANTA_HAT = material("santa_hat");
    public static final ArmorMaterial FESTIVE = material("festive");

    private FestiveArmorMaterial() {
    }

    private static ArmorMaterial material(String asset) {
        Map<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
        defense.put(ArmorType.HELMET, 1);
        defense.put(ArmorType.CHESTPLATE, 3);
        defense.put(ArmorType.LEGGINGS, 2);
        defense.put(ArmorType.BOOTS, 1);
        defense.put(ArmorType.BODY, 3);
        ResourceKey<EquipmentAsset> key = ResourceKey.create(EquipmentAssets.ROOT_ID, Constants.id(asset));
        return new ArmorMaterial(5, defense, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, REPAIRS, key);
    }

    public static void init() {
    }
}
