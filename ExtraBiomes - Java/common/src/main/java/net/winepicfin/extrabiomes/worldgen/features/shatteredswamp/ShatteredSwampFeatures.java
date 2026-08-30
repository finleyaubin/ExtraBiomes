package net.winepicfin.extrabiomes.worldgen.features.shatteredswamp;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Port of the Bedrock "shattered_swamp" bamboo feature subsystem:
 * <pre>
 * features/shattered_swamp/bamboo_feature.json          (minecraft:aggregate_feature wrapping a
 *                                                         single "minecraft:bamboo_feature" entry -
 *                                                         Bedrock's built-in vanilla-parity bamboo
 *                                                         generator, invoked with no extra config)
 * feature_rules/shattered_swamp/shattered_swamp_bamboo_feature.json
 *                                                        (tag "shattered_swamp", after_surface_pass,
 *                                                         iterations = clamp(..., 15, 15) i.e. always
 *                                                         exactly 15 per chunk, x/z uniform [0,16],
 *                                                         y = heightmap)
 * </pre>
 * <p>
 * This is a pure scatter of vanilla bamboo stalks - no unique config in the Bedrock JSON beyond the
 * iteration count, so it is ported directly onto vanilla's own {@link Feature#BAMBOO} feature/config
 * (the same one used by vanilla's bamboo jungle biomes) rather than reinventing bamboo placement:
 * <ul>
 *   <li>The wrapping "minecraft:aggregate_feature" has exactly one entry, so no
 *       Feature.RANDOM_SELECTOR/aggregation wrapper is needed here - per project convention this
 *       collapses to a single configured/placed feature pair.</li>
 *   <li>{@code minecraft:bamboo_feature} carries no probability parameter in this JSON, so the
 *       {@link ProbabilityFeatureConfiguration} probability (vanilla's {@link Feature#BAMBOO} config
 *       type) is set to vanilla's own default of {@code 0.2} (the same value vanilla's
 *       {@code bamboo_some_podzol.json} configured feature uses) - this is a simplification since
 *       Bedrock's internal bamboo generator's exact per-column density curve isn't exposed in the
 *       JSON to compare against.</li>
 *   <li>{@code iterations: clamp(..., 15, 15)} is a constant (the clamp's min and max are both 15,
 *       so the noise expression inside never has any effect) -> {@link CountPlacement#of(int)} with
 *       15.</li>
 *   <li>{@code x}/{@code z}: uniform over [0,16] -> {@link InSquarePlacement#spread()}.</li>
 *   <li>{@code y: query.heightmap(...)} -> {@link HeightmapPlacement#onHeightmap(Heightmap.Types)}
 *       using {@link Heightmap.Types#WORLD_SURFACE_WG}, matching vanilla's own bamboo placed
 *       feature's choice of heightmap type.</li>
 *   <li>{@code placement_pass: after_surface_pass} has no distinct Java equivalent step; ordinary
 *       {@code VEGETAL_DECORATION} generation-step placement (after surface/terrain shaping has
 *       already run) is the faithful equivalent.</li>
 * </ul>
 */
public class ShatteredSwampFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> BAMBOO_KEY = configuredKey("shattered_swamp_bamboo");
    public static final ResourceKey<PlacedFeature> BAMBOO_PLACED_KEY = placedKey("shattered_swamp_bamboo");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // No probability given in the Bedrock JSON, so this uses vanilla's own default bamboo density (same value as vanilla's "bamboo_some_podzol" configured feature).
        context.register(BAMBOO_KEY, new ConfiguredFeature<>(Feature.BAMBOO, new ProbabilityFeatureConfiguration(0.2F)));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(BAMBOO_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(BAMBOO_KEY),
                List.of(
                        CountPlacement.of(15),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "shatteredswamp/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "shatteredswamp/" + name));
    }
}
