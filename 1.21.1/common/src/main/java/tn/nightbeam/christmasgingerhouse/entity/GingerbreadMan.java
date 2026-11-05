package tn.nightbeam.christmasgingerhouse.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;
import tn.nightbeam.christmasgingerhouse.registry.ModSounds;

/**
 * Run, run, as fast as you can. A skittish little cookie that lives in snowy biomes, follows
 * anyone holding icing or sugar, and bolts from foxes, wolves and anything that hurts it.
 */
public class GingerbreadMan extends PathfinderMob {

    public GingerbreadMan(EntityType<? extends GingerbreadMan> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.75));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Fox.class, 8.0F, 1.3, 1.7));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Wolf.class, 8.0F, 1.3, 1.7));
        goalSelector.addGoal(3, new TemptGoal(this, 1.15, stack -> stack.is(ModItems.ICING.get()) || stack.is(Items.SUGAR), false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public int getAmbientSoundInterval() {
        return 260;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GINGERBREAD_MAN_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GINGERBREAD_MAN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GINGERBREAD_MAN_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.WOOD_STEP, 0.08F, 1.6F);
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.1F;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
