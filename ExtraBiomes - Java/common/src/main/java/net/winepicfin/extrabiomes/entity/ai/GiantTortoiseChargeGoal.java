package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.winepicfin.extrabiomes.entity.custom.GiantTortoiseEntity;

import java.util.EnumSet;
import java.util.UUID;

// Ported from Bedrock's ram_attack: runs the full charge/hit/retreat/re-charge cycle itself instead of handing off to a separate melee goal.
public class GiantTortoiseChargeGoal extends Goal {
    private static final UUID CHARGE_SPEED_MODIFIER_ID = UUID.fromString("23a748af-3d91-45e8-b502-545165bdedc4");
    private static final double MIN_RAM_DISTANCE = 4.0D;
    private static final double RAM_DISTANCE = 7.0D;
    private static final double RAM_SPEED_MULTIPLIER = 1.0D; // +100% => 2x base, matches ram_speed:2 vs run_speed:1
    private static final double ATTACK_RANGE = 3.0D;
    private static final int ATTACK_COOLDOWN_TICKS = 30;
    private static final int MAX_RETREAT_TICKS = 40;

    private enum State { CHARGE, RETREAT }

    private final GiantTortoiseEntity tortoise;
    private State state;
    private int attackCooldown;
    private int retreatTicks;

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
        boolean inRange = distSqr <= RAM_DISTANCE * RAM_DISTANCE && distSqr >= MIN_RAM_DISTANCE * MIN_RAM_DISTANCE;
        boolean canSee = this.tortoise.getSensing().hasLineOfSight(target);
        return inRange && canSee;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.tortoise.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.state = State.CHARGE;
        this.attackCooldown = 0;
        this.tortoise.setCharging(true);
        applySpeedBoost();
    }

    @Override
    public void stop() {
        this.tortoise.setCharging(false);
        removeSpeedBoost();
        this.tortoise.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.tortoise.getTarget();
        if (target == null) {
            return;
        }
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        double distSqr = this.tortoise.distanceToSqr(target);

        if (this.state == State.CHARGE) {
            this.tortoise.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.tortoise.getNavigation().moveTo(target, 1.0D);
            if (distSqr <= ATTACK_RANGE * ATTACK_RANGE && this.attackCooldown <= 0) {
                this.tortoise.doHurtTarget(target);
                this.attackCooldown = ATTACK_COOLDOWN_TICKS;
                this.state = State.RETREAT;
                this.retreatTicks = MAX_RETREAT_TICKS;
            }
        } else {
            double dx = this.tortoise.getX() - target.getX();
            double dz = this.tortoise.getZ() - target.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len > 1.0E-4) {
                double retreatX = this.tortoise.getX() + (dx / len) * RAM_DISTANCE;
                double retreatZ = this.tortoise.getZ() + (dz / len) * RAM_DISTANCE;
                this.tortoise.getNavigation().moveTo(retreatX, this.tortoise.getY(), retreatZ, 1.0D);
            }
            this.retreatTicks--;
            if (this.retreatTicks <= 0 || distSqr >= RAM_DISTANCE * RAM_DISTANCE) {
                this.state = State.CHARGE;
            }
        }
    }

    private void applySpeedBoost() {
        var attr = this.tortoise.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(CHARGE_SPEED_MODIFIER_ID) == null) {
            attr.addTransientModifier(new AttributeModifier(CHARGE_SPEED_MODIFIER_ID, "Giant tortoise charge speed",
                    RAM_SPEED_MULTIPLIER, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private void removeSpeedBoost() {
        var attr = this.tortoise.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(CHARGE_SPEED_MODIFIER_ID);
        }
    }
}
