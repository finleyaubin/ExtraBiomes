package net.winepicfin.extrabiomes.entity.custom;

// No Minecraft imports, deliberately: lets tests read these constants without triggering JellyfishEntity's registry-dependent class-load.
public final class JellyfishTuning {
    public static final double MAX_HEALTH = 6;
    public static final double MOVEMENT_SPEED = 0.15;

    // Mirrors Bedrock's gray_amount/scale_y pre_animation lerps: full transition over ~8 seconds (160 ticks).
    public static final float GRAY_STEP_PER_TICK = 1.0F / 160.0F;
    public static final float SCALE_Y_STEP_PER_TICK = 0.9F / 160.0F;

    private JellyfishTuning() {
    }
}
