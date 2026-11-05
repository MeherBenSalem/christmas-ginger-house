package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import tn.nightbeam.christmasgingerhouse.Constants;

/** Iron-grade candy: 420 uses, repaired with candy canes (item tag {@code candy_cane_repairs}). */
public final class CandyTier {

    public static final TagKey<Item> REPAIRS = TagKey.create(Registries.ITEM, Constants.id("candy_cane_repairs"));
    public static final ToolMaterial MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 420, 6.0F, 2.0F, 18, REPAIRS);

    private CandyTier() {
    }
}
