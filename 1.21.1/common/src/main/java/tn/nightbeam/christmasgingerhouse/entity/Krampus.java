package tn.nightbeam.christmasgingerhouse.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tn.nightbeam.christmasgingerhouse.registry.ModSounds;

/**
 * The shadow of the season. A rare night stalker in snowy biomes: his chains slow whoever he
 * strikes, he rattles them when he has your scent, and he carries the naughty list's coal.
 */
public class Krampus extends Monster {

    private int chainRattleCooldown;

    public Krampus(EntityType<? extends Krampus> type, Level level) {
        super(type, level);
        xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            if (chainRattleCooldown > 0) {
                chainRattleCooldown--;
            } else if (getTarget() != null) {
                playSound(ModSounds.KRAMPUS_CHAINS.get(), 1.2F, 0.9F + random.nextFloat() * 0.2F);
                chainRattleCooldown = 60 + random.nextInt(80);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            int seconds = level().getDifficulty() == Difficulty.HARD ? 4 : 2;
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, seconds * 20, 1), this);
            playSound(ModSounds.KRAMPUS_CHAINS.get(), 1.0F, 1.1F);
        }
        return hit;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.KRAMPUS_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.KRAMPUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.KRAMPUS_DEATH.get();
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(SoundEvents.GOAT_STEP, 0.4F, 0.7F);
    }
}
