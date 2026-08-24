package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.winepicfin.extrabiomes.entity.custom.HoppleshroomEntity;

import java.util.EnumSet;

// Ported from Bedrock: travels by hopping like a slime rather than pathing continuously on the ground.
public class HoppleshroomHopGoal extends Goal {
    private final HoppleshroomEntity hoppleshroom;
    private int hopDelay;

    public HoppleshroomHopGoal(HoppleshroomEntity hoppleshroom) {
        this.hoppleshroom = hoppleshroom;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
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

    @Override
    public void tick() {
        if (!this.hoppleshroom.onGround() || this.hopDelay-- > 0) {
            return;
        }
        this.hopDelay = 10 + this.hoppleshroom.getRandom().nextInt(30);

        float yRot = this.hoppleshroom.getRandom().nextFloat() * 360.0F;
        this.hoppleshroom.setYRot(yRot);
        this.hoppleshroom.yBodyRot = yRot;

        float rad = yRot * ((float) Math.PI / 180.0F);
        double speed = 0.25D * this.hoppleshroom.getAttributeValue(Attributes.MOVEMENT_SPEED) / 0.3D;
        this.hoppleshroom.hop(-Mth.sin(rad) * speed, Mth.cos(rad) * speed);
    }
}
