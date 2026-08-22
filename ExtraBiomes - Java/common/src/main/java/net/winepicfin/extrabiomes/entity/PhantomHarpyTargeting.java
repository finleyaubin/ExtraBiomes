package net.winepicfin.extrabiomes.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Phantom;
import net.winepicfin.extrabiomes.entity.custom.HarpyEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Java port of the one behavioral change in Bedrock's vanilla-phantom override
 * ("packs/BP/entities/phantom.json"): its {@code minecraft:behavior.nearest_attackable_target}
 * lists {@code harpy} alongside {@code player} (max_dist 80 vs 64), so phantoms swoop at this
 * mod's harpies too. Everything else in that file restates vanilla phantom unchanged.
 * <p>
 * Vanilla's own player-targeting goal sits at priority 1 and wins whenever a player is in range;
 * harpies go just below it so a phantom only chases one when no player is available. The search
 * radius comes from the phantom's generic.follow_range rather than Bedrock's 80-block max_dist,
 * which has no equivalent knob on a stock NearestAttackableTargetGoal.
 * <p>
 * The goal is only built here: {@code Mob.targetSelector} is protected in common's mappings, so
 * each loader adds it through its own access mechanism (Forge's AT-widened field, Fabric's
 * MobAccessor mixin).
 */
public final class PhantomHarpyTargeting {
    public static final int GOAL_PRIORITY = 2;

    @Nullable
    public static Goal createHarpyTargetGoal(Entity entity) {
        if (!(entity instanceof Phantom phantom)) {
            return null;
        }
        return new NearestAttackableTargetGoal<>(phantom, HarpyEntity.class, true);
    }

    private PhantomHarpyTargeting() {
    }
}
