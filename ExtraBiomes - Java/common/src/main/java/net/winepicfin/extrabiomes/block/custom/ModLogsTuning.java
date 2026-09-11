package net.winepicfin.extrabiomes.block.custom;

// Bedrock's destructible_by_mining.seconds_to_destroy is 2 for every ModLogs wood type (mystic/sky/palm/gilded_sky); deliberately has no Minecraft imports so tests can read this without a bootstrapped registry.
public final class ModLogsTuning {
    public static final float DESTROY_SECONDS = 2.0f;

    private ModLogsTuning() {
    }
}
