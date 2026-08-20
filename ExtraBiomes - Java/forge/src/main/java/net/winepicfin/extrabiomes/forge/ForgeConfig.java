package net.winepicfin.extrabiomes.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.winepicfin.extrabiomes.Config;

// Forge's own config file/GUI backing for the loader-agnostic net.winepicfin.extrabiomes.Config
// values - ForgeConfigSpec has no Fabric equivalent, so this class (and its registration in
// ExtraBiomesForge) stays forge-only. See fabric/.../ExtraBiomesFabric for Fabric's own
// plain-properties-file equivalent.
public class ForgeConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue BIOMEWEIGHT = BUILDER
            .comment("The Weight of ExtraBiomes biomes, the default value is " + Config.DEFAULT_BIOME_WEIGHT)
            .defineInRange("Biome Weight", Config.DEFAULT_BIOME_WEIGHT, 0, Integer.MAX_VALUE);

    // Weight for ModOverworldRegionRare, the small set of biomes that are genuinely low-frequency
    // in the Bedrock source data (replace_biomes amount <= 0.10 - see that class's javadoc).
    private static final ForgeConfigSpec.IntValue RAREBIOMEWEIGHT = BUILDER
            .comment("The Weight of ExtraBiomes' rarest biomes (Mystic Forest, Jellyfish Fields, " +
                    "Future Desert, The Netherlands, The Netherlands Mutated), the default value is " + Config.DEFAULT_RARE_BIOME_WEIGHT)
            .defineInRange("Rare Biome Weight", Config.DEFAULT_RARE_BIOME_WEIGHT, 0, Integer.MAX_VALUE);

    // On the gametest server, a low weight makes narrow/rare biomes impractically hard to find
    // within a reasonable search radius (see BiomeGenerationGameTests), so force it high there
    // regardless of the configured value - real players never see this override.
    private static final int GAMETEST_BIOME_WEIGHT = 100;

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void load()
    {
        boolean isGametest = ForgeGameTestHooks.isGametestServer();
        Config.biomeWeight = isGametest ? GAMETEST_BIOME_WEIGHT : BIOMEWEIGHT.get();
        Config.rareBiomeWeight = isGametest ? GAMETEST_BIOME_WEIGHT : RAREBIOMEWEIGHT.get();
        Config.load();
    }
}
