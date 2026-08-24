package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.winepicfin.extrabiomes.entity.ai.PiranhaBaitGoal;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;
import org.jetbrains.annotations.Nullable;

// Ported from Bedrock extrabiomes:piranha — aggressive schooling fish that bites players in water.
public class PiranhaEntity extends WaterAnimal implements Enemy {
    public static final int VARIANT_COUNT = 3;
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(PiranhaEntity.class, EntityDataSerializers.INT);
    // Separately-rolled size axis, independent of DATA_VARIANT's texture (see PiranhaTuning).
    private static final EntityDataAccessor<Float> DATA_SIZE =
            SynchedEntityData.defineId(PiranhaEntity.class, EntityDataSerializers.FLOAT);
    // Targets and goal state only exist server-side, so the bite state must be synced explicitly for the client's jaw animation to play.
    private static final EntityDataAccessor<Boolean> DATA_BITING =
            SynchedEntityData.defineId(PiranhaEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private BaitProjectileEntity chasedBait;

    public PiranhaEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10,
                PiranhaTuning.IN_WATER_SPEED_MODIFIER, PiranhaTuning.OUT_OF_WATER_SPEED_MODIFIER, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // finalizeSpawn overrides these base values once the rolled scale is known.
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, healthForScale(PiranhaTuning.SIZE_NORMAL_SCALE))
                .add(Attributes.MOVEMENT_SPEED, PiranhaTuning.MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, attackDamageForScale(PiranhaTuning.SIZE_NORMAL_SCALE))
                .add(Attributes.FOLLOW_RANGE, PiranhaTuning.FOLLOW_RANGE);
    }

    private static double lerp(float scale, double atMin, double atMax) {
        float fraction = Mth.clamp((scale - PiranhaTuning.SIZE_MIN_SCALE)
                / (PiranhaTuning.SIZE_MAX_SCALE - PiranhaTuning.SIZE_MIN_SCALE), 0.0F, 1.0F);
        return Mth.lerp(fraction, atMin, atMax);
    }

    private static double healthForScale(float scale) {
        return lerp(scale, PiranhaTuning.HEALTH_AT_MIN_SCALE, PiranhaTuning.HEALTH_AT_MAX_SCALE);
    }

    private static double attackDamageForScale(float scale) {
        return lerp(scale, PiranhaTuning.ATTACK_DAMAGE_AT_MIN_SCALE, PiranhaTuning.ATTACK_DAMAGE_AT_MAX_SCALE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PiranhaBaitGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, PiranhaTuning.MELEE_ATTACK_SPEED, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, PiranhaTuning.RANDOM_SWIM_SPEED, 20));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        // Piranhas also go after any other mob that wanders into the water, not just players (Bedrock's "is_family mob && != fish" entry).
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                (LivingEntity target) -> !(target instanceof WaterAnimal) && target.isInWater()));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    // WaterAnimal#travel ignores generic.movement_speed entirely (fixed 0.02 accel), which is why piranhas crawled; overridden the same way vanilla Dolphin does, off getSpeed().
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    public void setChasedBait(@Nullable BaitProjectileEntity bait) {
        this.chasedBait = bait;
    }

    @Override
    public void aiStep() {
        // Flop behavior copied from AbstractFish rather than inherited, since extending it would drag in bucket-catching and passive panic/avoid-player goals.
        if (!this.isInWater() && this.onGround() && this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add(
                    (this.random.nextFloat() * 2.0F - 1.0F) * 0.05F, 0.4F,
                    (this.random.nextFloat() * 2.0F - 1.0F) * 0.05F));
            this.setOnGround(false);
            this.hasImpulse = true;
            this.playSound(SoundEvents.COD_FLOP, this.getSoundVolume(), this.getVoicePitch());
        }

        super.aiStep();
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_BITING, this.getTarget() != null || this.chasedBait != null);
        }
    }

    // True while pursuing bait even with no player aggroed, matching Bedrock's bite state being independent of the true attack target.
    public boolean isBiting() {
        return this.entityData.get(DATA_BITING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_SIZE, PiranhaTuning.SIZE_NORMAL_SCALE);
        this.entityData.define(DATA_BITING, false);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public float getSizeScale() {
        return this.entityData.get(DATA_SIZE);
    }

    // Re-applied on NBT load too, since AttributeSupplier base values are static per-EntityType.
    private void setSizeScale(float scale) {
        this.entityData.set(DATA_SIZE, scale);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(healthForScale(scale));
        this.setHealth((float) this.getAttributeValue(Attributes.MAX_HEALTH));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackDamageForScale(scale));
        this.refreshDimensions();
    }

    @Override
    public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {
        return super.getDimensions(pose).scale(this.getSizeScale());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
        tag.putFloat("Size", this.getSizeScale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("Variant"));
        if (tag.contains("Size")) {
            this.setSizeScale(tag.getFloat("Size"));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.setVariant(this.random.nextInt(VARIANT_COUNT));
        this.setSizeScale(rollWeightedScale(this.random));
        return super.finalizeSpawn(level, difficulty, type, data, tag);
    }

    // Band wins by SIZE_BAND_WEIGHTS, exact scale is uniform random within the winning band.
    private static float rollWeightedScale(net.minecraft.util.RandomSource random) {
        int totalWeight = 0;
        for (int weight : PiranhaTuning.SIZE_BAND_WEIGHTS) {
            totalWeight += weight;
        }
        int roll = random.nextInt(totalWeight);
        if (roll < PiranhaTuning.SIZE_BAND_WEIGHTS[0]) {
            return Mth.lerp(random.nextFloat(), PiranhaTuning.SIZE_MIN_SCALE, PiranhaTuning.SMALL_BAND_MAX);
        } else if (roll < PiranhaTuning.SIZE_BAND_WEIGHTS[0] + PiranhaTuning.SIZE_BAND_WEIGHTS[1]) {
            return Mth.lerp(random.nextFloat(), PiranhaTuning.SMALL_BAND_MAX, PiranhaTuning.LARGE_BAND_MIN);
        } else {
            return Mth.lerp(random.nextFloat(), PiranhaTuning.LARGE_BAND_MIN, PiranhaTuning.SIZE_MAX_SCALE);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
}
