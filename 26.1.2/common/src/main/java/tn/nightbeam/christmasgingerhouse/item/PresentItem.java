package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import tn.nightbeam.christmasgingerhouse.registry.ModDataComponents;

import java.util.function.Consumer;

/** Present block item. The tooltip never spoils the surprise: it only says who it is from. */
public class PresentItem extends BlockItem {

    public PresentItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean isWrapped(ItemStack stack) {
        if (stack.has(DataComponents.CONTAINER_LOOT)) {
            return true;
        }
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        return contents != null && contents.nonEmptyItemCopyStream().findAny().isPresent();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        if (isWrapped(stack)) {
            tooltip.accept(Component.translatable("block.christmas_ginger_house.present.wrapped").withStyle(ChatFormatting.GOLD));
            String sender = stack.get(ModDataComponents.SENDER.get());
            if (sender != null) {
                tooltip.accept(Component.translatable("block.christmas_ginger_house.present.tag", sender).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.accept(Component.translatable("block.christmas_ginger_house.present.empty").withStyle(ChatFormatting.GRAY));
        }
    }
}
