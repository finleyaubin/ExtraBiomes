package net.winepicfin.extrabiomes.worldgen.features.glacier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Port of the Bedrock "extrabiomes:glacier/*" feature set:
 * <ul>
 *     <li>glacier_ice_feature / glacier_packed_ice_feature / glacier_top_ice_feature -
 *         {@code minecraft:ore_feature} entries that replace stone-family/dirt/sand blocks with
 *         ice, packed ice, and ice respectively, gated on the "glacier" biome tag.</li>
 *     <li>select_snow_drift_feature - a {@code minecraft:weighted_random_feature} between sixteen
 *         snow_drift_N_feature entries (weights 20:10:20:20:10:20:20:20:20:10:10:10:10:10:6:3 for drifts 1-16), each a
 *         {@code minecraft:structure_template_feature}, gated on the broader "frozen" biome tag
 *         (Glacier, ColdMesa/ColdMesaBryce/ColdMesaPlateau, ShatteredTiagaSpikes, TiagaSpikes).
 *         snow_drift_1/2 are the original spiky hash-noise drifts; snow_drift_3-6 (ridge dune,
 *         rounded mound, s-curve wave bank, low broad drift) were added later, generated from a
 *         smooth sine-summed height field (see tools/build_snow_drifts.py) for a more natural,
 *         wind-sculpted look instead of per-voxel jaggedness. snow_drift_7-11 (cornice ridge,
 *         sastrugi field, barchan crescent, moraine drift, giant swirl) extend that with powder
 *         snow, wind-scoured ice and glacial debris; snow_drift_12-15 (twisting spire, breaking
 *         wave, spiral cone, 40-tall giant spire, 150-tall colossal spire) are the tall ones. Java-only for now: generated with
 *         {@code tools/build_snow_drifts.py --java}.</li>
 * </ul>
 * Bedrock source: "ExtraBiomes - Bedrock/packs/BP/features/glacier/*.json" +
 * "ExtraBiomes - Bedrock/packs/BP/feature_rules/glacier/*.json".
 * <p>
 * The snow-drift structures reuse the "structurescatter" subsystem's shared
 * SingleStructureFeature/SingleStructureConfiguration infrastructure rather than defining a new
 * Feature class. Since Feature.RANDOM_SELECTOR's RandomFeatureConfiguration needs a
 * Holder&lt;PlacedFeature&gt; per sub-feature (not a registry key), each sub-feature is built
 * as an unregistered inline holder via {@link PlacementUtils#inlinePlaced} - exactly the pattern
 * vanilla itself uses for its own weighted/degenerate features (see e.g. vanilla's
 * TreePlacements) - rather than going through the CONFIGURED_FEATURE/PLACED_FEATURE registries,
 * which would create a registration-order problem (PLACED_FEATURE bootstrap normally runs after
 * CONFIGURED_FEATURE bootstrap, so a same-pass lookup of a not-yet-registered PlacedFeature would
 * fail).
 * <p>
 * RandomFeatureConfiguration evaluates its weighted entries sequentially (each a raw
 * random.nextFloat() &lt; chance test, not a normalized weight), so the target weight ratios are
 * converted to conditional chances up front: chance_i = w_i / (sum of weights from entry i on).
 */
public class GlacierFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> GLACIER_ICE_KEY =
            configuredKey("glacier_ice");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLACIER_PACKED_ICE_KEY =
            configuredKey("glacier_packed_ice");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLACIER_TOP_ICE_KEY =
            configuredKey("glacier_top_ice");

    public static final ResourceKey<PlacedFeature> GLACIER_ICE_PLACED_KEY =
            placedKey("glacier_ice");
    public static final ResourceKey<PlacedFeature> GLACIER_PACKED_ICE_PLACED_KEY =
            placedKey("glacier_packed_ice");
    public static final ResourceKey<PlacedFeature> GLACIER_TOP_ICE_PLACED_KEY =
            placedKey("glacier_top_ice");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_SNOW_DRIFT_KEY =
            configuredKey("select_snow_drift");
    /** This is the key the biome-wiring pass should addFeature(...) with. */
    public static final ResourceKey<PlacedFeature> SELECT_SNOW_DRIFT_PLACED_KEY =
            placedKey("select_snow_drift");

    private static final int SNOW_DRIFT_GROUND_OFFSET = -2;
    // Draped drifts follow the ground column by column, so they don't need sinking to hide dips and sit directly on the surface.
    private static final int DRAPED_SNOW_DRIFT_GROUND_OFFSET = 0;

    private static List<OreConfiguration.TargetBlockState> iceTargets(BlockState result) {
        RuleTest[] sources = new RuleTest[] {
                new BlockMatchTest(Blocks.STONE),
                new BlockMatchTest(Blocks.GRANITE),
                new BlockMatchTest(Blocks.ANDESITE),
                new BlockMatchTest(Blocks.DIORITE),
                new BlockMatchTest(Blocks.DIRT),
                new BlockMatchTest(Blocks.GRASS_BLOCK),
                new BlockMatchTest(Blocks.SAND),
                new BlockMatchTest(Blocks.GRAVEL),
                new BlockMatchTest(Blocks.SANDSTONE),
                new BlockMatchTest(Blocks.DEEPSLATE),
        };
        return Arrays.stream(sources).map(test -> OreConfiguration.target(test, result)).toList();
    }

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // OreConfiguration's vein-size codec caps at 64, so the packed/top ice veins (90/110 in Bedrock) are clamped.
        context.register(GLACIER_ICE_KEY, new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(iceTargets(Blocks.ICE.defaultBlockState()), 30, 0.0F)));
        context.register(GLACIER_PACKED_ICE_KEY, new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(iceTargets(Blocks.PACKED_ICE.defaultBlockState()), 64, 0.0F)));
        context.register(GLACIER_TOP_ICE_KEY, new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(iceTargets(Blocks.ICE.defaultBlockState()), 64, 0.0F)));

        // SNOW_DRIFT_GROUND_OFFSET sinks the wide, unevenly-shaped drift templates into the ground so uneven terrain under them doesn't read as floating (same technique as OasisPuddleFeature's -4).
        // Keep in sync with SELECT_WEIGHTS in tools/build_snow_drifts.py.
        int[] snowDriftWeights = {20, 10, 20, 20, 10, 20, 20, 20, 20, 10, 10, 10, 10, 10, 6, 3};

        List<Holder<PlacedFeature>> snowDriftPlaced = new ArrayList<>();
        for (int i = 0; i < snowDriftWeights.length; i++) {
            ResourceLocation structure = new ResourceLocation(ExtraBiomes.MOD_ID, "glacier/snow_drift_" + (i + 1));
            // Drifts 3+ have a flat full-footprint base that floats over any dip, so drape them over the terrain; centering keeps the ~30-wide swirl and giant spire inside the feature write window.
            boolean drapesOverTerrain = i >= 2;
            SingleStructureConfiguration config = drapesOverTerrain
                    ? new SingleStructureConfiguration(structure, DRAPED_SNOW_DRIFT_GROUND_OFFSET, true, true)
                    : new SingleStructureConfiguration(structure, SNOW_DRIFT_GROUND_OFFSET);
            Holder<ConfiguredFeature<?, ?>> snowDrift = Holder.direct(new ConfiguredFeature<>(ModStructureScatterFeatures.SINGLE_STRUCTURE.get(), config));
            // inlinePlaced avoids a registration-order problem: PLACED_FEATURE bootstrap runs after CONFIGURED_FEATURE, so these sub-features can't go through the registry here.
            snowDriftPlaced.add(PlacementUtils.inlinePlaced(snowDrift));
        }

        // RandomFeatureConfiguration tries entries in order with raw chances, so each weight becomes its share of the mass not yet claimed; the last drift is the default.
        List<WeightedPlacedFeature> snowDriftEntries = new ArrayList<>();
        int remainingWeight = Arrays.stream(snowDriftWeights).sum();
        for (int i = 0; i < snowDriftWeights.length - 1; i++) {
            snowDriftEntries.add(new WeightedPlacedFeature(snowDriftPlaced.get(i), (float) snowDriftWeights[i] / remainingWeight));
            remainingWeight -= snowDriftWeights[i];
        }
        context.register(SELECT_SNOW_DRIFT_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR,
                new RandomFeatureConfiguration(snowDriftEntries, snowDriftPlaced.get(snowDriftPlaced.size() - 1))));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // glacier_ice/glacier_packed_ice span the full underground range (-64..100) so they're wired at UNDERGROUND_ORES in the biome; glacier_top_ice only spans 64..100 so it's wired at LOCAL_MODIFICATIONS instead.
        context.register(GLACIER_ICE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(GLACIER_ICE_KEY),
                List.of(
                        CountPlacement.of(15),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(100)),
                        BiomeFilter.biome()
                )));
        context.register(GLACIER_PACKED_ICE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(GLACIER_PACKED_ICE_KEY),
                List.of(
                        CountPlacement.of(70),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(100)),
                        BiomeFilter.biome()
                )));
        context.register(GLACIER_TOP_ICE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(GLACIER_TOP_ICE_KEY),
                List.of(
                        CountPlacement.of(60),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(100)),
                        BiomeFilter.biome()
                )));

        // OCEAN_FLOOR_WG (not WORLD_SURFACE_WG) ignores fluids so a drift lands on a frozen lake's bed, not floats on its water column; rarity reduced from the literal ~33% to ~12.5% since drifts read as too dense at the literal rate.
        context.register(SELECT_SNOW_DRIFT_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_SNOW_DRIFT_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(8),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new BlockPos(0, 0, 0), Blocks.AIR, Blocks.SNOW)),
                        BiomeFilter.biome()
                )));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
