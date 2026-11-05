package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;
import tn.nightbeam.christmasgingerhouse.registry.ModSounds;

/**
 * Ring to call every reindeer within 24 blocks to your side. Reindeer that hear the bells
 * trot over and gain a short burst of speed, and so does the ringer.
 */
public class SleighBellsItem extends TooltipItem {

    public static final double CALL_RADIUS = 24.0;

    public SleighBellsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SLEIGH_BELLS.get(),
                SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        player.getCooldowns().addCooldown(this, 40);
        if (!level.isClientSide) {
            level.gameEvent(player, GameEvent.INSTRUMENT_PLAY, player.position());
            for (Reindeer reindeer : level.getEntitiesOfClass(Reindeer.class, player.getBoundingBox().inflate(CALL_RADIUS))) {
                reindeer.answerBells(player);
            }
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 6, 0, false, false, true));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
