package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import javax.annotation.Nullable;
import tn.nightbeam.christmasgingerhouse.block.PresentBlockEntity;

import java.util.List;

/** Present block item. The tooltip never spoils the surprise: it only says who it is from. */
public class PresentItem extends BlockItem {

    public PresentItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean isWrapped(ItemStack stack) {
        CompoundTag tag = getBlockEntityData(stack);
        if (tag == null) {
            return false;
        }
        if (tag.contains("LootTable", Tag.TAG_STRING)) {
            return true;
        }
        ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
        return !items.isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = getBlockEntityData(stack);
        if (isWrapped(stack)) {
            tooltip.add(Component.translatable("block.christmas_ginger_house.present.wrapped").withStyle(ChatFormatting.GOLD));
            if (tag != null && tag.contains(PresentBlockEntity.TAG_SENDER, Tag.TAG_STRING)) {
                tooltip.add(Component.translatable("block.christmas_ginger_house.present.tag",
                        tag.getString(PresentBlockEntity.TAG_SENDER)).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.translatable("block.christmas_ginger_house.present.empty").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }
}
