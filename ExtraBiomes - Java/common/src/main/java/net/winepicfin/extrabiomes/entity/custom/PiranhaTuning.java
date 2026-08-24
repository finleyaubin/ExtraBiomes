package net.winepicfin.extrabiomes.entity.custom;

// Bedrock's underwater_movement (0.3) isn't on Java's movement_speed scale, so MOVEMENT_SPEED is set relative to vanilla Dolphin (1.2) instead, matching that a Bedrock piranha swims twice as fast as a Bedrock dolphin.
public final class PiranhaTuning {
    // Bedrock's 3 discrete size steps are treated as anchor points on a continuous scale/health/damage curve, with a weighted split biasing where a fish lands.
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

    // Effective acceleration is goalSpeedModifier * MOVEMENT_SPEED * this; brought down to vanilla Dolphin's own 0.02 now that MOVEMENT_SPEED is dolphin-scale.
    public static final float IN_WATER_SPEED_MODIFIER = 0.02F;
    public static final float OUT_OF_WATER_SPEED_MODIFIER = 0.1F;

    public static final double RANDOM_SWIM_SPEED = 1.2;
    // Bedrock "minecraft:behavior.melee_attack" speed_multiplier.
    public static final double MELEE_ATTACK_SPEED = 1.4;

    private PiranhaTuning() {
    }
}
