package net.winepicfin.extrabiomes.entity.custom;

// Bedrock source values from ExtraBiomes - Bedrock/packs/BP/entities/treefrog.json.
// MOVEMENT_SPEED/FOLLOW_RANGE have no direct Bedrock equivalent (Bedrock frogs move via
// slime_float/slime_random_direction hopping, not a "movement" component).
// Deliberately has no Minecraft imports so tests can read these constants without triggering
// TreefrogEntity's own class-load side effects.
public final class TreefrogTuning {
    public static final double MAX_HEALTH = 4;
    public static final double MOVEMENT_SPEED = 0.3;
    public static final double JUMP_STRENGTH = 0.8;
    public static final double FOLLOW_RANGE = 16;

    private TreefrogTuning() {
    }
}
