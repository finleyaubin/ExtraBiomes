package net.winepicfin.extrabiomes.block;

// Values ported from various standalone Bedrock blocks (ExtraBiomes - Bedrock/packs/BP/blocks/)
// that don't have their own custom Block subclass to hold a "Tuning" class alongside. Deliberately
// has no Minecraft imports so tests can read the current Java value without needing a
// bootstrapped registry.
public final class MiscBlockTuning {
    // blocks/dense_cloud.json, blocks/dense_cloud_brick.json: minecraft:destructible_by_mining.seconds_to_destroy.
    public static final float DENSE_CLOUD_DESTROY_SECONDS = 0.3f;
    public static final float DENSE_CLOUD_BRICK_DESTROY_SECONDS = 0.5f;

    // blocks/mushrooms/glow_mushroom_block.json: minecraft:light_emission (same 0-15 scale as
    // Java's light level).
    public static final int GLOW_MUSHROOM_BLOCK_LIGHT_EMISSION = 15;

    private MiscBlockTuning() {
    }
}
