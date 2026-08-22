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
    public static final double MAX_HEALTH = 6;
    public static final double MOVEMENT_SPEED = 1.2;
    public static final double ATTACK_DAMAGE = 4;
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
