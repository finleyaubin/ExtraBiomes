package net.winepicfin.extrabiomes;

import net.winepicfin.extrabiomes.worldgen.biomes.ModTerrablender;

// Loader-agnostic config *values* only - each platform owns its own config file format/UI (ForgeConfigSpec vs. a Fabric .properties file) and writes these fields before calling load().
public class Config
{
    public static final int DEFAULT_BIOME_WEIGHT = 20;
    public static final int DEFAULT_SECONDARY_BIOME_WEIGHT = 20;
    public static final int DEFAULT_RARE_BIOME_WEIGHT = 10;

    public static final boolean DEFAULT_WEAKER_PIRANHAS = false;

    public static int biomeWeight = DEFAULT_BIOME_WEIGHT;
    public static int secondaryBiomeWeight = DEFAULT_SECONDARY_BIOME_WEIGHT;
    public static int rareBiomeWeight = DEFAULT_RARE_BIOME_WEIGHT;

    // When true, piranhas deal less attack damage and spawn less frequently (PiranhaTuning.WEAKER_DAMAGE_MULTIPLIER/WEAKER_SPAWN_CHANCE).
    public static boolean weakerPiranhas = DEFAULT_WEAKER_PIRANHAS;

    public static void load()
    {
        ModTerrablender.registerBiomes();
    }
}
