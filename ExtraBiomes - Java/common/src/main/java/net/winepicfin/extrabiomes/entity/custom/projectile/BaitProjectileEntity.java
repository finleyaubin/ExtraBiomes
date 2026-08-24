package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.winepicfin.extrabiomes.entity.ModEntities;
import org.jetbrains.annotations.NotNull;

// Ported from Bedrock extrabiomes:bait_projectile: a slow-falling lure with 90 health that piranha bites whittle down, giving it a limited lifespan as a decoy.
public class BaitProjectileEntity extends ThrowableItemProjectile {
    private static final int MAX_HEALTH = 90;
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
        return 0.001F;
    }

    // Unlike Pebble/RazorFeather, bait doesn't discard on impact (it needs to sit as a decoy), so motion must be arrested here or the near-zero gravity lets it drift through the ground forever.
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.landed = true;
        this.setDeltaMovement(Vec3.ZERO);
        if (!this.level().isClientSide) {
            this.setPos(this.getX(), result.getLocation().y, this.getZ());
        }
    }

    @Override
    public boolean isNoGravity() {
        return this.landed || super.isNoGravity();
    }

    // Piranhas aren't LivingEntity targets so can't use vanilla hurt(); PiranhaBaitGoal calls this directly on each bite.
    public void bite(int amount) {
        this.health -= amount;
        if (this.health <= 0 && !this.level().isClientSide) {
            this.discard();
        }
    }

    // Drives BaitRenderer's controller.render.bait texture selection (array.skins[floor(health/10)]).
    public int getHealth() {
        return this.health;
    }
}
