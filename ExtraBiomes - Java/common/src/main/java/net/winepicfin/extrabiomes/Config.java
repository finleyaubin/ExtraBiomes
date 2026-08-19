package net.winepicfin.extrabiomes;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.winepicfin.extrabiomes.worldgen.biomes.ModTerrablender;

// The @Mod.EventBusSubscriber/ModConfigEvent wiring lives in ExtraBiomesForge (forge module),
// which calls load() below — those FML annotation-processor types only exist in Loom's
// specially patched Forge dev jar, not any plain downloadable Maven artifact.
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue BIOMEWEIGHT = BUILDER
            .comment("The Weight of ExtraBiomes biomes, the default value is 15")
            .defineInRange("Biome Weight", 15, 0, Integer.MAX_VALUE);

    // Weight for ModOverworldRegionRare, the small set of biomes that are genuinely low-frequency
    // in the Bedrock source data (replace_biomes amount <= 0.10 - see that class's javadoc).
    // Default of 4 is roughly proportional to Biome Weight's default of 15, scaled down by the
    // ratio of this region's average Bedrock amount (~0.086) to the primary region's (~0.32).
    private static final ForgeConfigSpec.IntValue RAREBIOMEWEIGHT = BUILDER
            .comment("The Weight of ExtraBiomes' rarest biomes (Mystic Forest, Jellyfish Fields, " +
                    "Future Desert, The Netherlands, The Netherlands Mutated), the default value is 4")
            .defineInRange("Rare Biome Weight", 4, 0, Integer.MAX_VALUE);

    // On the gametest server, a low weight makes narrow/rare biomes impractically hard to find
    // within a reasonable search radius (see BiomeGenerationGameTests), so force it high there
    // regardless of the configured value - real players never see this override.
    private static final int GAMETEST_BIOME_WEIGHT = 100;

    public static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int biomeWeight;
    public static int rareBiomeWeight;

    public static void load()
    {
        boolean isGametest = ForgeGameTestHooks.isGametestServer();
        biomeWeight = isGametest ? GAMETEST_BIOME_WEIGHT : BIOMEWEIGHT.get();
        rareBiomeWeight = isGametest ? GAMETEST_BIOME_WEIGHT : RAREBIOMEWEIGHT.get();
        System.out.println(biomeWeight+" is the biome weight On load");
        System.out.println(rareBiomeWeight+" is the rare biome weight On load");
        ModTerrablender.registerBiomes();

    }

}
