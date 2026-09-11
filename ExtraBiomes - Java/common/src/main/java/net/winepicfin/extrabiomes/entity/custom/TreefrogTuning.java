package net.winepicfin.extrabiomes.entity.custom;

// MOVEMENT_SPEED/FOLLOW_RANGE have no direct Bedrock equivalent, since Bedrock frogs move via slime hopping rather than a "movement" component.
// No Minecraft imports, deliberately: lets tests read these constants without triggering TreefrogEntity's registry-dependent class-load.
public final class TreefrogTuning {
    public static final double MAX_HEALTH = 4;
    public static final double MOVEMENT_SPEED = 0.3;
    public static final double JUMP_STRENGTH = 0.8;
    public static final double FOLLOW_RANGE = 16;
    // Fall damage is reduced by a flat 12 rather than cancelled outright, so a treefrog never hurts itself on its own hop but a long drop still kills it.
    public static final int FALL_DAMAGE_MODIFIER = -12;

    private TreefrogTuning() {
    }
}
