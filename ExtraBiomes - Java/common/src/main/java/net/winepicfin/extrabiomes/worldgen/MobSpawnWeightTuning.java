package net.winepicfin.extrabiomes.worldgen;

// Deliberately has no Minecraft imports so tests can read the current Java value without needing a bootstrapped registry.
public final class MobSpawnWeightTuning {
    public static final int GIANT_TORTOISE = 15;
    public static final int PIRANHA_JUNGLE = 35;
    public static final int PIRANHA_SWAMP = 35;
    // Herd size (not the weight above) plus the WATER_AMBIENT cap on ModEntities.PIRANHA is what actually controls jungle water density, since piranha is the only WATER_AMBIENT spawn vanilla jungles have.
    public static final int PIRANHA_JUNGLE_MIN_GROUP = 8;
    public static final int PIRANHA_JUNGLE_MAX_GROUP = 10;
    public static final int TREEFROG_JUNGLE = 25;
    public static final int TREEFROG_SWAMP = 25;
    public static final int HOPPLESHROOM = 35;
    public static final int JELLYFISH = 25;
    public static final int JELLYFISH_BEACH = 5;
    public static final int HARPY = 50;
    public static final int WORM = 25;
    public static final int PUCKOO_BEACH = 10;
    public static final int PUCKOO_BEACH_MIN_GROUP = 2;
    public static final int PUCKOO_BEACH_MAX_GROUP = 4;

    private MobSpawnWeightTuning() {
    }
}
