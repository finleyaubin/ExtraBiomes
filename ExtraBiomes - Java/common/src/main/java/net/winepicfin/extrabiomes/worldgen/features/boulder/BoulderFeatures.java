package net.winepicfin.extrabiomes.worldgen.features.boulder;

import net.minecraft.core.Holder;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;
import java.util.Optional;

public class BoulderFeatures {

    public static final TagKey<Block> BOULDER_REPLACEABLE =
            TagKey.create(Registries.BLOCK, new ResourceLocation(ExtraBiomes.MOD_ID, "boulder_replaceable"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_REGULAR_KEY = configuredKey("pebble_regular");
    public static final ResourceKey<PlacedFeature> PEBBLE_REGULAR_PLACED_KEY = placedKey("pebble_regular");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_SMALL_KEY = configuredKey("pebble_small");
    public static final ResourceKey<PlacedFeature> PEBBLE_SMALL_PLACED_KEY = placedKey("pebble_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_LARGE_KEY = configuredKey("pebble_large");
    public static final ResourceKey<PlacedFeature> PEBBLE_LARGE_PLACED_KEY = placedKey("pebble_large");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_REGULAR_MOSSY_KEY = configuredKey("pebble_regular_mossy");
    public static final ResourceKey<PlacedFeature> PEBBLE_REGULAR_MOSSY_PLACED_KEY = placedKey("pebble_regular_mossy");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_SMALL_MOSSY_KEY = configuredKey("pebble_small_mossy");
    public static final ResourceKey<PlacedFeature> PEBBLE_SMALL_MOSSY_PLACED_KEY = placedKey("pebble_small_mossy");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_LARGE_MOSSY_KEY = configuredKey("pebble_large_mossy");
    public static final ResourceKey<PlacedFeature> PEBBLE_LARGE_MOSSY_PLACED_KEY = placedKey("pebble_large_mossy");

    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_SELECT_KEY = configuredKey("pebble_select");
    public static final ResourceKey<PlacedFeature> PEBBLE_SELECT_PLACED_KEY = placedKey("pebble_select");

    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_STONE_KEY = configuredKey("ground_stone");
    public static final ResourceKey<PlacedFeature> GROUND_STONE_PLACED_KEY = placedKey("ground_stone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_ANDESITE_KEY = configuredKey("ground_andesite");
    public static final ResourceKey<PlacedFeature> GROUND_ANDESITE_PLACED_KEY = placedKey("ground_andesite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_DIORITE_KEY = configuredKey("ground_diorite");
    public static final ResourceKey<PlacedFeature> GROUND_DIORITE_PLACED_KEY = placedKey("ground_diorite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_GRANITE_KEY = configuredKey("ground_granite");
    public static final ResourceKey<PlacedFeature> GROUND_GRANITE_PLACED_KEY = placedKey("ground_granite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_CALCITE_KEY = configuredKey("ground_calcite");
    public static final ResourceKey<PlacedFeature> GROUND_CALCITE_PLACED_KEY = placedKey("ground_calcite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_TUFF_KEY = configuredKey("ground_tuff");
    public static final ResourceKey<PlacedFeature> GROUND_TUFF_PLACED_KEY = placedKey("ground_tuff");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_COBBLESTONE_KEY = configuredKey("ground_cobblestone");
    public static final ResourceKey<PlacedFeature> GROUND_COBBLESTONE_PLACED_KEY = placedKey("ground_cobblestone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_MOSSY_COBBLESTONE_KEY = configuredKey("ground_mossy_cobblestone");
    public static final ResourceKey<PlacedFeature> GROUND_MOSSY_COBBLESTONE_PLACED_KEY = placedKey("ground_mossy_cobblestone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_BLACKSTONE_KEY = configuredKey("ground_blackstone");
    public static final ResourceKey<PlacedFeature> GROUND_BLACKSTONE_PLACED_KEY = placedKey("ground_blackstone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROUND_PEBBLE_PATCH_KEY = configuredKey("ground_pebble_patch");
    public static final ResourceKey<PlacedFeature> GROUND_PEBBLE_PATCH_PLACED_KEY = placedKey("ground_pebble_patch");

    /**
     * extrabiomes:boulder/select_boulder - the single feature the boulder_placer feature_rule
     * places. This is the ResourceKey biome classes should register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, SELECT_BOULDER_PLACED_KEY)}.
     */
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_BOULDER_KEY = configuredKey("select_boulder");
    public static final ResourceKey<PlacedFeature> SELECT_BOULDER_PLACED_KEY = placedKey("select_boulder");

    // -----------------------------------------------------------------
    // stick pile (stick_pile/select_stick_pile.json + stick_pile0/1.json)
    // -----------------------------------------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> STICK_PILE_0_KEY = configuredKey("stick_pile_0");
    public static final ResourceKey<PlacedFeature> STICK_PILE_0_PLACED_KEY = placedKey("stick_pile_0");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STICK_PILE_1_KEY = configuredKey("stick_pile_1");
    public static final ResourceKey<PlacedFeature> STICK_PILE_1_PLACED_KEY = placedKey("stick_pile_1");

    /**
     * extrabiomes:stick_pile/select_stick_pile - the single feature the stick_pile_placer
     * feature_rule places. This is the ResourceKey biome classes should register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SELECT_STICK_PILE_PLACED_KEY)}.
     */
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_STICK_PILE_KEY = configuredKey("select_stick_pile");
    public static final ResourceKey<PlacedFeature> SELECT_STICK_PILE_PLACED_KEY = placedKey("select_stick_pile");

    // Was -1 (sinks the pile in slightly, same technique as GlacierFeatures' snow drifts), but
    // playtest feedback was that it read as sunk too far into the ground - 0 sits it flush with
    // the heightmap surface instead.
    private static final int STICK_PILE_GROUND_OFFSET = 0;
    // Matches MushroomFeatures' own huge-mushroom clear-space threshold - see its registerStructure
    // javadoc. Not 1.0F: the pile's own bottom/floor row against the ground is expected to be
    // non-air, so requiring every block clear would reject nearly all placements.
    private static final float STICK_PILE_MIN_CLEAR_FRACTION = 0.9F;

    // ===================================================================
    // configured features
    // ===================================================================
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        // --- pebble structure variants (facing_direction unspecified -> random rotation) ---
        registerSingleStructure(context, PEBBLE_REGULAR_KEY, "boulder/pebble", Optional.empty());
        registerSingleStructure(context, PEBBLE_SMALL_KEY, "boulder/small_pebble", Optional.empty());
        registerSingleStructure(context, PEBBLE_LARGE_KEY, "boulder/large_pebble", Optional.empty());
        registerSingleStructure(context, PEBBLE_REGULAR_MOSSY_KEY, "boulder/mossy_pebble", Optional.empty());
        registerSingleStructure(context, PEBBLE_SMALL_MOSSY_KEY, "boulder/small_mossy_pebble", Optional.empty());
        registerSingleStructure(context, PEBBLE_LARGE_MOSSY_KEY, "boulder/large_mossy_pebble", Optional.empty());

        // pebble.json weights: regular 1, small 2, large 1, regular_mossy 1, small_mossy 2, large_mossy 1 (total 8).
        // Converted to sequential-trial chances (weight / remaining-total-from-here); large_mossy is
        // the guaranteed (chance 1.0) remainder, so it becomes the RANDOM_SELECTOR "default".
        context.register(PEBBLE_SELECT_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(PEBBLE_REGULAR_PLACED_KEY), 1.0F / 8.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(PEBBLE_SMALL_PLACED_KEY), 2.0F / 7.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(PEBBLE_LARGE_PLACED_KEY), 1.0F / 5.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(PEBBLE_REGULAR_MOSSY_PLACED_KEY), 1.0F / 4.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(PEBBLE_SMALL_MOSSY_PLACED_KEY), 2.0F / 3.0F)
                ),
                placedFeatures.getOrThrow(PEBBLE_LARGE_MOSSY_PLACED_KEY)
        )));

        // --- boulder ground vegetation patches ---
        Holder<PlacedFeature> pebbleSelect = placedFeatures.getOrThrow(PEBBLE_SELECT_PLACED_KEY);
        registerGroundPatch(context, GROUND_STONE_KEY, Blocks.STONE.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_ANDESITE_KEY, Blocks.ANDESITE.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_DIORITE_KEY, Blocks.DIORITE.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_GRANITE_KEY, Blocks.GRANITE.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_CALCITE_KEY, Blocks.CALCITE.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_TUFF_KEY, Blocks.TUFF.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_COBBLESTONE_KEY, Blocks.COBBLESTONE.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_MOSSY_COBBLESTONE_KEY, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), pebbleSelect, false);
        registerGroundPatch(context, GROUND_BLACKSTONE_KEY, Blocks.BLACKSTONE.defaultBlockState(), pebbleSelect, false);
        // pebble_patch.json: depth is a fixed 1 (range_min == range_max) and a much wider horizontal_radius (4-10).
        registerGroundPatch(context, GROUND_PEBBLE_PATCH_KEY, Blocks.GRASS_BLOCK.defaultBlockState(), pebbleSelect, true);

        // select_boulder.json weights: stone 10, andesite 5, diorite 5, granite 5, calcite 4, tuff 2,
        // cobblestone 2, mossy_cobblestone 1, blackstone 1, pebble_patch 50 (total 85). Same
        // sequential-trial conversion as pebble.json above; pebble_patch (the last/heaviest entry)
        // ends up as the guaranteed "default".
        context.register(SELECT_BOULDER_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_STONE_PLACED_KEY), 10.0F / 85.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_ANDESITE_PLACED_KEY), 5.0F / 75.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_DIORITE_PLACED_KEY), 5.0F / 70.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_GRANITE_PLACED_KEY), 5.0F / 65.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_CALCITE_PLACED_KEY), 4.0F / 60.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_TUFF_PLACED_KEY), 2.0F / 56.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_COBBLESTONE_PLACED_KEY), 2.0F / 54.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_MOSSY_COBBLESTONE_PLACED_KEY), 1.0F / 52.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(GROUND_BLACKSTONE_PLACED_KEY), 1.0F / 51.0F)
                ),
                placedFeatures.getOrThrow(GROUND_PEBBLE_PATCH_PLACED_KEY)
        )));

        // --- stick pile structure variants (facing_direction: "north" -> fixed Rotation.NONE).
        // minClearFraction requires most of the structure's footprint to already be air before
        // placement is allowed (same mechanism MushroomFeatures uses), so a pile no longer
        // unconditionally stamps itself through trees/other terrain it happens to land inside of.
        // requireGroundedFloor requires solid ground under the WHOLE footprint (not just the
        // single heightmap-sampled origin column), so it no longer partially floats over a ledge/
        // slope/gap either - see SingleStructureFeature's own javadoc for both. ---
        registerSingleStructure(context, STICK_PILE_0_KEY, "boulder/big_stick_pile0", Optional.of(Rotation.NONE), STICK_PILE_GROUND_OFFSET, STICK_PILE_MIN_CLEAR_FRACTION, true);
        registerSingleStructure(context, STICK_PILE_1_KEY, "boulder/big_stick_pile1", Optional.of(Rotation.NONE), STICK_PILE_GROUND_OFFSET, STICK_PILE_MIN_CLEAR_FRACTION, true);

        // select_stick_pile.json weights: stick_pile0 1, stick_pile1 1 (total 2) -> chance 0.5, default stick_pile1.
        context.register(SELECT_STICK_PILE_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(placedFeatures.getOrThrow(STICK_PILE_0_PLACED_KEY), 0.5F)),
                placedFeatures.getOrThrow(STICK_PILE_1_PLACED_KEY)
        )));
    }

    private static void registerSingleStructure(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, String structurePath, Optional<Rotation> rotation) {
        registerSingleStructure(context, key, structurePath, rotation, 0);
    }

    private static void registerSingleStructure(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, String structurePath, Optional<Rotation> rotation, int groundOffset) {
        ResourceLocation structure = new ResourceLocation(ExtraBiomes.MOD_ID, structurePath);
        SingleStructureConfiguration config = new SingleStructureConfiguration(structure, rotation, groundOffset);
        context.register(key, new ConfiguredFeature<>(ModStructureScatterFeatures.SINGLE_STRUCTURE.get(), config));
    }

    private static void registerSingleStructure(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, String structurePath, Optional<Rotation> rotation, int groundOffset, float minClearFraction, boolean requireGroundedFloor) {
        ResourceLocation structure = new ResourceLocation(ExtraBiomes.MOD_ID, structurePath);
        SingleStructureConfiguration config = new SingleStructureConfiguration(structure, rotation, groundOffset, minClearFraction, requireGroundedFloor);
        context.register(key, new ConfiguredFeature<>(ModStructureScatterFeatures.SINGLE_STRUCTURE.get(), config));
    }

    private static void registerGroundPatch(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, net.minecraft.world.level.block.state.BlockState groundState, Holder<PlacedFeature> pebbleSelect, boolean isPebblePatch) {
        VegetationPatchConfiguration config = new VegetationPatchConfiguration(
                BOULDER_REPLACEABLE,
                BlockStateProvider.simple(groundState),
                pebbleSelect,
                CaveSurface.FLOOR,
                isPebblePatch ? ConstantInt.of(1) : UniformInt.of(1, 6),
                0.0F,
                5,
                0.1F,
                isPebblePatch ? UniformInt.of(4, 10) : UniformInt.of(1, 4),
                0.3F
        );
        context.register(key, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, config));
    }

    // ===================================================================
    // placed features
    // ===================================================================
    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // pebble sub-features + selector: no placement modifiers of their own - they are invoked
        // directly (at an already-chosen position) either as a vegetation_feature or as a
        // WeightedPlacedFeature entry of another RANDOM_SELECTOR.
        registerNoModifiers(context, configuredFeatures, PEBBLE_REGULAR_PLACED_KEY, PEBBLE_REGULAR_KEY);
        registerNoModifiers(context, configuredFeatures, PEBBLE_SMALL_PLACED_KEY, PEBBLE_SMALL_KEY);
        registerNoModifiers(context, configuredFeatures, PEBBLE_LARGE_PLACED_KEY, PEBBLE_LARGE_KEY);
        registerNoModifiers(context, configuredFeatures, PEBBLE_REGULAR_MOSSY_PLACED_KEY, PEBBLE_REGULAR_MOSSY_KEY);
        registerNoModifiers(context, configuredFeatures, PEBBLE_SMALL_MOSSY_PLACED_KEY, PEBBLE_SMALL_MOSSY_KEY);
        registerNoModifiers(context, configuredFeatures, PEBBLE_LARGE_MOSSY_PLACED_KEY, PEBBLE_LARGE_MOSSY_KEY);
        registerNoModifiers(context, configuredFeatures, PEBBLE_SELECT_PLACED_KEY, PEBBLE_SELECT_KEY);

        // boulder ground vegetation patches: likewise no modifiers of their own - only the
        // top-level select_boulder placement below carries the scatter/heightmap/biome modifiers.
        registerNoModifiers(context, configuredFeatures, GROUND_STONE_PLACED_KEY, GROUND_STONE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_ANDESITE_PLACED_KEY, GROUND_ANDESITE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_DIORITE_PLACED_KEY, GROUND_DIORITE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_GRANITE_PLACED_KEY, GROUND_GRANITE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_CALCITE_PLACED_KEY, GROUND_CALCITE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_TUFF_PLACED_KEY, GROUND_TUFF_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_COBBLESTONE_PLACED_KEY, GROUND_COBBLESTONE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_MOSSY_COBBLESTONE_PLACED_KEY, GROUND_MOSSY_COBBLESTONE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_BLACKSTONE_PLACED_KEY, GROUND_BLACKSTONE_KEY);
        registerNoModifiers(context, configuredFeatures, GROUND_PEBBLE_PATCH_PLACED_KEY, GROUND_PEBBLE_PATCH_KEY);

        // select_boulder: iterations 1, scatter_chance 10, x/z uniform [0,16], y = heightmap +/- 1,
        // then boulder_snap_to_floor_feature re-searches for the actual floor (folded into
        // VegetationPatchConfiguration's own vertical_range/surface search at generation time).
        context.register(SELECT_BOULDER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_BOULDER_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        // stick pile sub-features: no modifiers of their own, same reasoning as pebbles. The
        // select_stick_pile selector itself gets its modifiers below (it is the top-level feature
        // placed directly by stick_pile_placer.json), so it is NOT also registered here.
        registerNoModifiers(context, configuredFeatures, STICK_PILE_0_PLACED_KEY, STICK_PILE_0_KEY);
        registerNoModifiers(context, configuredFeatures, STICK_PILE_1_PLACED_KEY, STICK_PILE_1_KEY);

        // select_stick_pile (via stick_pile_placer.json): iterations 1, scatter_chance 10, x/z
        // uniform [0,16], y = heightmap +/- 1 (grounded/unburied constraints approximated by
        // sitting directly on the heightmap surface - see class docs). Anchored on OCEAN_FLOOR_WG
        // (not WORLD_SURFACE_WG, which counts water as non-air and would place the pile floating on
        // a lake/river's surface) plus SurfaceWaterDepthFilter.forMaxDepth(0) so piles never
        // generate on or in water at all - same fix as NetherlandsWindmillFeature's own water check.
        context.register(SELECT_STICK_PILE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_STICK_PILE_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()
                )
        ));
    }

    private static void registerNoModifiers(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, ResourceKey<PlacedFeature> placedKey, ResourceKey<ConfiguredFeature<?, ?>> configuredKey) {
        context.register(placedKey, new PlacedFeature(configuredFeatures.getOrThrow(configuredKey), List.<PlacementModifier>of()));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "boulder/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "boulder/" + name));
    }
}
