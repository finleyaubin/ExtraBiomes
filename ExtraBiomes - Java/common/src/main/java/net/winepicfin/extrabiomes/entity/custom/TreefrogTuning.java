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
    // Bedrock's "minecraft:damage_sensor" trigger for cause "fall" (damage_modifier: -12): fall
    // damage is reduced by a flat 12 rather than cancelled outright, so a treefrog never hurts
    // itself on its own hop but a long enough drop still kills it.
    public static final int FALL_DAMAGE_MODIFIER = -12;

    private TreefrogTuning() {
    }
}
