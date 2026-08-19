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
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
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
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * Port of the Bedrock "boulder" feature subsystem:
 * <pre>
 * features/boulder/select_boulder.json         (minecraft:weighted_random_feature, 10 entries)
 * features/boulder/{stone,andesite,diorite,granite,calcite,tuff,cobblestone,
 *                    mossy_cobblestone,blackstone}_boulder.json + pebble_patch.json
 *                                               (minecraft:vegetation_patch_feature)
 * features/boulder/boulder_snap_to_floor_feature.json (minecraft:snap_to_surface_feature wrapper)
 * features/boulder/pebble.json                 (minecraft:weighted_random_feature, 6 entries)
 * features/boulder/{regular,small,large}_pebble.json + {regular,small,large}_mossy_pebble.json
 *                                               (minecraft:structure_template_feature)
 * features/stick_pile/select_stick_pile.json    (minecraft:weighted_random_feature, 2 entries)
 * features/stick_pile/stick_pile{0,1}.json      (minecraft:structure_template_feature)
 * feature_rules/boulder/boulder_placer.json, feature_rules/boulder/stick_pile_placer.json
 * </pre>
 * <p>
 * Mapping notes:
 * <ul>
 *   <li>Each "weighted_random_feature" list is converted to {@link Feature#RANDOM_SELECTOR} +
 *       {@link RandomFeatureConfiguration}. Bedrock evaluates a true weighted pick over the whole
 *       list; vanilla's RANDOM_SELECTOR instead tries entries in order, each with its own
 *       independent success chance, falling through to a mandatory "default" entry. To reproduce
 *       the same distribution, each entry's chance is set to {@code weight / (sum of its own and
 *       all remaining weights)} - this yields exactly the same per-item probabilities as a proper
 *       weighted pick, and (conveniently) the last/lowest-priority original entry always ends up
 *       with chance 1.0, i.e. it can be used directly as vanilla's mandatory "default" entry
 *       instead of a WeightedPlacedFeature. This trick is applied to all three weighted lists
 *       below (select_boulder, pebble, select_stick_pile).</li>
 *   <li>"minecraft:vegetation_patch_feature" -> {@link Feature#VEGETATION_PATCH} +
 *       {@link VegetationPatchConfiguration}. {@code replaceable_blocks} becomes the
 *       {@code extrabiomes:boulder_replaceable} block tag (dirt/podzol/rooted_dirt/stone/
 *       grass_block/snow_block, mirroring Bedrock's dirt/podzol/dirt_with_roots/stone/grass/snow).
 *       {@code ground_block} -> {@code groundState}, {@code vegetation_feature} -> the shared
 *       pebble-select {@link PlacedFeature} (see below), {@code surface: "floor"} ->
 *       {@link CaveSurface#FLOOR}, {@code depth}/{@code horizontal_radius} ranges -> IntProviders,
 *       {@code vertical_range}/{@code vegetation_chance} passed straight through.
 *       {@code extra_bottom_block_chance} has no Bedrock equivalent in these files, so it is 0.</li>
 *   <li>"minecraft:snap_to_surface_feature" (boulder_snap_to_floor_feature.json) is folded into
 *       the top-level select_boulder {@link PlacedFeature}'s placement modifiers
 *       ({@link HeightmapPlacement}) rather than becoming a separate Java feature, per project
 *       convention - VegetationPatchFeature itself already re-searches for the floor within
 *       {@code vertical_range}, so the heightmap placement modifier only needs to get the
 *       placement roughly onto the surface first.</li>
 *   <li>"minecraft:structure_template_feature" (the six pebble variants + two stick-pile variants)
 *       -> the shared {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature}
 *       infrastructure, one {@link SingleStructureConfiguration} per converted .nbt. None of the
 *       pebble variants specify a {@code facing_direction}, so they use a per-placement random
 *       rotation (matches Bedrock's default/unspecified facing); the stick piles both fix
 *       {@code facing_direction: "north"}, so they use a fixed {@link Rotation#NONE}.</li>
 *   <li>{@code constraints.grounded}/{@code unburied}/{@code block_intersection} (all six pebble
 *       variants and both stick piles) and {@code adjustment_radius: 4} (stick piles) are Bedrock
 *       placement-time constraints with no direct Java equivalent; they are approximated by
 *       placing on {@link Heightmap.Types#WORLD_SURFACE_WG} with no additional search/adjustment.
 *       This is a simplification - stick piles in particular may occasionally end up floating or
 *       embedded on uneven terrain where Bedrock's adjustment_radius would have nudged them to fit.</li>
 *   <li>{@code scatter_chance: 10} in both {@code boulder_placer.json} and
 *       {@code stick_pile_placer.json} is approximated with {@link RarityFilter#onAverageOnceEvery(int)}
 *       (an average 1-in-10 chance per chunk column attempt), combined with
 *       {@link InSquarePlacement#spread()} for the x/z 0-16 uniform spread (iterations: 1).</li>
 *   <li>The Bedrock stick-pile structures place a custom {@code extrabiomes:stick_pile} block
 *       (with a {@code extrabiomes:facing}/block_face state). Bedrock's permutations only ever
 *       apply an identical rotation within each opposite-face pair (north/south, east/west,
 *       up/down), i.e. the visual only depends on {@link net.minecraft.core.Direction#getAxis()}
 *       - exactly like a log - so it is ported as {@code net.winepicfin.extrabiomes.forge.block.custom.StickPileBlock},
 *       a {@link net.minecraft.world.level.block.RotatedPillarBlock} with three baked per-axis
 *       models (see tools/convert_stick_pile_model.py, which converts the Bedrock geometry
 *       directly since its bones carry no rotations of their own). tools/block_map.py maps
 *       {@code extrabiomes:stick_pile} to it directly (collapsing the 6-way block_face onto the
 *       3-way axis via {@code BLOCK_FACE_AXIS}) instead of dropping to {@code minecraft:air}.</li>
 * </ul>
 */
public class BoulderFeatures {

    // -----------------------------------------------------------------
    // shared tag
    // -----------------------------------------------------------------
    public static final TagKey<Block> BOULDER_REPLACEABLE =
            TagKey.create(Registries.BLOCK, new ResourceLocation(ExtraBiomes.MOD_ID, "boulder_replaceable"));

    // -----------------------------------------------------------------
    // pebble sub-features (boulder/pebble.json + its 6 structure_template_feature variants)
    // -----------------------------------------------------------------
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

    /** extrabiomes:boulder/pebble - weighted pick between the six pebble structure variants. */
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_SELECT_KEY = configuredKey("pebble_select");
    public static final ResourceKey<PlacedFeature> PEBBLE_SELECT_PLACED_KEY = placedKey("pebble_select");

    // -----------------------------------------------------------------
    // boulder ground patches (one vegetation_patch_feature per select_boulder.json entry)
    // -----------------------------------------------------------------
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

    private static final int STICK_PILE_GROUND_OFFSET = -1;

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
        // STICK_PILE_GROUND_OFFSET sinks each pile in slightly so it reads as resting among/into
        // the ground rather than floating on top of it. ---
        registerSingleStructure(context, STICK_PILE_0_KEY, "boulder/big_stick_pile0", Optional.of(Rotation.NONE), STICK_PILE_GROUND_OFFSET);
        registerSingleStructure(context, STICK_PILE_1_KEY, "boulder/big_stick_pile1", Optional.of(Rotation.NONE), STICK_PILE_GROUND_OFFSET);

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
        // sitting directly on the heightmap surface - see class docs). OCEAN_FLOOR_WG (instead of
        // WORLD_SURFACE_WG) keeps this off the water's own surface, landing on the actual
        // lake/pond bed instead - STICK_PILE_GROUND_OFFSET then sinks each pile in slightly.
        context.register(SELECT_STICK_PILE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_STICK_PILE_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
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
