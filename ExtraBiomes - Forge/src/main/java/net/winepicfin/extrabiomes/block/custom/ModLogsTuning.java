package net.winepicfin.extrabiomes.block.custom;

// Bedrock's "minecraft:destructible_by_mining".seconds_to_destroy for every ModLogs-based wood
// type's log/wood block (mystic/sky/palm/gilded_sky - see e.g.
// ExtraBiomes - Bedrock/packs/BP/blocks/mystic_wood/mystic_log.json) is 2, shared identically
// across all four. Deliberately has no Minecraft imports so tests can read the current Java
// value without needing a bootstrapped registry to construct a Block.
public final class ModLogsTuning {
    public static final float DESTROY_SECONDS = 5.0f;

    private ModLogsTuning() {
    }
}
