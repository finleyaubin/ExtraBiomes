package net.winepicfin.extrabiomes.entity.custom;

// Bedrock source values from ExtraBiomes - Bedrock/packs/BP/entities/jellyfish.json.
// Deliberately has no Minecraft imports so tests can read these constants without triggering
// JellyfishEntity's own class-load side effects (its SynchedEntityData.defineId(...) call needs
// a bootstrapped registry that plain JUnit can't provide).
public final class JellyfishTuning {
    public static final double MAX_HEALTH = 6;
    public static final double MOVEMENT_SPEED = 0.15;

    // Mirrors Bedrock's v.gray_amount / v.scale_y pre_animation lerps: full transition over ~8
    // seconds (160 ticks).
    public static final float GRAY_STEP_PER_TICK = 1.0F / 160.0F;
    public static final float SCALE_Y_STEP_PER_TICK = 0.9F / 160.0F;

    private JellyfishTuning() {
    }
}
