package net.winepicfin.extrabiomes.worldgen.features.undergroundjungle;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.moss.MossFeatures;

import java.util.List;

/**
 * Java port of the Bedrock "underground_jungle" feature subsystem:
 * <pre>
 * features/underground_jungle/select_moss_or_jungle_tree_feature.json       (minecraft:weighted_random_feature)
 * features/underground_jungle/select_moss_or_jungle_tree_upper_feature.json (minecraft:aggregate_feature)
 * features/underground_jungle/cave_vine_feature.json                       (minecraft:growing_plant_feature)
 * features/underground_jungle/cave_vine_snap_to_ceiling_feature.json       (minecraft:snap_to_surface_feature)
 * features/underground_jungle/grass_floor_feature.json + _upper            (minecraft:vegetation_patch_feature)
 * features/underground_jungle/grass_floor_snap_to_floor_feature.json + _upper (minecraft:snap_to_surface_feature)
 * feature_rules/underground_jungle/jungle_after_surface_cave_vines_feature.json
 * feature_rules/underground_jungle/jungle_surface_grass_floor_feature.json + _upper
 * </pre>
 * Applies to FloatingJungle, FungleJungle, JungleMarsh, JunglePillars, LushMesa, LushMesaBryce and
 * DeepDarkGreen (wiring left to the biome-wiring pass; see class-level {@code addFeature} calls
 * documented below).
 * <p>
 * Not ported from this directory's feature_rules (all reference vanilla-only, non-{@code extrabiomes}
 * features and are out of scope): {@code jungle_after_surface_vines_feature.json} (places vanilla's
 * own {@code minecraft:fixup_vines_position_feature}) and {@code jungle_podzol_floor_feature.json}
 * (places vanilla's own {@code minecraft:optional_podzol_feature} for {@code bamboo}-tagged biomes) -
 * both are Bedrock's stock vanilla-parity feature_rules, not specific to this mod's content.
 * <p>
 * Mapping notes / simplifications:
 * <ul>
 *   <li>{@code cave_vine_feature.json} + its {@code snap_to_ceiling} wrapper -&gt;
 *       {@link CaveVineFeature}, a single custom feature that folds the ceiling search directly in
 *       (see its own javadoc for the height/age/block-weight mapping).</li>
 *   <li>{@code minecraft:optional_fallen_jungle_tree_feature} and
 *       {@code minecraft:jungle_tree_with_cocoa_feature} are Bedrock built-ins with no shipped JSON
 *       body. {@code jungle_tree_with_cocoa_feature} is mapped onto vanilla's own
 *       {@link TreeFeatures#JUNGLE_TREE} (a regular jungle tree that already carries a
 *       {@code CocoaDecorator(0.2F)} - an exact behavioral match). {@code optional_fallen_jungle_tree}
 *       has no vanilla equivalent at all and is approximated by {@link FallenJungleTreeFeature} (see
 *       its javadoc). {@code mega_jungle_tree_feature} reuses vanilla's
 *       {@link TreeFeatures#MEGA_JUNGLE_TREE} directly (already vine-decorated, matching Bedrock's
 *       plain giant jungle tree). {@code bamboo_feature} reuses vanilla's
 *       {@link VegetationFeatures#BAMBOO_NO_PODZOL} (a single bamboo plant with no podzol
 *       side-effect, the closest match to Bedrock's bare bamboo placement).</li>
 *   <li>{@code extrabiomes:custom_moss_patch_feature} (from the "moss" subsystem, referenced here as
 *       {@code select_moss_or_jungle_tree}'s moss entry) is rebuilt fresh in this class rather than
 *       reused from {@link MossFeatures}: that class deliberately left the outer
 *       {@code custom_moss_patch_feature.json} vegetation-patch wrapper out of scope and instead
 *       registered its three {@code custom_moss_select_feature.json} aggregate members
 *       ({@link MossFeatures#TALL_GRASS_PATCH_KEY}, {@link MossFeatures#MOSS_CARPET_KEY}, vanilla
 *       {@link TreeFeatures#JUNGLE_BUSH}) as independent top-level {@code PlacedFeature}s carrying
 *       their own chunk-wide scatter/heightmap-surface placement modifiers - exactly wrong for
 *       nesting inside another patch's single-slot {@code vegetation_feature} (which needs a
 *       no-modifier placement invoked once at an already-chosen underground column; a heightmap
 *       modifier here would send it to the overworld surface). This class instead wraps the same
 *       three underlying <em>configured</em> features (reusing {@link MossFeatures}'s configured
 *       features directly, so no logic is duplicated) with fresh no-modifier placements and combines
 *       them with the new {@link MultiFeature} (a minimal aggregate-feature runner - see its javadoc)
 *       to reproduce {@code custom_moss_select_feature.json}'s "run all three unconditionally"
 *       semantics, then wraps that in a fresh {@link net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration}
 *       matching {@code custom_moss_patch_feature.json} exactly.</li>
 *   <li>{@code select_moss_or_jungle_tree_feature.json}'s {@code minecraft:weighted_random_feature}
 *       (mega_jungle_tree 1, optional_fallen_jungle_tree 5, jungle_tree_with_cocoa 10,
 *       custom_moss_patch 9, bamboo 1; total 26) is converted to {@link Feature#RANDOM_SELECTOR} the
 *       same way as other ported subsystems: each entry's chance is {@code weight / (remaining total
 *       from this entry onward)}, which reproduces the same distribution as a true weighted pick and
 *       leaves the last entry (bamboo) with chance 1.0, usable directly as the mandatory default.</li>
 *   <li>{@code select_moss_or_jungle_tree_upper_feature.json}'s {@code minecraft:aggregate_feature}
 *       (fallen tree + cocoa tree + moss patch, all unconditional, no bamboo/mega tree) is likewise
 *       built with {@link MultiFeature}.</li>
 *   <li>Both {@code grass_floor_feature.json} variants' {@code minecraft:snap_to_surface_feature}
 *       wrapper is folded into {@link VegetationPatchConfiguration}'s own {@code surface}/
 *       {@code vertical_range} floor search, per project convention (see e.g. {@code MushroomFeatures}
 *       / {@code BoulderFeatures}). Both {@code replaceable_blocks} lists are ported as block tags;
 *       Bedrock's separate {@code cave_vines}/{@code cave_vines_body_with_berries}/
 *       {@code cave_vines_head_with_berries} block ids all collapse onto Java's two
 *       {@link Blocks#CAVE_VINES} (head)/{@link Blocks#CAVE_VINES_PLANT} (body) blocks (the "with
 *       berries" distinction is just a boolean block-state in Java), so {@code grass_floor_upper}'s
 *       replaceable list (which lists only the berry variants, not the plain head/body ids) is
 *       approximated by tagging the whole head/body block pair - a minor over-approximation noted
 *       here rather than silently.</li>
 * </ul>
 * <p>
 * Biome wiring (left to the separate wiring pass, not touched by this class):
 * <pre>
 * biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_PLACED_KEY);
 * biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_UPPER_PLACED_KEY);
 * biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, UndergroundJungleFeatures.CAVE_VINE_PLACED_KEY);
 * </pre>
 */
