package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import tn.nightbeam.christmasgingerhouse.Constants;

/** Knitted holiday wear: leather-grade protection, wool repairs, and immunity to powder snow (via tag). */
public enum FestiveArmorMaterial implements ArmorMaterial {
    FESTIVE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 88;
            case CHESTPLATE -> 128;
            case LEGGINGS -> 120;
            case BOOTS -> 104;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 1;
            case CHESTPLATE -> 3;
            case LEGGINGS -> 2;
            case BOOTS -> 1;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.WHITE_WOOL, Items.RED_WOOL, Items.GREEN_WOOL);
    }

    @Override
    public String getName() {
        return Constants.MOD_ID + ":festive";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
