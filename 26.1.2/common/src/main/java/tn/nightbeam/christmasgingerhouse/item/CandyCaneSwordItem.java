package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Iron-tier blade. Every hit gives the wielder a brief sugar rush (Speed I). */
public class CandyCaneSwordItem extends TooltipItem {

    public CandyCaneSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.addEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 3, 0, false, false, true));
        super.hurtEnemy(stack, target, attacker);
    }
}
