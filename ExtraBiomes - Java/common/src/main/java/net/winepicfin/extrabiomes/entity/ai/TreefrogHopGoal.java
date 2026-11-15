package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.winepicfin.extrabiomes.entity.custom.TreefrogEntity;

import java.util.EnumSet;

// Ported from Bedrock: hops like a slime rather than pathing continuously; delay tuned well beyond Bedrock's 5-15 ticks for long, infrequent leaps.
// Claims only MOVE so FloatGoal (JUMP) keeps the frog afloat between hops instead of pre-empting them.
public class TreefrogHopGoal extends Goal {
    private static final int MIN_LAND_DELAY = 60;
    // Short, so a frog that lands in water leaps back out instead of drifting.
    private static final int MIN_SWIM_DELAY = 10;

    private final TreefrogEntity treefrog;
    private int hopDelay;

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
        boolean swimming = this.treefrog.isInWater();
        if (!(this.treefrog.onGround() || swimming) || this.hopDelay-- > 0) {
            return;
        }
        this.hopDelay = swimming ? nextDelay(MIN_SWIM_DELAY) : nextDelay(MIN_LAND_DELAY);

        float yRot = this.treefrog.getRandom().nextFloat() * 360.0F;
        this.treefrog.setYRot(yRot);
        this.treefrog.yBodyRot = yRot;

        float rad = yRot * ((float) Math.PI / 180.0F);
        double speed = 0.7D * this.treefrog.getAttributeValue(Attributes.MOVEMENT_SPEED) / 0.3D;
        this.treefrog.hop(-Mth.sin(rad) * speed, Mth.cos(rad) * speed);
    }
}
