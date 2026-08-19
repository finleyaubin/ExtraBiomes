package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
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
import net.winepicfin.extrabiomes.entity.ai.PiranhaBaitGoal;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;
import org.jetbrains.annotations.Nullable;

// Ported from Bedrock extrabiomes:piranha — aggressive schooling fish that bites players in water.
public class PiranhaEntity extends WaterAnimal implements Enemy {
    public static final int VARIANT_COUNT = 3;
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(PiranhaEntity.class, EntityDataSerializers.INT);

    // Ported from Bedrock's controller.animation.piranha.general "bite" state (query.has_target) —
    // the jaw chomps whenever the piranha has something to attack, a player or a chased bait alike.
    @Nullable
    private BaitProjectileEntity chasedBait;

    public PiranhaEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.1F, 0.5F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PiranhaBaitGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.2, 20));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        // Ported from Bedrock's nearest_attackable_target "is_family mob && != fish" entry —
        // piranhas also go after any other mob that wanders into the water, not just players.
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                (LivingEntity target) -> !(target instanceof WaterAnimal) && target.isInWater()));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public void setChasedBait(@Nullable BaitProjectileEntity bait) {
        this.chasedBait = bait;
    }

    // Drives the jaw-bite animation client-side — true while pursuing bait even if no player is
    // currently aggroed, matching Bedrock's "bite" state being independent of the true attack target.
    public boolean isBiting() {
        return this.getTarget() != null || this.chasedBait != null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("Variant"));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.setVariant(this.random.nextInt(VARIANT_COUNT));
        return super.finalizeSpawn(level, difficulty, type, data, tag);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
}
