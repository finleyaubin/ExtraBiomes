package net.winepicfin.extrabiomes.worldgen;

// Weight values ported from Bedrock's spawn_rules "minecraft:weight".default
// (ExtraBiomes - Bedrock/packs/BP/spawn_rules/<name>.json) into ModBiomeModifiers'
// AddSpawnsBiomeModifier registrations. Deliberately has no Minecraft imports so tests can read
// the current Java value without needing a bootstrapped registry.
public final class MobSpawnWeightTuning {
    public static final int GIANT_TORTOISE = 15;
    public static final int PIRANHA_JUNGLE = 35;
    public static final int PIRANHA_SWAMP = 35;
    public static final int TREEFROG_JUNGLE = 25;
    public static final int TREEFROG_SWAMP = 25;
    public static final int HOPPLESHROOM = 35;
    public static final int JELLYFISH = 25;
    public static final int JELLYFISH_BEACH = 5;
    public static final int HARPY = 20;
    public static final int WORM = 25;

    private MobSpawnWeightTuning() {
    }
}
