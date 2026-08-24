package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.ai.TreefrogHopGoal;
import org.jetbrains.annotations.Nullable;

// Ported from Bedrock extrabiomes:treefrog — small hopping passive frog. Drops frogs_legs.
public class TreefrogEntity extends Animal {
    public TreefrogEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, TreefrogTuning.MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, TreefrogTuning.MOVEMENT_SPEED)
                .add(Attributes.JUMP_STRENGTH, TreefrogTuning.JUMP_STRENGTH)
                .add(Attributes.FOLLOW_RANGE, TreefrogTuning.FOLLOW_RANGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new TreefrogHopGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    // JUMP_STRENGTH only affects horses by default — read it directly so createAttributes() actually controls hop height.
    @Override
    protected float getJumpPower() {
        return (float) this.getAttributeValue(Attributes.JUMP_STRENGTH);
    }

    // Subtracts a flat 12 (Bedrock's modifier) rather than cancelling fall damage outright like vanilla Frog does, so hopping is safe but long drops stay lethal.
    @Override
    protected int calculateFallDamage(float distance, float multiplier) {
        return Math.max(0, super.calculateFallDamage(distance, multiplier) + TreefrogTuning.FALL_DAMAGE_MODIFIER);
    }

    public void hop(double vx, double vz) {
        this.setDeltaMovement(vx, this.getJumpPower(), vz);
        this.hasImpulse = true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.FROG_HURT;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }
}
