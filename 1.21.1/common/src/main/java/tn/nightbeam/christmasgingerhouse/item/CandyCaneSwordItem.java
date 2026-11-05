package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Iron-tier blade. Every hit gives the wielder a brief sugar rush (Speed I). */
public class CandyCaneSwordItem extends SwordItem {

    public CandyCaneSwordItem(Properties properties) {
        super(CandyTier.INSTANCE, properties.attributes(SwordItem.createAttributes(CandyTier.INSTANCE, 3, -2.4F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 3, 0, false, false, true));
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
