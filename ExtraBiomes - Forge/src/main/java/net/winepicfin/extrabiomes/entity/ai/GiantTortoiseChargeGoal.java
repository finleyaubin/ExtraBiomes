package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.winepicfin.extrabiomes.entity.custom.GiantTortoiseEntity;

import java.util.EnumSet;
import java.util.UUID;

// Ported from Bedrock minecraft:behavior.ram_attack (min_ram_distance: 4, ram_distance: 7,
// ram_speed: 2 vs run_speed: 1) — beelines at the target and doubles movement speed while
// within charging range. The body-roll spin in GiantTortoiseModel reacts automatically since
// it's driven by actual distance moved, so no animation triggering is needed here.
public class GiantTortoiseChargeGoal extends Goal {
    private static final UUID CHARGE_SPEED_MODIFIER_ID = UUID.fromString("23a748af-3d91-45e8-b502-545165bdedc4");
    private static final double MIN_RAM_DISTANCE = 4.0D;
    private static final double RAM_DISTANCE = 7.0D;
    private static final double RAM_SPEED_MULTIPLIER = 1.0D; // +100% => 2x base, matches ram_speed:2 vs run_speed:1

    private final GiantTortoiseEntity tortoise;
    private boolean charging;

    public GiantTortoiseChargeGoal(GiantTortoiseEntity tortoise) {
        this.tortoise = tortoise;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.tortoise.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        double distSqr = this.tortoise.distanceToSqr(target);
        return distSqr <= RAM_DISTANCE * RAM_DISTANCE && distSqr >= MIN_RAM_DISTANCE * MIN_RAM_DISTANCE
                && this.tortoise.getSensing().hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.tortoise.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        double distSqr = this.tortoise.distanceToSqr(target);
        // keep charging a little past min_ram_distance so it doesn't flicker on/off at the boundary
        return distSqr <= RAM_DISTANCE * RAM_DISTANCE * 1.5 && distSqr > 1.0;
    }

    @Override
    public void start() {
        this.charging = true;
        applySpeedBoost();
    }

    @Override
    public void stop() {
        this.charging = false;
        removeSpeedBoost();
        this.tortoise.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.tortoise.getTarget();
        if (target == null) {
            return;
        }
        this.tortoise.getLookControl().setLookAt(target, 30.0F, 30.0F);
        // base speed value is irrelevant here since the AttributeModifier scales the entity's
        // real movement speed attribute directly, keeping the ram consistent with normal walk speed
        this.tortoise.getNavigation().moveTo(target, 1.0D);
    }

    private void applySpeedBoost() {
        var attr = this.tortoise.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(CHARGE_SPEED_MODIFIER_ID) == null) {
            attr.addTransientModifier(new AttributeModifier(CHARGE_SPEED_MODIFIER_ID, "Giant tortoise charge speed",
                    RAM_SPEED_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private void removeSpeedBoost() {
        var attr = this.tortoise.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(CHARGE_SPEED_MODIFIER_ID);
        }
    }

    public boolean isCharging() {
        return this.charging;
    }
}
