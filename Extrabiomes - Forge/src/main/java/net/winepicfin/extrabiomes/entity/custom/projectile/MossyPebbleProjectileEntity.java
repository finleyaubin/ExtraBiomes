package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class MossyPebbleProjectileEntity extends ThrowableItemProjectile {
    public MossyPebbleProjectileEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType,pLevel);
    }
    public MossyPebbleProjectileEntity(Level pLevel) {
        super(ModEntities.MOSSY_PEBBLE_PROJECTILE.get(),pLevel);
    }
    public MossyPebbleProjectileEntity(Level pLevel, LivingEntity livingEntity) {
        super(ModEntities.MOSSY_PEBBLE_PROJECTILE.get(),livingEntity,pLevel);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.mossy_pebble.get();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        if(!this.level().isClientSide()){
            this.level().broadcastEntityEvent(this,((byte) 3));
            //this.level().setBlock(blockPosition(),,3)
        }
        super.onHitBlock(pResult);
    }
}
