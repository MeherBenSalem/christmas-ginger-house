package tn.nightbeam.christmasgingerhouse.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;

/**
 * Snowy-biome herd animal. Breeds with carrots, apples or candy canes and comes running to
 * sleigh bells. One in fifty is born with a glowing red nose; naming one "Rudolph" lights it too.
 */
public class Reindeer extends Animal {

    private static final EntityDataAccessor<Boolean> RED_NOSE = SynchedEntityData.defineId(Reindeer.class, EntityDataSerializers.BOOLEAN);
    private static final Ingredient FOOD = Ingredient.of(Items.CARROT, Items.APPLE, Items.GOLDEN_CARROT);
    private static final float RED_NOSE_CHANCE = 0.02F;

    private int answeringBellsTicks;
    @Nullable
    private Player bellRinger;

    public Reindeer(EntityType<? extends Reindeer> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.8));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        goalSelector.addGoal(3, new TemptGoal(this, 1.2, Ingredient.of(Items.CARROT, Items.APPLE, Items.GOLDEN_CARROT, ModItems.CANDY_CANE.get()), false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.15));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(RED_NOSE, false);
    }

    public boolean hasRedNose() {
        return entityData.get(RED_NOSE) || (hasCustomName() && "Rudolph".equalsIgnoreCase(getName().getString()));
    }

    public void setRedNose(boolean redNose) {
        entityData.set(RED_NOSE, redNose);
    }

    /** Called by {@link tn.nightbeam.christmasgingerhouse.item.SleighBellsItem}. */
    public void answerBells(Player player) {
        bellRinger = player;
        answeringBellsTicks = 20 * 8;
        addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 8, 1, false, false));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (answeringBellsTicks > 0) {
            answeringBellsTicks--;
            if (bellRinger != null && bellRinger.isAlive() && distanceToSqr(bellRinger) > 9.0) {
                if (answeringBellsTicks % 10 == 0) {
                    getNavigation().moveTo(bellRinger, 1.4);
                }
                getLookControl().setLookAt(bellRinger, 30.0F, 30.0F);
            } else {
                getNavigation().stop();
                answeringBellsTicks = 0;
                bellRinger = null;
            }
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD.test(stack) || stack.is(ModItems.CANDY_CANE.get());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        Reindeer calf = ModEntities.REINDEER.get().create(level);
        if (calf != null) {
            boolean parentGlows = hasRedNose() || (partner instanceof Reindeer other && other.hasRedNose());
            calf.setRedNose(random.nextFloat() < (parentGlows ? 0.25F : RED_NOSE_CHANCE));
        }
        return calf;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        setRedNose(random.nextFloat() < RED_NOSE_CHANCE);
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("RedNose", entityData.get(RED_NOSE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setRedNose(tag.getBoolean("RedNose"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.LLAMA_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.85F;
    }
}
