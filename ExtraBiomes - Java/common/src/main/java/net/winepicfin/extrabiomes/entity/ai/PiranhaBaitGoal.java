package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
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
        double distSq = this.bait.distanceToSqr(this.piranha);
        if (distSq > BITE_RANGE * BITE_RANGE) {
            // SmoothSwimmingMoveControl only drives movement while its navigation has an active path,
            // so this must go through moveTo() rather than setWantedPosition() directly or the piranha
            // just stares at the bait without ever closing the distance.
            this.piranha.getNavigation().moveTo(this.bait.getX(), this.bait.getY(), this.bait.getZ(), 1.4);
            return;
        }
        if (this.biteCooldown-- <= 0) {
            this.biteCooldown = BITE_INTERVAL;
            this.bait.bite(1 + this.piranha.getRandom().nextInt(3));
        }
    }
}
