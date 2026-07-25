package net.winepicfin.extrabiomes.worldgen.features.mushroom;

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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.block.Rotation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * Port of Bedrock's shared "huge mushroom select" chain
 * ("ExtraBiomes - Bedrock/packs/BP/features/mushroom/**") plus the underground
 * glow-mushroom and mycelium-floor chain ("feature_rules/underground_mushroom/*").
 * <p>
 * Bedrock source overview:
 * <ul>
 *   <li>{@code features/mushroom/select_huge_mushroom.json} ({@code minecraft:weighted_random_feature}):
 *       vanilla {@code minecraft:huge_mushroom_feature} weight 10, plus 11 custom colored huge mushroom
 *       structures ({@code huge_red_mushroom1}, {@code huge_brown_mushroom1}, {@code huge_purple_mushroom},
 *       {@code huge_blue_mushroom}, {@code huge_yellow_mushroom}, {@code huge_green_mushroom},
 *       {@code huge_cyan_mushroom}, {@code huge_white_mushroom}, {@code huge_black_mushroom},
 *       {@code huge_orange_mushroom}, {@code huge_glow_mushroom}) each weight 1 -> total weight 21.
 *       Each colored variant is a {@code minecraft:structure_template_feature} referencing a converted
 *       .nbt under {@code data/extrabiomes/structures/mushroom/}; {@code huge_brown_mushroom1} and
 *       {@code huge_red_mushroom1} pin {@code facing_direction: "south"} (fixed rotation), every other
 *       colored variant leaves it random.</li>
 *   <li>{@code feature_rules/mushroom_island_surface_huge_mushroom_feature.json}: places
 *       {@code select_huge_mushroom} once per chunk at {@code query.above_top_solid} for biomes tagged
 *       {@code mooshroom_island} -&gt; {@link #MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY}.</li>
 *   <li>{@code feature_rules/shattered_swamp/swamp_huge_mushroom_feature.json}: places
 *       {@code select_huge_mushroom} once per chunk (1/4 chance) at {@code query.above_top_solid} for
 *       biomes tagged {@code swamp} or {@code roofed} -&gt; {@link #SWAMP_HUGE_MUSHROOM_PLACED_KEY}.</li>
 *   <li>{@code feature_rules/underground_mushroom/huge_glow_mushroom_feature.json}: places ONLY
 *       {@code extrabiomes:mushroom/huge_glow_mushroom} (not the full weighted selector), 25
 *       iterations/chunk, y in [-64, heightmap-10], for overworld biomes -&gt;
 *       {@link #HUGE_GLOW_MUSHROOM_UNDERGROUND_PLACED_KEY}.</li>
 *   <li>{@code feature_rules/underground_mushroom/mushroom_surface_mycelium_floor_feature.json} (via
 *       {@code features/underground_mushroom/mycelium_floor_snap_to_floor_feature.json} ->
 *       {@code mycelium_floor_feature.json}, a {@code minecraft:vegetation_patch_feature}): scatters
 *       mycelium floor patches (400 iterations/chunk, y in [-64,60]) for {@code mooshroom_island} biomes,
 *       growing {@code features/underground_mushroom/select_mushroom_feature.json} on top of each patch
 *       cell - a {@code minecraft:weighted_random_feature} of {@code select_huge_mushroom} (weight 3)
 *       vs. vanilla {@code minecraft:legacy:small_mushrooms_feature} (weight 3) -&gt;
 *       {@link #MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY}.</li>
 * </ul>
 * <p>
 * <b>NOT ported (missing blocks, not invented here):</b> Bedrock's per-color small-mushroom scatter
 * chain - {@code features/mushroom/mushroom_custom_feature.json} (the "extra custom mushroom" weighted
 * selector), the 9 {@code features/mushroom/scatter_<color>_mushroom.json} /
 * {@code <color>_mushroom_patch.json} pairs, {@code features/mushroom/glow_mushroom_patch.json} /
 * {@code scatter_glow_mushroom.json}, and the two feature_rules that only exist to place
 * {@code mushroom_custom_feature} ({@code mushroom_island_custom_mushroom_feature.json} and
 * {@code overworld_surface_extra_custom_mushroom_feature.json}). Every one of these places a Bedrock
 * "{@code extrabiomes:<color>_mushroom_placed}" block - a small individual mushroom decoration distinct
 * from the huge-mushroom cap blocks - and NONE of those "_placed" blocks exist in ModBlocks (only the
 * 9 huge-mushroom CAP blocks do: BLACK/BLUE/CYAN/GREEN/ORANGE/PURPLE/WHITE/YELLOW/GLOW_MUSHROOM_BLOCK,
 * which this class already reuses for the huge structures above). Per project convention, no new blocks
 * were invented to cover this gap - add the missing small mushroom blocks to ModBlocks first, then wire
 * up this second chain the same way as {@link #SELECT_HUGE_MUSHROOM_KEY} below.
 */
public class MushroomFeatures {

    // -- huge mushroom "building blocks": one raw structure placement per colored variant -----------
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_BLACK_MUSHROOM_KEY = cfKey("huge_black_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_BLUE_MUSHROOM_KEY = cfKey("huge_blue_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_BROWN_MUSHROOM1_KEY = cfKey("huge_brown_mushroom1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_CYAN_MUSHROOM_KEY = cfKey("huge_cyan_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_GLOW_MUSHROOM_KEY = cfKey("huge_glow_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_GREEN_MUSHROOM_KEY = cfKey("huge_green_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_ORANGE_MUSHROOM_KEY = cfKey("huge_orange_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_PURPLE_MUSHROOM_KEY = cfKey("huge_purple_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_RED_MUSHROOM1_KEY = cfKey("huge_red_mushroom1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_WHITE_MUSHROOM_KEY = cfKey("huge_white_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_YELLOW_MUSHROOM_KEY = cfKey("huge_yellow_mushroom");

    public static final ResourceKey<PlacedFeature> HUGE_BLACK_MUSHROOM_PLACED_KEY = pfKey("huge_black_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_BLUE_MUSHROOM_PLACED_KEY = pfKey("huge_blue_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_BROWN_MUSHROOM1_PLACED_KEY = pfKey("huge_brown_mushroom1");
    public static final ResourceKey<PlacedFeature> HUGE_CYAN_MUSHROOM_PLACED_KEY = pfKey("huge_cyan_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_GLOW_MUSHROOM_PLACED_KEY = pfKey("huge_glow_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_GREEN_MUSHROOM_PLACED_KEY = pfKey("huge_green_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_ORANGE_MUSHROOM_PLACED_KEY = pfKey("huge_orange_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_PURPLE_MUSHROOM_PLACED_KEY = pfKey("huge_purple_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_RED_MUSHROOM1_PLACED_KEY = pfKey("huge_red_mushroom1");
    public static final ResourceKey<PlacedFeature> HUGE_WHITE_MUSHROOM_PLACED_KEY = pfKey("huge_white_mushroom");
    public static final ResourceKey<PlacedFeature> HUGE_YELLOW_MUSHROOM_PLACED_KEY = pfKey("huge_yellow_mushroom");

    // -- vanilla huge red/brown mushroom, re-registered locally so they can sit in our own
    //    RANDOM_SELECTOR alongside the colored variants (mirrors vanilla's own defaults). ------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_HUGE_RED_MUSHROOM_KEY = cfKey("vanilla_huge_red_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_HUGE_BROWN_MUSHROOM_KEY = cfKey("vanilla_huge_brown_mushroom");
    public static final ResourceKey<PlacedFeature> VANILLA_HUGE_RED_MUSHROOM_PLACED_KEY = pfKey("vanilla_huge_red_mushroom");
    public static final ResourceKey<PlacedFeature> VANILLA_HUGE_BROWN_MUSHROOM_PLACED_KEY = pfKey("vanilla_huge_brown_mushroom");

    // -- the shared master selector ("select_huge_mushroom") -----------------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_HUGE_MUSHROOM_KEY = cfKey("select_huge_mushroom");
    /** Generic placement (just a biome filter) - use this as an ingredient of other features. */
    public static final ResourceKey<PlacedFeature> SELECT_HUGE_MUSHROOM_PLACED_KEY = pfKey("select_huge_mushroom");
    /** Bedrock's {@code mushroom_island_surface_huge_mushroom_feature.json} distribution. */
    public static final ResourceKey<PlacedFeature> MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY = pfKey("mushroom_island_huge_mushroom");
    /** Bedrock's {@code shattered_swamp/swamp_huge_mushroom_feature.json} distribution (1/4 chance, once per chunk, on the surface). */
    public static final ResourceKey<PlacedFeature> SWAMP_HUGE_MUSHROOM_PLACED_KEY = pfKey("swamp_huge_mushroom");

    // -- standalone underground glow-mushroom scatter (huge_glow_mushroom_feature.json) ---------------
    public static final ResourceKey<PlacedFeature> HUGE_GLOW_MUSHROOM_UNDERGROUND_PLACED_KEY = pfKey("huge_glow_mushroom_underground");

    // -- vanilla small red/brown mushroom stand-in for "minecraft:legacy:small_mushrooms_feature" -----
    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_SMALL_RED_MUSHROOM_KEY = cfKey("vanilla_small_red_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_SMALL_BROWN_MUSHROOM_KEY = cfKey("vanilla_small_brown_mushroom");
    public static final ResourceKey<PlacedFeature> VANILLA_SMALL_RED_MUSHROOM_PLACED_KEY = pfKey("vanilla_small_red_mushroom");
    public static final ResourceKey<PlacedFeature> VANILLA_SMALL_BROWN_MUSHROOM_PLACED_KEY = pfKey("vanilla_small_brown_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_SMALL_MUSHROOM_KEY = cfKey("vanilla_small_mushroom");
    public static final ResourceKey<PlacedFeature> VANILLA_SMALL_MUSHROOM_PLACED_KEY = pfKey("vanilla_small_mushroom");

    // -- underground_mushroom/select_mushroom_feature.json: select_huge_mushroom (3) vs small (3) -----
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_MUSHROOM_KEY = cfKey("select_mushroom");
    public static final ResourceKey<PlacedFeature> SELECT_MUSHROOM_PLACED_KEY = pfKey("select_mushroom");

    // -- underground_mushroom/mycelium_floor_feature.json (vegetation_patch_feature) ------------------
    public static final TagKey<Block> MYCELIUM_FLOOR_REPLACEABLE = TagKey.create(Registries.BLOCK,
            new ResourceLocation(ExtraBiomes.MOD_ID, "mycelium_floor_replaceable"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYCELIUM_FLOOR_KEY = cfKey("mycelium_floor");
    /** Bedrock's {@code mushroom_surface_mycelium_floor_feature.json} distribution. */
    public static final ResourceKey<PlacedFeature> MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY = pfKey("mushroom_surface_mycelium_floor");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // -- the 11 colored huge mushroom structures --------------------------------------------------
        registerStructure(context, HUGE_BLACK_MUSHROOM_KEY, "huge_black_mushroom", Optional.empty());
        registerStructure(context, HUGE_BLUE_MUSHROOM_KEY, "huge_blue_mushroom", Optional.empty());
        registerStructure(context, HUGE_BROWN_MUSHROOM1_KEY, "huge_brown_mushroom1", Optional.of(Rotation.NONE));
        registerStructure(context, HUGE_CYAN_MUSHROOM_KEY, "huge_cyan_mushroom", Optional.empty());
        registerStructure(context, HUGE_GLOW_MUSHROOM_KEY, "huge_glow_mushroom", Optional.empty());
        registerStructure(context, HUGE_GREEN_MUSHROOM_KEY, "huge_green_mushroom", Optional.empty());
        registerStructure(context, HUGE_ORANGE_MUSHROOM_KEY, "huge_orange_mushroom", Optional.empty());
        registerStructure(context, HUGE_PURPLE_MUSHROOM_KEY, "huge_purple_mushroom", Optional.empty());
        registerStructure(context, HUGE_RED_MUSHROOM1_KEY, "huge_red_mushroom1", Optional.of(Rotation.NONE));
        registerStructure(context, HUGE_WHITE_MUSHROOM_KEY, "huge_white_mushroom", Optional.empty());
        registerStructure(context, HUGE_YELLOW_MUSHROOM_KEY, "huge_yellow_mushroom", Optional.empty());

        // -- vanilla huge red/brown mushroom, mirroring vanilla's own defaults -------------------------
        register(context, VANILLA_HUGE_RED_MUSHROOM_KEY, Feature.HUGE_RED_MUSHROOM,
                new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(Blocks.RED_MUSHROOM_BLOCK), BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 2));
        register(context, VANILLA_HUGE_BROWN_MUSHROOM_KEY, Feature.HUGE_BROWN_MUSHROOM,
                new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(Blocks.BROWN_MUSHROOM_BLOCK), BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3));

        // -- select_huge_mushroom.json: weighted_random_feature, 11 colored variants (1 each) + vanilla
        //    huge_mushroom_feature (10, split evenly 5/5 between red and brown to emulate its internal
        //    50/50 pick since Java has no single combined "either huge mushroom" feature). Total weight
        //    21. RandomFeatureConfiguration is a SEQUENCE of independent Bernoulli trials (first hit
        //    wins), so weight w_i at position i (with W_i weight remaining from i onward) becomes
        //    chance = w_i / W_i; the final entry is folded into the mandatory "default" (guaranteed if
        //    every earlier trial missed, which is exactly its correct probability). ------------------
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        register(context, SELECT_HUGE_MUSHROOM_KEY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_BLACK_MUSHROOM_PLACED_KEY), 1f / 21f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_BLUE_MUSHROOM_PLACED_KEY), 1f / 20f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_BROWN_MUSHROOM1_PLACED_KEY), 1f / 19f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_CYAN_MUSHROOM_PLACED_KEY), 1f / 18f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_GLOW_MUSHROOM_PLACED_KEY), 1f / 17f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_GREEN_MUSHROOM_PLACED_KEY), 1f / 16f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_ORANGE_MUSHROOM_PLACED_KEY), 1f / 15f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_PURPLE_MUSHROOM_PLACED_KEY), 1f / 14f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_RED_MUSHROOM1_PLACED_KEY), 1f / 13f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_WHITE_MUSHROOM_PLACED_KEY), 1f / 12f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(HUGE_YELLOW_MUSHROOM_PLACED_KEY), 1f / 11f),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(VANILLA_HUGE_RED_MUSHROOM_PLACED_KEY), 5f / 10f)
                ),
                placedFeatures.getOrThrow(VANILLA_HUGE_BROWN_MUSHROOM_PLACED_KEY)
        ));

        // -- vanilla small mushroom stand-in for Bedrock's "minecraft:legacy:small_mushrooms_feature" --
        register(context, VANILLA_SMALL_RED_MUSHROOM_KEY, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.RED_MUSHROOM)));
        register(context, VANILLA_SMALL_BROWN_MUSHROOM_KEY, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.BROWN_MUSHROOM)));
        HolderGetter<PlacedFeature> placedFeatures2 = context.lookup(Registries.PLACED_FEATURE);
        register(context, VANILLA_SMALL_MUSHROOM_KEY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(placedFeatures2.getOrThrow(VANILLA_SMALL_RED_MUSHROOM_PLACED_KEY), 0.5f)),
                placedFeatures2.getOrThrow(VANILLA_SMALL_BROWN_MUSHROOM_PLACED_KEY)
        ));

        // -- underground_mushroom/select_mushroom_feature.json: select_huge_mushroom (3) vs small (3) --
        HolderGetter<PlacedFeature> placedFeatures3 = context.lookup(Registries.PLACED_FEATURE);
        register(context, SELECT_MUSHROOM_KEY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(placedFeatures3.getOrThrow(SELECT_HUGE_MUSHROOM_PLACED_KEY), 0.5f)),
                placedFeatures3.getOrThrow(VANILLA_SMALL_MUSHROOM_PLACED_KEY)
        ));

        // -- underground_mushroom/mycelium_floor_feature.json (vegetation_patch_feature) ---------------
        HolderGetter<PlacedFeature> placedFeatures4 = context.lookup(Registries.PLACED_FEATURE);
        // VegetationPatchConfiguration field order is (replaceable, groundState, vegetationFeature,
        // surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius,
        // extraEdgeColumnChance) - a prior pass here had verticalRange/extraEdgeColumnChance swapped
        // and zeroed, which fails datagen ("Value 0 outside of range [1:256]; Value 5.0 outside of
        // range [0.0:1.0]"). Bedrock's mycelium_floor_feature.json: vertical_range 5, vegetation_chance
        // 0.008, horizontal_radius 4-8, extra_edge_column_chance 0.3 (extraBottomBlockChance has no
        // Bedrock equivalent, left at 0).
        register(context, MYCELIUM_FLOOR_KEY, Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                MYCELIUM_FLOOR_REPLACEABLE,
                BlockStateProvider.simple(Blocks.MYCELIUM),
                placedFeatures4.getOrThrow(SELECT_MUSHROOM_PLACED_KEY),
                CaveSurface.FLOOR,
                ConstantInt.of(1),
                0.0f,
                5,
                0.008f,
                UniformInt.of(4, 8),
                0.3f
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // building-block placements: no extra modifiers, they are only ever placed by a wrapping
        // ConfiguredFeature (RANDOM_SELECTOR) or by another PlacedFeature that supplies its own
        // distribution, exactly mirroring how Bedrock's constraints (unburied/grounded) live on the
        // structure_template_feature itself rather than on the distribution.
        register(context, HUGE_BLACK_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_BLACK_MUSHROOM_KEY));
        register(context, HUGE_BLUE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_BLUE_MUSHROOM_KEY));
        register(context, HUGE_BROWN_MUSHROOM1_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_BROWN_MUSHROOM1_KEY));
        register(context, HUGE_CYAN_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_CYAN_MUSHROOM_KEY));
        register(context, HUGE_GLOW_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_GLOW_MUSHROOM_KEY));
        register(context, HUGE_GREEN_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_GREEN_MUSHROOM_KEY));
        register(context, HUGE_ORANGE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_ORANGE_MUSHROOM_KEY));
        register(context, HUGE_PURPLE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_PURPLE_MUSHROOM_KEY));
        register(context, HUGE_RED_MUSHROOM1_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_RED_MUSHROOM1_KEY));
        register(context, HUGE_WHITE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_WHITE_MUSHROOM_KEY));
        register(context, HUGE_YELLOW_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_YELLOW_MUSHROOM_KEY));
        register(context, VANILLA_HUGE_RED_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(VANILLA_HUGE_RED_MUSHROOM_KEY));
        register(context, VANILLA_HUGE_BROWN_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(VANILLA_HUGE_BROWN_MUSHROOM_KEY));
        register(context, VANILLA_SMALL_RED_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(VANILLA_SMALL_RED_MUSHROOM_KEY));
        register(context, VANILLA_SMALL_BROWN_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(VANILLA_SMALL_BROWN_MUSHROOM_KEY));
        register(context, VANILLA_SMALL_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(VANILLA_SMALL_MUSHROOM_KEY));

        // select_huge_mushroom, generic (no distribution baked in) - use as a vegetation_feature input
        // or wrap again (see MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY) for a specific distribution.
        // NOTE: no BiomeFilter here - this is only ever referenced as a nested ingredient (inside
        // SELECT_MUSHROOM_KEY's RandomFeatureConfiguration), never added directly to a biome. A
        // BiomeFilter on a nested/ingredient feature causes "Tried to biome check an unregistered
        // feature" at runtime, since FeatureSorter only indexes top-level per-biome features.
        register(context, SELECT_HUGE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(SELECT_HUGE_MUSHROOM_KEY));

        // mushroom_island_surface_huge_mushroom_feature.json: iterations 1, x/z uniform [0,16],
        // y = query.above_top_solid(...) -> once per chunk, spread across it, one above the surface.
        register(context, MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(SELECT_HUGE_MUSHROOM_KEY),
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BiomeFilter.biome());

        // shattered_swamp/swamp_huge_mushroom_feature.json: iterations 1, scatter_chance 1/4,
        // y = query.above_top_solid(...) -> once per chunk (25% of the time), on the surface.
        register(context, SWAMP_HUGE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(SELECT_HUGE_MUSHROOM_KEY),
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                net.minecraft.world.level.levelgen.placement.RarityFilter.onAverageOnceEvery(4),
                BiomeFilter.biome());

        // underground_mushroom/huge_glow_mushroom_feature.json: iterations 25, y in [-64, heightmap-10].
        // NOTE: was VerticalAnchor.belowTop(10), which is 10 blocks below the world's build-height
        // ceiling (~y310) - not "10 below the terrain surface" as the comment/Bedrock intended. Java's
        // VerticalAnchor has no heightmap-relative variant, so this was scattering huge mushroom
        // structures uniformly across nearly the entire world height, mostly in open sky above the
        // actual terrain. Bounded to a fixed absolute height instead, matching the same underground-ish
        // range used by MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY below.
        //
        // EnvironmentScanPlacement then snaps the picked Y down onto the nearest solid floor (scanning
        // through air/cave_air, same mechanism vanilla uses for glow lichen/amethyst clusters), so the
        // structure lands grounded inside a cave/cavity instead of floating wherever the raw random Y
        // happened to fall. If no floor is found within range for that attempt, placement is skipped.
        register(context, HUGE_GLOW_MUSHROOM_UNDERGROUND_PLACED_KEY, configuredFeatures.getOrThrow(HUGE_GLOW_MUSHROOM_KEY),
                CountPlacement.of(25),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(60)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR),
                        12),
                BiomeFilter.biome());

        // select_mushroom (underground_mushroom/select_mushroom_feature.json), generic - fed as the
        // vegetation_feature of the mycelium floor patch below.
        // NOTE: no BiomeFilter here either, for the same reason as SELECT_HUGE_MUSHROOM_PLACED_KEY above -
        // this is only ever referenced nested (as MYCELIUM_FLOOR_KEY's vegetationFeature), never added
        // directly to a biome.
        register(context, SELECT_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(SELECT_MUSHROOM_KEY));

        // mushroom_surface_mycelium_floor_feature.json: iterations 400, y uniform [-64,60]. Bedrock's
        // separate "snap_to_surface_feature" wrapper (vertical_search_range 12, surface floor) is folded
        // into VegetationPatchConfiguration.surface(CaveSurface.FLOOR) above - the patch feature already
        // searches for a valid floor itself, so no extra Java feature is needed for it.
        // NOTE: CountPlacement's IntProvider codec caps at 256 - Bedrock's 400 iterations/chunk has no
        // exact Java equivalent, so this is clamped to the engine max.
        register(context, MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY, configuredFeatures.getOrThrow(MYCELIUM_FLOOR_KEY),
                CountPlacement.of(256),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(60)),
                BiomeFilter.biome());
    }

    private static void registerStructure(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, String structureName, Optional<Rotation> fixedRotation) {
        SingleStructureConfiguration config = new SingleStructureConfiguration(
                new ResourceLocation(ExtraBiomes.MOD_ID, "mushroom/" + structureName),
                fixedRotation,
                0
        );
        context.register(key, new ConfiguredFeature<>(ModStructureScatterFeatures.SINGLE_STRUCTURE.get(), config));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> cfKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> pfKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, net.minecraft.core.Holder<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
        context.register(key, new PlacedFeature(configuration, List.of(modifiers)));
    }
}
