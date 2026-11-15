package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
    private static final float SWIM_POSE_BLEND_PER_TICK = 0.15F;
    // Keeps a pond of frogs from stroking in unison.
    private static final int STROKE_STAGGER_TICKS = 7;

    private float verticalSpeed;
    private float lastVerticalSpeed;
    private float swimAmount;
    private float lastSwimAmount;

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
    protected int calculateFallDamage(double distance, float multiplier) {
        return Math.max(0, super.calculateFallDamage(distance, multiplier) + TreefrogTuning.FALL_DAMAGE_MODIFIER);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            trackAnimationState();
        }
    }

    // Measured from the motion actually shown, since the client only hears the server's velocity every few ticks.
    private void trackAnimationState() {
        this.lastVerticalSpeed = this.verticalSpeed;
        this.verticalSpeed = (float) (this.getY() - this.yo) * SharedConstants.TICKS_PER_SECOND;

        boolean swimming = this.isInWater() && !this.onGround();
        this.lastSwimAmount = this.swimAmount;
        this.swimAmount = Mth.clamp(this.swimAmount + (swimming ? SWIM_POSE_BLEND_PER_TICK : -SWIM_POSE_BLEND_PER_TICK), 0.0F, 1.0F);
    }

    public float getVerticalSpeedInBlocksPerSecond(float partialTick) {
        return Mth.lerp(partialTick, this.lastVerticalSpeed, this.verticalSpeed);
    }

    public float getSwimAmount(float partialTick) {
        return Mth.lerp(partialTick, this.lastSwimAmount, this.swimAmount);
    }

    // Runs on world time, which both sides share, so the server's thrust and the client's legs stay in step.
    public float getStrokePhase(float partialTick) {
        return TreefrogStroke.phaseAt(this.level().getGameTime() + this.getId() * STROKE_STAGGER_TICKS + partialTick);
    }

    public void hop(double vx, double vz) {
        this.setDeltaMovement(vx, this.getJumpPower(), vz);
        this.needsSync = true;
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
