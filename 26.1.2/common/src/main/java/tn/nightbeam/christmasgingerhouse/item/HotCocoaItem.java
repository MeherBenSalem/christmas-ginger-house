package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A warming mug: drinking it thaws the drinker. Sounds, animation and the bowl come from its consumable. */
public class HotCocoaItem extends TooltipItem {

    public HotCocoaItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        entity.setTicksFrozen(0);
        entity.clearFire();
        return super.finishUsingItem(stack, level, entity);
    }
}
