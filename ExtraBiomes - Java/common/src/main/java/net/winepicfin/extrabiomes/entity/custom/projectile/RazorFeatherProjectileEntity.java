package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

// Ported from Bedrock extrabiomes:razor_feather — thrown feather dealing impact damage.
public class RazorFeatherProjectileEntity extends ThrowableItemProjectile {
    public RazorFeatherProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public RazorFeatherProjectileEntity(Level level) {
        super(ModEntities.RAZOR_FEATHER.get(), level);
    }

    public RazorFeatherProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.RAZOR_FEATHER.get(), shooter, level);
    }

    protected RazorFeatherProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.RAZOR_FEATHER.get();
    }

    protected float getDamage() {
        return 4.0F;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                        this.getX(), this.getY(), this.getZ(),
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), this.getDamage());
    }

    // Fires for both onHitBlock (landing) and onHitEntity (striking a target) since Projectile.onHit
    // dispatches to those before returning here. Player-thrown feathers drop as a pickupable item on
    // either outcome, matching Bedrock's player_razor_feather component group; feathers thrown by mobs
    // (e.g. HarpyEntity) just vanish like before.
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            if (this.getOwner() instanceof Player) {
                ItemEntity drop = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem().copy());
                drop.setDefaultPickUpDelay();
                this.level().addFreshEntity(drop);
            }
            this.discard();
        }
    }
}
