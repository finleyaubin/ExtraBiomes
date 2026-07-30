package net.winepicfin.extrabiomes.entity.custom.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.ModEntities;
import org.jetbrains.annotations.NotNull;

// Ported from Bedrock extrabiomes:bait_projectile — a slow-falling fishing bait lure (summon-only).
public class BaitProjectileEntity extends ThrowableItemProjectile {
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
}
