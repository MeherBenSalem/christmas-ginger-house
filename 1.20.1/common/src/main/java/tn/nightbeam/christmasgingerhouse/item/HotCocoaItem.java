package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** A warming mug: drinking it thaws the drinker and hands back the bowl. */
public class HotCocoaItem extends TooltipItem {

    public HotCocoaItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        entity.setTicksFrozen(0);
        entity.clearFire();
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return result;
        }
        if (result.isEmpty()) {
            return new ItemStack(Items.BOWL);
        }
        if (entity instanceof Player player && !player.getInventory().add(new ItemStack(Items.BOWL))) {
            player.drop(new ItemStack(Items.BOWL), false);
        }
        return result;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }
}
