package net.winepicfin.extrabiomes.worldgen.features.taigaspike;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;

/**
 * Port of Bedrock's "taiga_spike" subsystem, converted from:
 * <pre>
 * features/taiga_spike/select_spike.json (minecraft:weighted_random_feature):
 *   weighted 1:1:7 between taiga_spike/spike_1 / spike_2 / spike_3
 * features/taiga_spike/spike_{1,2,3}.json (minecraft:structure_template_feature):
 *   structure_name: "extrabiomes:ice_spikes/{1,2,3}"
 *   constraints.block_intersection.block_allowlist:
 *     [air, water, snow_layer, grass_block, snow, dirt, stone]
 * feature_rules/taiga_spike/taiga_spike_feature.json:
 *   biome_filter: all_of has_biome_tag taiga + frozen + mutated (i.e. Shattered/Ice Spikes taiga)
 *   distribution: iterations 1, x/z uniform [0,16],
 *                 y = query.above_top_solid(worldx, worldz) + uniform[-13, -4]
 * </pre>
 *
 * WHY THIS WAS PORTED RATHER THAN KEPT AS THE VANILLA STAND-IN:
 * dump_structure.py on the three converted .mcstructure files shows they are small, fully
 * hand-authored packed_ice + snow_layer shapes (spike_1: 7x44x7, spike_2: 7x76x7, spike_3: 5x14x5),
 * not vanilla's procedurally-generated {@code Feature.ICE_SPIKE} column (which uses a randomized
 * per-placement radius/height algorithm with no fixed silhouette). Bedrock also intentionally biases
 * heavily toward the small spike_3 shape (weight 7 of 9) with the two larger, more elaborate shapes
 * (spike_1, spike_2 - the latter taller than any vanilla ice spike ever generates) as rare accents.
 * That's a meaningfully different, authored visual identity from vanilla's uniform noise-based spikes,
 * so it is ported properly here rather than left as the vanilla {@code minecraft:ice_spike} stand-in.
 * <p>
 * Each structure was converted with tools/mc2java.py into
 * data/extrabiomes/structures/taigaspike/spike_{1,2,3}.nbt (block ids minecraft:packed_ice and
 * minecraft:snow_layer both map cleanly - zero warnings from the converter).
 * <p>
 * Reuses the shared {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature}/
 * {@link SingleStructureConfiguration} infra (one ConfiguredFeature per spike variant, random rotation
 * since Bedrock's structure_template_feature entries specify no fixed facing_direction), wrapped in a
 * single vanilla {@link Feature#RANDOM_SELECTOR} ConfiguredFeature for the 1:1:7 weighting (as a
 * sequential-trial chain: spike_1 chance 1/9, spike_2 chance (1/9)/(8/9)=1/8 conditional on spike_1
 * not being chosen, spike_3 as the guaranteed RANDOM_SELECTOR default - this reproduces the exact
 * same 1:1:7 marginal probabilities as Bedrock's true weighted pick, the same trick already used by
 * StonePillarsFeature/BoulderFeatures/MesaFeatures/MushroomFeatures in this codebase).
 * <p>
 * Bedrock's y = above_top_solid + uniform[-13, -4] (a *range* of embed depths, unlike stone_pillars'
 * fixed -5) is reproduced with {@code HeightmapPlacement.onHeightmap(WORLD_SURFACE_WG)} followed by
 * {@code RandomOffsetPlacement.vertical(UniformInt.of(-13, -4))} - groundOffset in
 * SingleStructureConfiguration is left at 0 for all three variants since the vertical randomness is
 * handled by the placement modifier instead.
 * <p>
 * Bedrock's constraints.block_intersection.block_allowlist [air, water, snow_layer, grass_block, snow,
 * dirt, stone] is approximated with a BlockPredicateFilter testing the block at the (randomized)
 * placement origin itself against that same block list - this list is broad enough (essentially "any
 * common taiga surface/near-surface block") that it is a light touch rather than a hard restriction,
 * matching Bedrock's intent of "don't stamp a spike through a cave/structure/water body unexpectedly".
 */
public class TaigaSpikeFeatures {
    private static final ResourceKey<ConfiguredFeature<?, ?>> SPIKE_1_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taiga_spike_1"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> SPIKE_2_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taiga_spike_2"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> SPIKE_3_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taiga_spike_3"));

    private static final ResourceKey<PlacedFeature> SPIKE_1_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taiga_spike_1"));
    private static final ResourceKey<PlacedFeature> SPIKE_2_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taiga_spike_2"));
    private static final ResourceKey<PlacedFeature> SPIKE_3_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taiga_spike_3"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_TAIGA_SPIKE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "select_taiga_spike"));
    public static final ResourceKey<PlacedFeature> TAIGA_SPIKE_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "select_taiga_spike"));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(SPIKE_1_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taigaspike/spike_1"))
        ));
        context.register(SPIKE_2_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taigaspike/spike_2"))
        ));
        context.register(SPIKE_3_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "taigaspike/spike_3"))
        ));

        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        context.register(SELECT_TAIGA_SPIKE_KEY, new ConfiguredFeature<>(
                Feature.RANDOM_SELECTOR,
                new RandomFeatureConfiguration(
                        List.of(
                                new WeightedPlacedFeature(placedFeatures.getOrThrow(SPIKE_1_PLACED_KEY), 1.0f / 9.0f),
                                new WeightedPlacedFeature(placedFeatures.getOrThrow(SPIKE_2_PLACED_KEY), 1.0f / 8.0f)
                        ),
                        placedFeatures.getOrThrow(SPIKE_3_PLACED_KEY)
                )
        ));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Shared origin-block constraint for all three variants, mirroring Bedrock's block_intersection.block_allowlist.
        List<PlacementModifier> perSpikeModifiers = List.of(
                BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                        BlockPos.ZERO,
                        Blocks.AIR, Blocks.WATER, Blocks.SNOW, Blocks.GRASS_BLOCK, Blocks.SNOW_BLOCK, Blocks.DIRT, Blocks.STONE
                ))
        );

        context.register(SPIKE_1_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SPIKE_1_KEY), perSpikeModifiers));
        context.register(SPIKE_2_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SPIKE_2_KEY), perSpikeModifiers));
        context.register(SPIKE_3_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SPIKE_3_KEY), perSpikeModifiers));

        context.register(TAIGA_SPIKE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_TAIGA_SPIKE_KEY),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        RandomOffsetPlacement.vertical(UniformInt.of(-13, -4)),
                        BiomeFilter.biome()
                )
        ));
    }
}
