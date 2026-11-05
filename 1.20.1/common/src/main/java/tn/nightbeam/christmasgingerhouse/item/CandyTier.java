package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;

public final class CandyTier implements Tier {

    public static final CandyTier INSTANCE = new CandyTier();

    private CandyTier() {
    }

    @Override
    public int getUses() {
        return 420;
    }

    @Override
    public float getSpeed() {
        return 6.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 2.0F;
    }

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public int getEnchantmentValue() {
        return 18;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.CANDY_CANE.get());
    }
}
