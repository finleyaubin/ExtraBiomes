package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.winepicfin.extrabiomes.advancements.ModCriteriaTriggers;
import net.winepicfin.extrabiomes.entity.custom.PiranhaEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;

import java.util.EnumSet;
import java.util.Comparator;
import java.util.List;

// Ported from Bedrock's bait targeting; runs ahead of the melee/swim goals so a thrown bait pulls the school off the player.
public class PiranhaBaitGoal extends Goal {
    private static final double SEARCH_RADIUS = 25.0;
    private static final double GIVE_UP_RADIUS = 35.0;
    private static final double BITE_RANGE = 1.2;
    private static final int BITE_INTERVAL = 10;
    // How far the piranha has to get from the bait's thrower, while actively chasing it, for the "lured it away" advancement to count.
    private static final double LURE_AWAY_DISTANCE = 8.0;

    private final PiranhaEntity piranha;
    private BaitProjectileEntity bait;
    private int biteCooldown;

    public PiranhaBaitGoal(PiranhaEntity piranha) {
        this.piranha = piranha;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        List<BaitProjectileEntity> candidates = this.piranha.level().getEntitiesOfClass(BaitProjectileEntity.class,
                this.piranha.getBoundingBox().inflate(SEARCH_RADIUS));
        this.bait = candidates.stream()
                .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(this.piranha)))
                .orElse(null);
        return this.bait != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.bait != null && this.bait.isAlive()
                && this.bait.distanceToSqr(this.piranha) <= GIVE_UP_RADIUS * GIVE_UP_RADIUS;
    }

    @Override
    public void start() {
        this.piranha.setChasedBait(this.bait);
        this.biteCooldown = 0;
    }

    @Override
    public void stop() {
        this.piranha.setChasedBait(null);
        this.piranha.getNavigation().stop();
        this.bait = null;
    }

    @Override
    public void tick() {
        this.piranha.getLookControl().setLookAt(this.bait, 30.0F, 30.0F);
        checkLuredAwayFromThrower();
        // Horizontal-only: landed bait floats right at the water's surface, but WaterBoundPathNavigation
        // won't breach it (SwimNodeEvaluator rejects non-Dolphins going above water), so a piranha
        // swimming just beneath the surface can never close the full 3D gap to it. Gating the bite on
        // XZ distance alone lets it still land the bite once it's underneath, as Bedrock's piranhas do.
        double dx = this.bait.getX() - this.piranha.getX();
        double dz = this.bait.getZ() - this.piranha.getZ();
        double horizontalDistSq = dx * dx + dz * dz;
        if (horizontalDistSq > BITE_RANGE * BITE_RANGE) {
            // SmoothSwimmingMoveControl only drives movement while its navigation has an active path,
            // so this must go through moveTo() rather than setWantedPosition() directly or the piranha
            // just stares at the bait without ever closing the distance.
            this.piranha.getNavigation().moveTo(this.bait.getX(), this.bait.getY(), this.bait.getZ(), 1.4);
            return;
        }
        if (this.biteCooldown-- <= 0) {
            this.biteCooldown = BITE_INTERVAL;
            this.bait.bite(1 + this.piranha.getRandom().nextInt(3), this.piranha.position());
        }
    }

    private void checkLuredAwayFromThrower() {
        if (!(this.bait.getOwner() instanceof ServerPlayer thrower)) {
            return;
        }
        if (this.piranha.distanceToSqr(thrower) >= LURE_AWAY_DISTANCE * LURE_AWAY_DISTANCE) {
            ModCriteriaTriggers.LURED_PIRANHA_WITH_BAIT.trigger(thrower);
        }
    }
}
