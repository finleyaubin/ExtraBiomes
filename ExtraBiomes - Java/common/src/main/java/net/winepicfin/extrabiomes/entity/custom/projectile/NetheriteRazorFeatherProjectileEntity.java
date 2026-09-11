package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

// Ported from Bedrock extrabiomes:netherite_razor_feather — strongest razor feather variant (summon-only).
public class NetheriteRazorFeatherProjectileEntity extends RazorFeatherProjectileEntity {
    public NetheriteRazorFeatherProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public NetheriteRazorFeatherProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.NETHERITE_RAZOR_FEATHER.get(), shooter, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.NETHERITE_RAZOR_FEATHER.get();
    }

    @Override
    protected float getDamage() {
        return 8.0F;
    }
}
