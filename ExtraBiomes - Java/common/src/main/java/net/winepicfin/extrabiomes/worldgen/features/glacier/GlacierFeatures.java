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

import java.util.Arrays;
import java.util.List;

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
    public static final ResourceKey<PlacedFeature> SELECT_SNOW_DRIFT_PLACED_KEY =
            placedKey("select_snow_drift");

    private static final int SNOW_DRIFT_GROUND_OFFSET = -2;

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
        Holder<ConfiguredFeature<?, ?>> snowDrift1 = Holder.direct(new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "glacier/snow_drift_1"), SNOW_DRIFT_GROUND_OFFSET)
        ));
        Holder<ConfiguredFeature<?, ?>> snowDrift2 = Holder.direct(new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "glacier/snow_drift_2"), SNOW_DRIFT_GROUND_OFFSET)
        ));
        // inlinePlaced avoids a registration-order problem: PLACED_FEATURE bootstrap runs after CONFIGURED_FEATURE, so these sub-features can't go through the registry here.
        Holder<PlacedFeature> snowDrift1Placed = PlacementUtils.inlinePlaced(snowDrift1);
        Holder<PlacedFeature> snowDrift2Placed = PlacementUtils.inlinePlaced(snowDrift2);

        // Weighted 2:1 selection is encoded as one entry with chance 2/3 plus a default feature that gets the remaining 1/3.
        context.register(SELECT_SNOW_DRIFT_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR,
                new RandomFeatureConfiguration(
                        List.of(new WeightedPlacedFeature(snowDrift1Placed, 2.0F / 3.0F)),
                        snowDrift2Placed
                )));
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

        // snow_drift.json: iterations 1, scatter_chance 30 (~30% per chunk) -> Bedrock's
        // percentage-based scatter_chance has no exact Java equivalent, so this is approximated with
        // RarityFilter; x/z uniform[0,16] -> InSquarePlacement.spread(); y = [heightmap, heightmap]
        // -> HeightmapPlacement on OCEAN_FLOOR_WG (ignores fluids, unlike WORLD_SURFACE_WG, which
        // would land a drift on top of a frozen lake's water column instead of its bed), combined
        // with the SNOW_DRIFT_GROUND_OFFSET groundOffset above (see snowDrift1/2 comment).
        // constraints.unburied + block_intersection.block_allowlist [air, snow_layer] -> a
        // BlockPredicateFilter testing the placement origin itself (0,0,0) must be air or (Java)
        // snow, i.e. the surface must be clear/snow-covered to place onto; constraints.grounded is
        // implicitly satisfied by HeightmapPlacement always landing on top of the first
        // solid/motion-blocking column.
        // Reduced from onAverageOnceEvery(3) (~33%, the literal reciprocal of Bedrock's 30%) to
        // onAverageOnceEvery(8) (~12.5%) - drifts were reading as too dense across glacier/frozen
        // biomes at the literal rate.
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
