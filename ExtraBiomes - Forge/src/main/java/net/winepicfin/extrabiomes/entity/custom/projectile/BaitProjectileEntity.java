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
// Bedrock gives it 90 health that piranha bites whittle down until it is destroyed; that gives the
// bait a limited lifespan as a decoy rather than luring the school away forever.
public class BaitProjectileEntity extends ThrowableItemProjectile {
    private static final int MAX_HEALTH = 90;
    private int health = MAX_HEALTH;

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

    // Piranhas aren't LivingEntity targets, so they can't use vanilla hurt(); PiranhaBaitGoal calls
    // this directly each time it takes a bite out of the bait.
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
