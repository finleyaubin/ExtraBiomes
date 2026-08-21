package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.winepicfin.extrabiomes.entity.ai.GiantTortoiseChargeGoal;

// Ported from Bedrock extrabiomes:giant_tortoise — large, slow, amphibious monster that
// charges (ram_attack) and melees (attack: 10 damage) players, golems and the warden on sight.
public class GiantTortoiseEntity extends Monster {
    private static final EntityDataAccessor<Boolean> DATA_CHARGING =
            SynchedEntityData.defineId(GiantTortoiseEntity.class, EntityDataSerializers.BOOLEAN);

    public GiantTortoiseEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CHARGING, false);
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(DATA_CHARGING, charging);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, GiantTortoiseTuning.MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, GiantTortoiseTuning.MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, GiantTortoiseTuning.ATTACK_DAMAGE)
                .add(Attributes.ATTACK_KNOCKBACK, GiantTortoiseTuning.ATTACK_KNOCKBACK)
                .add(Attributes.KNOCKBACK_RESISTANCE, GiantTortoiseTuning.KNOCKBACK_RESISTANCE)
                .add(Attributes.FOLLOW_RANGE, GiantTortoiseTuning.FOLLOW_RANGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new GiantTortoiseChargeGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0, 10));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        // Bedrock also targets iron golems, snow golems and the warden — add here if/when those
        // entity classes are available to reference, e.g.:
        // this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
