package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Ported from Bedrock extrabiomes:bait_projectile: a thrown lure that, once landed, sits as a durable, attackable decoy for piranhas.
public class BaitProjectileEntity extends ThrowableItemProjectile {
    // Bumped well past Bedrock's 90 so it survives real combat damage once landed, not just piranha nibbles.
    public static final int MAX_HEALTH = 300;
    // How long the hurt flash/shake plays after a bite, matching LivingEntity's own HURT_DURATION.
    private static final int HURT_DURATION = 10;
    // Small and heavily self-damped: enough to visibly jostle the bait per bite without letting it wander off over dozens of piranha nibbles.
    private static final double KNOCKBACK_STRENGTH = 0.12;
    private static final double KNOCKBACK_DAMPING = 0.6;

    // health/hurtTime/landed all drive client-only rendering (BaitRenderer/BaitModel) or client-visible
    // behavior (isPickable/interact), and the client only ever sees a separate, network-synced copy of
    // this entity - plain fields here would silently stay at their defaults on that copy forever.
    private static final EntityDataAccessor<Integer> DATA_HEALTH =
            SynchedEntityData.defineId(BaitProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HURT_TIME =
            SynchedEntityData.defineId(BaitProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_LANDED =
            SynchedEntityData.defineId(BaitProjectileEntity.class, EntityDataSerializers.BOOLEAN);

    public BaitProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BaitProjectileEntity(Level level) {
        super(ModEntities.BAIT_PROJECTILE.get(), level);
    }

    public BaitProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.BAIT_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HEALTH, MAX_HEALTH);
        this.entityData.define(DATA_HURT_TIME, 0);
        this.entityData.define(DATA_LANDED, false);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.STRING;
    }

    @Override
    protected float getGravity() {
        return 0.03F;
    }

    // Unlike Pebble/RazorFeather, bait doesn't discard on impact (it needs to sit as a decoy), so motion must be arrested here or gravity keeps pulling it through the ground forever.
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        land();
        if (!this.level().isClientSide) {
            this.setPos(this.getX(), result.getLocation().y, this.getZ());
        }
    }

    // Thrown into water it doesn't land where it first touches the surface (that would freeze it half-submerged and out of a piranha's reach); instead it bobs upward like FishingHook's BOBBING state until it settles floating at the surface.
    @Override
    public void tick() {
        super.tick();
        // Not a LivingEntity, so none of the vanilla hurt-flash/shake plumbing applies here automatically - drive it ourselves.
        if (this.getHurtTime() > 0) {
            this.entityData.set(DATA_HURT_TIME, this.getHurtTime() - 1);
        }
        if (isLanded()) {
            // A bite knocks it around a little (see applyKnockback); damp that back out quickly so
            // repeated nibbles don't let it wander off - it's meant to sit still as a decoy.
            if (this.getDeltaMovement().lengthSqr() > 1.0E-4) {
                this.setDeltaMovement(this.getDeltaMovement().scale(KNOCKBACK_DAMPING));
            } else if (!this.getDeltaMovement().equals(Vec3.ZERO)) {
                this.setDeltaMovement(Vec3.ZERO);
            }
            return;
        }
        BlockPos pos = this.blockPosition();
        FluidState fluidState = this.level().getFluidState(pos);
        if (!fluidState.is(FluidTags.WATER)) {
            return;
        }
        float surfaceHeight = fluidState.getHeight(this.level(), pos);
        double offset = this.getY() - pos.getY() - surfaceHeight;
        Vec3 delta = this.getDeltaMovement();
        // Undo this tick's gravity impulse (super.tick() already applied it) before damping - left in,
        // it biases the settling point below the surface and, since only X/Z were damped below, Y never
        // stopped oscillating enough to clear the landed threshold, so bait thrown into water never
        // actually landed (stayed a live, unbitable projectile forever).
        double velocityY = (delta.y + this.getGravity()) * 0.9 - offset * 0.2;
        this.setDeltaMovement(delta.x * 0.9, velocityY, delta.z * 0.9);
        if (Math.abs(offset) < 0.05 && this.getDeltaMovement().length() < 0.05) {
            land();
        }
    }

    private void land() {
        this.entityData.set(DATA_LANDED, true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    private boolean isLanded() {
        return this.entityData.get(DATA_LANDED);
    }

    // Nudges it sideways away from whatever bit/hit it - small and quickly damped out by tick(), so
    // it visibly reacts without actually wandering away from where the player placed it.
    private void applyKnockback(@Nullable Vec3 fromPosition) {
        if (fromPosition == null) {
            return;
        }
        double dx = this.getX() - fromPosition.x;
        double dz = this.getZ() - fromPosition.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance < 1.0E-4) {
            return;
        }
        this.setDeltaMovement(this.getDeltaMovement().add(
                dx / horizontalDistance * KNOCKBACK_STRENGTH, 0.0, dz / horizontalDistance * KNOCKBACK_STRENGTH));
        this.hasImpulse = true;
    }

    @Override
    public boolean isNoGravity() {
        return isLanded() || super.isNoGravity();
    }

    // Only attackable once landed: mid-flight it should behave like any other thrown projectile, not something players can swing at.
    @Override
    public boolean isPickable() {
        return isLanded();
    }

    // Shift-click with an empty hand to reclaim a landed bait instead of leaving it to expire - its remaining health carries over as the returned item's damage bar (BaitItem is durability-based specifically so this has something to show).
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!isLanded() || !player.isShiftKeyDown() || !player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            ItemStack pickedUp = this.getItem().isEmpty() ? new ItemStack(ModItems.BAIT.get()) : this.getItem().copy();
            pickedUp.setCount(1);
            pickedUp.setDamageValue(this.getMaxHealth() - this.getHealth());
            if (!player.getInventory().add(pickedUp)) {
                player.drop(pickedUp, false);
            }
            this.discard();
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    // Lets players and other mobs damage the landed decoy through the normal combat path, on top of PiranhaBaitGoal's direct bite() calls (piranhas aren't LivingEntity attackers, so they can't route through hurt()).
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!isLanded() || this.level().isClientSide || this.isInvulnerableTo(source)) {
            return false;
        }
        bite(Math.max(1, Math.round(amount)), source.getSourcePosition());
        return true;
    }

    public void bite(int amount, @Nullable Vec3 fromPosition) {
        int newHealth = this.getHealth() - amount;
        this.entityData.set(DATA_HEALTH, newHealth);
        this.entityData.set(DATA_HURT_TIME, HURT_DURATION);
        applyKnockback(fromPosition);
        if (newHealth <= 0 && !this.level().isClientSide) {
            this.discard();
        }
    }

    // Drives BaitRenderer's controller.render.bait texture selection (array.skins[floor(health / stage)]) and BaitModel's worm-loss animation.
    public int getHealth() {
        return this.entityData.get(DATA_HEALTH);
    }

    // Lets BaitItem re-throw a partially-bitten bait (picked up via interact()) starting from the durability it had left, instead of resetting to full.
    public void setHealth(int health) {
        this.entityData.set(DATA_HEALTH, Mth.clamp(health, 1, MAX_HEALTH));
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    // Drives BaitRenderer's hurt-flash overlay and BaitModel's hurt shake.
    public int getHurtTime() {
        return this.entityData.get(DATA_HURT_TIME);
    }
}
