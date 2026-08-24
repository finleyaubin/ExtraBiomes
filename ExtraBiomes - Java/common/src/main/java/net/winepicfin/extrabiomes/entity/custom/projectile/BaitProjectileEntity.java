package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.winepicfin.extrabiomes.entity.ModEntities;
import org.jetbrains.annotations.NotNull;

// Ported from Bedrock extrabiomes:bait_projectile: a thrown lure that, once landed, sits as a durable, attackable decoy for piranhas.
public class BaitProjectileEntity extends ThrowableItemProjectile {
    // Bumped well past Bedrock's 90 so it survives real combat damage once landed, not just piranha nibbles.
    private static final int MAX_HEALTH = 300;
    private int health = MAX_HEALTH;
    private boolean landed;

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
        if (this.landed) {
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
        this.setDeltaMovement(delta.x * 0.9, delta.y - offset * 0.2, delta.z * 0.9);
        if (Math.abs(offset) < 0.05 && this.getDeltaMovement().length() < 0.05) {
            land();
        }
    }

    private void land() {
        this.landed = true;
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean isNoGravity() {
        return this.landed || super.isNoGravity();
    }

    // Only attackable once landed: mid-flight it should behave like any other thrown projectile, not something players can swing at.
    @Override
    public boolean isPickable() {
        return this.landed;
    }

    // Lets players and other mobs damage the landed decoy through the normal combat path, on top of PiranhaBaitGoal's direct bite() calls (piranhas aren't LivingEntity attackers, so they can't route through hurt()).
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.landed || this.level().isClientSide || this.isInvulnerableTo(source)) {
            return false;
        }
        bite(Math.max(1, Math.round(amount)));
        return true;
    }

    public void bite(int amount) {
        this.health -= amount;
        if (this.health <= 0 && !this.level().isClientSide) {
            this.discard();
        }
    }

    // Drives BaitRenderer's controller.render.bait texture selection (array.skins[floor(health / stage)]).
    public int getHealth() {
        return this.health;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }
}
