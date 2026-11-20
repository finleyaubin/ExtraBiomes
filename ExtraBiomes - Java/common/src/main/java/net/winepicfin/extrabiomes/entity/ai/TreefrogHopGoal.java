package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.winepicfin.extrabiomes.entity.custom.TreefrogEntity;
import net.winepicfin.extrabiomes.entity.custom.TreefrogStroke;

import java.util.EnumSet;

// Ported from Bedrock: hops like a slime rather than pathing continuously; delay tuned well beyond Bedrock's 5-15 ticks for long, infrequent leaps.
// Claims only MOVE so FloatGoal (JUMP) keeps the frog afloat between hops instead of pre-empting them.
public class TreefrogHopGoal extends Goal {
    private static final int MIN_LAND_DELAY = 60;
    private static final int MIN_SWIM_DELAY = 40;
    private static final double SWIM_LOOKAHEAD = 2.0;
    // Water drags walking-speed input down to a crawl; this brings the kick of a stroke up to about three blocks a second.
    private static final double SWIM_SPEED_MODIFIER = 3.0;

    private final TreefrogEntity treefrog;
    private int hopDelay;
    private Vec3 heading = Vec3.ZERO;

    public TreefrogHopGoal(TreefrogEntity treefrog) {
        this.treefrog = treefrog;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void start() {
        this.hopDelay = 0;
    }

    private int nextDelay(int minimum) {
        return minimum + this.treefrog.getRandom().nextInt(minimum + 1);
    }

    @Override
    public void tick() {
        boolean grounded = this.treefrog.onGround();
        boolean swimming = !grounded && this.treefrog.isInWater();
        if (!grounded && !swimming) {
            return;
        }
        if (this.hopDelay-- <= 0) {
            this.hopDelay = grounded ? nextDelay(MIN_LAND_DELAY) : nextDelay(MIN_SWIM_DELAY);
            float yRot = this.treefrog.getRandom().nextFloat() * 360.0F;
            this.heading = Vec3.directionFromRotation(0.0F, yRot);
            if (grounded) {
                hopAlongHeading(yRot);
            }
        }
        if (swimming) {
            paddleAlongHeading();
        }
    }

    private void hopAlongHeading(float yRot) {
        this.treefrog.setYRot(yRot);
        this.treefrog.yBodyRot = yRot;
        double speed = 0.7D * this.treefrog.getAttributeValue(Attributes.MOVEMENT_SPEED) / 0.3D;
        this.treefrog.hop(this.heading.x * speed, this.heading.z * speed);
    }

    // Water gives nothing to leap from, so the frog paddles; vanilla lifts a swimmer onto a bank only while it keeps pushing forward.
    private void paddleAlongHeading() {
        Vec3 ahead = this.treefrog.position().add(this.heading.scale(SWIM_LOOKAHEAD));
        double thrust = TreefrogStroke.thrustAt(this.treefrog.getStrokePhase(0.0F));
        this.treefrog.getMoveControl().setWantedPosition(ahead.x, ahead.y, ahead.z, SWIM_SPEED_MODIFIER * thrust);
    }
}
