package net.winepicfin.extrabiomes.entity.custom;

// Bedrock source values from ExtraBiomes - Bedrock/packs/BP/entities/piranha.json.
// Bedrock's "minecraft:underwater_movement" (0.3) is on a different scale from Java's
// generic.movement_speed and can't be copied across literally — what carries over is that a
// Bedrock piranha swims twice as fast as a Bedrock dolphin, so MOVEMENT_SPEED is set relative to
// vanilla Java's Dolphin (1.2) instead. See PiranhaEntity#travel for why the attribute is only
// honoured at all once travel() is overridden.
// Deliberately has no Minecraft imports so tests can read these constants without triggering
// PiranhaEntity's own class-load side effects.
public final class PiranhaTuning {
    // Bedrock's 3 discrete size steps (scale_small/normal/large) are treated as anchor points on a
    // continuous scale/health/damage curve instead, with the weighted split biasing where a fish lands.
    public static final float SIZE_MIN_SCALE = 0.5F;
    public static final float SIZE_NORMAL_SCALE = 1.0F;
    public static final float SIZE_MAX_SCALE = 1.5F;

    public static final double HEALTH_AT_MIN_SCALE = 6;
    public static final double HEALTH_AT_MAX_SCALE = 8;
    public static final double ATTACK_DAMAGE_AT_MIN_SCALE = 1;
    public static final double ATTACK_DAMAGE_AT_MAX_SCALE = 3;

    // Midpoints between adjacent Bedrock anchor points, partitioning the size range without gaps.
    public static final float SMALL_BAND_MAX = (SIZE_MIN_SCALE + SIZE_NORMAL_SCALE) / 2.0F;
    public static final float LARGE_BAND_MIN = (SIZE_NORMAL_SCALE + SIZE_MAX_SCALE) / 2.0F;
    // Relative weights for the weighted band roll - matches Bedrock's entity_spawned weights.
    public static final int[] SIZE_BAND_WEIGHTS = {30, 50, 15};

    public static final double MOVEMENT_SPEED = 1.2;
    public static final double FOLLOW_RANGE = 16;

    // SmoothSwimmingMoveControl's in-water speed factor. Effective acceleration is
    // goalSpeedModifier * MOVEMENT_SPEED * this, so it has to come down from the 0.1 copied off
    // Axolotl now that MOVEMENT_SPEED is dolphin-scale; 0.02 is vanilla Dolphin's own value.
    public static final float IN_WATER_SPEED_MODIFIER = 0.02F;
    public static final float OUT_OF_WATER_SPEED_MODIFIER = 0.1F;

    public static final double RANDOM_SWIM_SPEED = 1.2;
    // Bedrock "minecraft:behavior.melee_attack" speed_multiplier.
    public static final double MELEE_ATTACK_SPEED = 1.4;

    private PiranhaTuning() {
    }
}