public class UndergroundJungleFeatures {

    // Custom Feature<?> implementations must be registered in Registries.FEATURE (mirrors
    // net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures) so their codec has a
    // stable registry name for ConfiguredFeature (de)serialization/datagen.
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ExtraBiomes.MOD_ID);

    public static final RegistryObject<CaveVineFeature> CAVE_VINE_FEATURE =
            FEATURES.register("underground_jungle_cave_vine", () -> new CaveVineFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<FallenJungleTreeFeature> FALLEN_JUNGLE_TREE_FEATURE =
            FEATURES.register("underground_jungle_fallen_jungle_tree", () -> new FallenJungleTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<MultiFeature> MULTI_FEATURE =
            FEATURES.register("underground_jungle_multi", () -> new MultiFeature(MultiFeatureConfiguration.CODEC));

    /** Must be called once from the mod's main class, e.g. {@code UndergroundJungleFeatures.register(modEventBus);}. */
    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }

    // -----------------------------------------------------------------
    // replaceable-block tags
    // -----------------------------------------------------------------
    public static final TagKey<Block> GRASS_FLOOR_REPLACEABLE =
            tagKey("underground_jungle_grass_floor_replaceable");
    public static final TagKey<Block> GRASS_FLOOR_UPPER_REPLACEABLE =
            tagKey("underground_jungle_grass_floor_upper_replaceable");
    public static final TagKey<Block> MOSS_PATCH_REPLACEABLE =
            tagKey("underground_jungle_moss_patch_replaceable");

    // -----------------------------------------------------------------
    // building-block features (fallen tree, cave vine)
    // -----------------------------------------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> FALLEN_JUNGLE_TREE_KEY = cfKey("fallen_jungle_tree");
    public static final ResourceKey<PlacedFeature> FALLEN_JUNGLE_TREE_PLACED_KEY = pfKey("fallen_jungle_tree");

    public static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_VINE_KEY = cfKey("cave_vine");
    /** Bedrock's {@code jungle_after_surface_cave_vines_feature.json} distribution - register in UNDERGROUND_DECORATION. */
    public static final ResourceKey<PlacedFeature> CAVE_VINE_PLACED_KEY = pfKey("cave_vine");

    // -----------------------------------------------------------------
    // custom_moss_select_feature.json equivalent (fresh no-modifier wrappers around MossFeatures'
    // configured features + vanilla's jungle bush, combined with MultiFeature)
    // -----------------------------------------------------------------
    public static final ResourceKey<PlacedFeature> MOSS_SELECT_TALL_GRASS_PLACED_KEY = pfKey("moss_select_tall_grass");
    public static final ResourceKey<PlacedFeature> MOSS_SELECT_CARPET_PLACED_KEY = pfKey("moss_select_carpet");
    public static final ResourceKey<PlacedFeature> MOSS_SELECT_JUNGLE_BUSH_PLACED_KEY = pfKey("moss_select_jungle_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_SELECT_KEY = cfKey("moss_select");
    public static final ResourceKey<PlacedFeature> MOSS_SELECT_PLACED_KEY = pfKey("moss_select");

    // -----------------------------------------------------------------
    // custom_moss_patch_feature.json equivalent
    // -----------------------------------------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> CUSTOM_MOSS_PATCH_KEY = cfKey("custom_moss_patch");
    public static final ResourceKey<PlacedFeature> CUSTOM_MOSS_PATCH_PLACED_KEY = pfKey("custom_moss_patch");

    // -----------------------------------------------------------------
    // vanilla tree building blocks, re-keyed locally so they can sit in our own RANDOM_SELECTOR/MultiFeature
    // -----------------------------------------------------------------
    public static final ResourceKey<PlacedFeature> MEGA_JUNGLE_TREE_PLACED_KEY = pfKey("mega_jungle_tree");
    public static final ResourceKey<PlacedFeature> JUNGLE_TREE_WITH_COCOA_PLACED_KEY = pfKey("jungle_tree_with_cocoa");
    public static final ResourceKey<PlacedFeature> BAMBOO_PLACED_KEY = pfKey("bamboo");

    // -----------------------------------------------------------------
    // select_moss_or_jungle_tree_feature.json / _upper
    // -----------------------------------------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_MOSS_OR_JUNGLE_TREE_KEY = cfKey("select_moss_or_jungle_tree");
    public static final ResourceKey<PlacedFeature> SELECT_MOSS_OR_JUNGLE_TREE_PLACED_KEY = pfKey("select_moss_or_jungle_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_MOSS_OR_JUNGLE_TREE_UPPER_KEY = cfKey("select_moss_or_jungle_tree_upper");
    public static final ResourceKey<PlacedFeature> SELECT_MOSS_OR_JUNGLE_TREE_UPPER_PLACED_KEY = pfKey("select_moss_or_jungle_tree_upper");

    // -----------------------------------------------------------------
    // grass_floor_feature.json / _upper (top-level features - wire these into biomes)
    // -----------------------------------------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRASS_FLOOR_KEY = cfKey("grass_floor");
    /** Bedrock's {@code jungle_surface_grass_floor_feature.json} distribution - register in VEGETAL_DECORATION. */
    public static final ResourceKey<PlacedFeature> GRASS_FLOOR_PLACED_KEY = pfKey("grass_floor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRASS_FLOOR_UPPER_KEY = cfKey("grass_floor_upper");
    /** Bedrock's {@code jungle_surface_grass_floor_upper_feature.json} distribution - register in VEGETAL_DECORATION. */
    public static final ResourceKey<PlacedFeature> GRASS_FLOOR_UPPER_PLACED_KEY = pfKey("grass_floor_upper");

    // ===================================================================
    // configured features
    // ===================================================================
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        // -- building blocks --------------------------------------------------------------------------
        context.register(FALLEN_JUNGLE_TREE_KEY, new ConfiguredFeature<>(FALLEN_JUNGLE_TREE_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(CAVE_VINE_KEY, new ConfiguredFeature<>(CAVE_VINE_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        // -- custom_moss_select_feature.json: tall grass scatter + moss carpet + jungle bush, all run
        //    unconditionally at the chosen column (see class docs for why this can't reuse MossFeatures'
        //    own top-level placed features directly). ---------------------------------------------------
        context.register(MOSS_SELECT_KEY, new ConfiguredFeature<>(MULTI_FEATURE.get(), new MultiFeatureConfiguration(List.of(
                placedFeatures.getOrThrow(MOSS_SELECT_TALL_GRASS_PLACED_KEY),
                placedFeatures.getOrThrow(MOSS_SELECT_CARPET_PLACED_KEY),
                placedFeatures.getOrThrow(MOSS_SELECT_JUNGLE_BUSH_PLACED_KEY)
        ))));

        // -- custom_moss_patch_feature.json: depth 1-2, vertical_range 1, vegetation_chance 0.1,
        //    horizontal_radius 1-3, extra_edge_column_chance 0.3. ------------------------------------
        context.register(CUSTOM_MOSS_PATCH_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                MOSS_PATCH_REPLACEABLE,
                BlockStateProvider.simple(Blocks.MOSS_BLOCK),
                placedFeatures.getOrThrow(MOSS_SELECT_PLACED_KEY),
                CaveSurface.FLOOR,
                UniformInt.of(1, 2),
                0.0F,
                1,
                0.1F,
                UniformInt.of(1, 3),
                0.3F
        )));

        // -- select_moss_or_jungle_tree_feature.json weights: mega_jungle_tree 1, optional_fallen_jungle_tree 5,
        //    jungle_tree_with_cocoa 10, custom_moss_patch 9, bamboo 1 (total 26). Sequential-trial
        //    conversion (see class docs); bamboo (last/lowest-priority entry) becomes the default. ------
        context.register(SELECT_MOSS_OR_JUNGLE_TREE_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(MEGA_JUNGLE_TREE_PLACED_KEY), 1.0F / 26.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(FALLEN_JUNGLE_TREE_PLACED_KEY), 5.0F / 25.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(JUNGLE_TREE_WITH_COCOA_PLACED_KEY), 10.0F / 20.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(CUSTOM_MOSS_PATCH_PLACED_KEY), 9.0F / 10.0F)
                ),
                placedFeatures.getOrThrow(BAMBOO_PLACED_KEY)
        )));

        // -- select_moss_or_jungle_tree_upper_feature.json: aggregate of fallen tree + cocoa tree + moss
        //    patch, all unconditional (no mega tree, no bamboo). -------------------------------------
        context.register(SELECT_MOSS_OR_JUNGLE_TREE_UPPER_KEY, new ConfiguredFeature<>(MULTI_FEATURE.get(), new MultiFeatureConfiguration(List.of(
                placedFeatures.getOrThrow(FALLEN_JUNGLE_TREE_PLACED_KEY),
                placedFeatures.getOrThrow(JUNGLE_TREE_WITH_COCOA_PLACED_KEY),
                placedFeatures.getOrThrow(CUSTOM_MOSS_PATCH_PLACED_KEY)
        ))));

        // -- grass_floor_feature.json: depth 1, vertical_range 5, vegetation_chance 0.4, horizontal_radius 8. --
        context.register(GRASS_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                GRASS_FLOOR_REPLACEABLE,
                BlockStateProvider.simple(Blocks.GRASS_BLOCK),
                placedFeatures.getOrThrow(SELECT_MOSS_OR_JUNGLE_TREE_PLACED_KEY),
                CaveSurface.FLOOR,
                ConstantInt.of(1),
                0.0F,
                5,
                0.4F,
                ConstantInt.of(8),
                0.3F
        )));

        // -- grass_floor_upper_feature.json: depth 1, vertical_range 5, vegetation_chance 0 (never
        //    actually grows anything - see class docs), horizontal_radius 8. -------------------------
        context.register(GRASS_FLOOR_UPPER_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                GRASS_FLOOR_UPPER_REPLACEABLE,
                BlockStateProvider.simple(Blocks.GRASS_BLOCK),
                placedFeatures.getOrThrow(SELECT_MOSS_OR_JUNGLE_TREE_UPPER_PLACED_KEY),
                CaveSurface.FLOOR,
                ConstantInt.of(1),
                0.0F,
                5,
                0.0F,
                ConstantInt.of(8),
                0.0F
        )));
    }

    // ===================================================================
    // placed features
    // ===================================================================
    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // -- building blocks: no modifiers - only ever invoked at an already-chosen position by a
        //    wrapping RANDOM_SELECTOR/MultiFeature/VegetationPatchConfiguration. ------------------------
        registerNoModifiers(context, FALLEN_JUNGLE_TREE_PLACED_KEY, configuredFeatures.getOrThrow(FALLEN_JUNGLE_TREE_KEY));
        registerNoModifiers(context, MEGA_JUNGLE_TREE_PLACED_KEY, configuredFeatures.getOrThrow(TreeFeatures.MEGA_JUNGLE_TREE));
        registerNoModifiers(context, JUNGLE_TREE_WITH_COCOA_PLACED_KEY, configuredFeatures.getOrThrow(TreeFeatures.JUNGLE_TREE));
        registerNoModifiers(context, BAMBOO_PLACED_KEY, configuredFeatures.getOrThrow(VegetationFeatures.BAMBOO_NO_PODZOL));

        // custom_moss_select_feature.json members: tall grass scatter (reuses MossFeatures' own
        // RANDOM_PATCH configured feature, just without its chunk-wide scatter/heightmap modifiers),
        // moss carpet (air-guarded, matching the original's "0 spread" single placement), jungle bush.
        registerNoModifiers(context, MOSS_SELECT_TALL_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MossFeatures.TALL_GRASS_PATCH_KEY));
        context.register(MOSS_SELECT_CARPET_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(MossFeatures.MOSS_CARPET_KEY),
                List.<PlacementModifier>of(BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)))
        ));
        registerNoModifiers(context, MOSS_SELECT_JUNGLE_BUSH_PLACED_KEY, configuredFeatures.getOrThrow(TreeFeatures.JUNGLE_BUSH));
        registerNoModifiers(context, MOSS_SELECT_PLACED_KEY, configuredFeatures.getOrThrow(MOSS_SELECT_KEY));

        registerNoModifiers(context, CUSTOM_MOSS_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(CUSTOM_MOSS_PATCH_KEY));
        registerNoModifiers(context, SELECT_MOSS_OR_JUNGLE_TREE_PLACED_KEY, configuredFeatures.getOrThrow(SELECT_MOSS_OR_JUNGLE_TREE_KEY));
        registerNoModifiers(context, SELECT_MOSS_OR_JUNGLE_TREE_UPPER_PLACED_KEY, configuredFeatures.getOrThrow(SELECT_MOSS_OR_JUNGLE_TREE_UPPER_KEY));

        // -- jungle_after_surface_cave_vines_feature.json: iterations 90, x/z uniform [0,16],
        //    y uniform [-64,60]. ---------------------------------------------------------------------
        context.register(CAVE_VINE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(CAVE_VINE_KEY),
                List.of(
                        CountPlacement.of(90),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(60)),
                        BiomeFilter.biome()
                )
        ));

        // -- jungle_surface_grass_floor_feature.json: iterations 100, y uniform [-64,62]. -------------
        context.register(GRASS_FLOOR_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(GRASS_FLOOR_KEY),
                List.of(
                        CountPlacement.of(100),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(62)),
                        BiomeFilter.biome()
                )
        ));

        // -- jungle_surface_grass_floor_upper_feature.json: iterations 200, y uniform [63,128]. --------
        context.register(GRASS_FLOOR_UPPER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(GRASS_FLOOR_UPPER_KEY),
                List.of(
                        CountPlacement.of(200),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(63), VerticalAnchor.absolute(128)),
                        BiomeFilter.biome()
                )
        ));
    }

    private static void registerNoModifiers(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration) {
        context.register(key, new PlacedFeature(configuration, List.of()));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> cfKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "underground_jungle/" + name));
    }

    private static ResourceKey<PlacedFeature> pfKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "underground_jungle/" + name));
    }

    private static TagKey<Block> tagKey(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
