package net.winepicfin.extrabiomes.worldgen;

// Weight values ported from Bedrock's spawn_rules "minecraft:weight".default
// (ExtraBiomes - Bedrock/packs/BP/spawn_rules/<name>.json) into ModBiomeModifiers'
// AddSpawnsBiomeModifier registrations. Deliberately has no Minecraft imports so tests can read
// the current Java value without needing a bootstrapped registry.
public final class MobSpawnWeightTuning {
    public static final int GIANT_TORTOISE = 15;
    public static final int PIRANHA_JUNGLE = 35;
    public static final int PIRANHA_SWAMP = 35;
    // Bedrock's jungle "minecraft:herd" min_size/max_size. The Java port previously used 6-10;
    // matching Bedrock's 8-10 puts more piranhas in the water per successful spawn attempt, which
    // together with the WATER_AMBIENT category on ModEntities.PIRANHA is what actually controls how
    // dense jungle water gets - the weight above is only a tiebreak between entries in the same
    // category, and piranha is the only one vanilla jungles have.
    public static final int PIRANHA_JUNGLE_MIN_GROUP = 8;
    public static final int PIRANHA_JUNGLE_MAX_GROUP = 10;
    public static final int TREEFROG_JUNGLE = 25;
    public static final int TREEFROG_SWAMP = 25;
    public static final int HOPPLESHROOM = 35;
    public static final int JELLYFISH = 25;
    public static final int JELLYFISH_BEACH = 5;
    public static final int HARPY = 50;
    public static final int WORM = 25;

    private MobSpawnWeightTuning() {
    }
}
