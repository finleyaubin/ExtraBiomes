package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

// Ported from Bedrock extrabiomes:diamond_razor_feather — stronger razor feather variant (summon-only).
public class DiamondRazorFeatherProjectileEntity extends RazorFeatherProjectileEntity {
    public DiamondRazorFeatherProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public DiamondRazorFeatherProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.DIAMOND_RAZOR_FEATHER.get(), level);
        this.setOwner(shooter);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.DIAMOND_RAZOR_FEATHER.get();
    }

    @Override
    protected float getDamage() {
        return 6.0F;
    }
}
