package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Bedrock's wheat-field subsystem: "extrabiomes:the_netherlands/wheat_big" / "wheat_small"
 * ({@code minecraft:structure_template_feature}, each a single-block .mcstructure - verified from the
 * block_palette: minecraft:wheat with growth 7 / growth 4), "select_wheat" ({@code minecraft:weighted_random_feature},
 * wheat_big:wheat_small = 5:3), and "wheat_floor_feature" ({@code minecraft:vegetation_patch_feature}) wrapping it,
 * placed via feature_rules "extrabiomes:netherlands_wheat_feature" - which is gated on {@code has_biome_tag "mutated"}
 * in ADDITION to "the_netherlands", i.e. this only applies to TheNetherlandsMutated, not the base TheNetherlands biome.
 * <p>
 * wheat_big -> Blocks.WHEAT with CropBlock.AGE = 7 (fully grown). wheat_small -> CropBlock.AGE = 4.
 * select_wheat's 5:3 weight ratio -> a single {@link WeightedPlacedFeature} of chance 5/8 = 0.625 for wheat_big,
 * falling through to wheat_small as the {@link RandomFeatureConfiguration} default (3/8 = 0.375 remaining chance) -
 * this exactly reproduces the ratio since Feature.RANDOM_SELECTOR rolls each listed WeightedPlacedFeature in order
 * and falls back to the default once none hit.
 * <p>
 * {@code replaceable_blocks: [minecraft:farmland, minecraft:dirt]} -> extrabiomes:netherlands_wheat_replaceable tag.
 * {@code ground_block: minecraft:farmland} -> BlockStateProvider.simple(Blocks.FARMLAND).
 * {@code depth: 1-1} -> ConstantInt.of(1), {@code vertical_range: 2} -> 2, {@code vegetation_chance: 1} -> 1.0F,
 * {@code horizontal_radius: 1-10} -> UniformInt.of(1, 10), {@code extra_edge_column_chance: 0} -> 0.0F.
 * <p>
 * SIMPLIFICATION: feature_rules distribution's y = uniform[heightmap, heightmap+3] (a vertical search band above
 * the surface) has no direct PlacementModifier equivalent for a floor-surface vegetation patch; it is absorbed into
 * the VegetationPatchConfiguration's own vertical_range=2 (which already makes the feature search up/down for valid
 * ground near the heightmap placement point), so the PlacedFeature itself places on HeightmapPlacement.onHeightmap(WORLD_SURFACE_WG).
 */
public class NetherlandsWheatFeatures {
    public static final TagKey<Block> WHEAT_REPLACEABLE = TagKey.create(Registries.BLOCK, new ResourceLocation(ExtraBiomes.MOD_ID, "netherlands_wheat_replaceable"));

    private static final ResourceKey<ConfiguredFeature<?, ?>> WHEAT_BIG_KEY = key("netherlands_wheat_big");
    private static final ResourceKey<ConfiguredFeature<?, ?>> WHEAT_SMALL_KEY = key("netherlands_wheat_small");
    private static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_WHEAT_KEY = key("netherlands_select_wheat");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WHEAT_FLOOR_KEY = key("netherlands_wheat_floor");

    private static final ResourceKey<PlacedFeature> WHEAT_BIG_PLACED_KEY = placedKey("netherlands_wheat_big");
    private static final ResourceKey<PlacedFeature> WHEAT_SMALL_PLACED_KEY = placedKey("netherlands_wheat_small");
    private static final ResourceKey<PlacedFeature> SELECT_WHEAT_PLACED_KEY = placedKey("netherlands_select_wheat");
    public static final ResourceKey<PlacedFeature> WHEAT_FLOOR_PLACED_KEY = placedKey("netherlands_wheat_floor");

    // Not from Bedrock - buried water source added per playtest feedback so farmland out of NetherlandsWaterFeature's canal reach doesn't revert to dirt and kill its crop.
    private static final ResourceKey<ConfiguredFeature<?, ?>> HYDRATION_WATER_KEY = key("netherlands_hydration_water");
    public static final ResourceKey<PlacedFeature> HYDRATION_WATER_PLACED_KEY = placedKey("netherlands_hydration_water");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(WHEAT_BIG_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7)))));
        context.register(WHEAT_SMALL_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 4)))));

        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        context.register(SELECT_WHEAT_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(placedFeatures.getOrThrow(WHEAT_BIG_PLACED_KEY), 0.625F)),
                placedFeatures.getOrThrow(WHEAT_SMALL_PLACED_KEY))));

        context.register(WHEAT_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                WHEAT_REPLACEABLE, BlockStateProvider.simple(Blocks.FARMLAND), placedFeatures.getOrThrow(SELECT_WHEAT_PLACED_KEY),
                CaveSurface.FLOOR, ConstantInt.of(1), 0.0F, 2, 1.0F, UniformInt.of(1, 10), 0.0F)));

        context.register(HYDRATION_WATER_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.WATER.defaultBlockState()))));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(WHEAT_BIG_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(WHEAT_BIG_KEY), List.of()));
        context.register(WHEAT_SMALL_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(WHEAT_SMALL_KEY), List.of()));
        context.register(SELECT_WHEAT_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SELECT_WHEAT_KEY), List.of()));

        List<PlacementModifier> scatter = List.of(
                CountPlacement.of(100), InSquarePlacement.spread(), HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome());
        context.register(WHEAT_FLOOR_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(WHEAT_FLOOR_KEY), scatter));

        // Scans down to the dirt beneath the farmland so the water always sits under a solid cap; the horizontal-neighbour check skips field edges to avoid exposing the water.
        List<PlacementModifier> hydration = List.of(
                CountPlacement.of(20),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.matchesBlocks(Blocks.DIRT),
                        BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.FARMLAND, Blocks.WHEAT),
                        6),
                BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                        BlockPredicate.not(BlockPredicate.matchesBlocks(new BlockPos(1, 0, 0), Blocks.AIR)),
                        BlockPredicate.not(BlockPredicate.matchesBlocks(new BlockPos(-1, 0, 0), Blocks.AIR)),
                        BlockPredicate.not(BlockPredicate.matchesBlocks(new BlockPos(0, 0, 1), Blocks.AIR)),
                        BlockPredicate.not(BlockPredicate.matchesBlocks(new BlockPos(0, 0, -1), Blocks.AIR)))),
                BiomeFilter.biome());
        context.register(HYDRATION_WATER_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(HYDRATION_WATER_KEY), hydration));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
