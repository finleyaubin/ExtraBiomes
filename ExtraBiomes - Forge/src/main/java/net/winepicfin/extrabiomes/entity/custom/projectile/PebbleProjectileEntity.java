package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class PebbleProjectileEntity extends ThrowableItemProjectile {
    public PebbleProjectileEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType,pLevel);
    }
    public PebbleProjectileEntity(Level pLevel) {
        super(ModEntities.PEBBLE_PROJECTILE.get(),pLevel);
    }
    public PebbleProjectileEntity(Level pLevel, LivingEntity livingEntity) {
        super(ModEntities.PEBBLE_PROJECTILE.get(),livingEntity,pLevel);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.PEBBLE.get();
    }


    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 6.0F);
    }
    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

    }
}
