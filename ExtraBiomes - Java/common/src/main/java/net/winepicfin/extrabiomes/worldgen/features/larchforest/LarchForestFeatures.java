package net.winepicfin.extrabiomes.worldgen.features.larchforest;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;
import net.winepicfin.extrabiomes.worldgen.features.glacier.GlacierFeatures;
import net.winepicfin.extrabiomes.worldgen.placement.LakeSafeOrigin;
import net.winepicfin.extrabiomes.worldgen.tree.custom.LarchFoliagePlacer;

import java.util.List;

/**
 * Everything the Larch Forest scatters, modelled on the reference photo in issue #388: a dense belt of
 * golden larches with dark spruces (some 2x2 giants) mixed in, open meadow clearings dotted with young conifers,
 * mossy rocks and a patchy first dusting of snow. Ground the ice sheet left behind shows through as erratic
 * boulders and kettle lakes. The dry grass tufts of the 26.x original don't exist in 1.20.1.
 */
public class LarchForestFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<SnowPatchFeature> SNOW_PATCH_FEATURE =
            FEATURES.register("snow_patch", () -> new SnowPatchFeature(NoneFeatureConfiguration.CODEC));

    public static void register() {
        FEATURES.register();
    }

    // Below this value of vanilla's biome-info noise the forest opens into a meadow clearing.
    private static final double CLEARING_NOISE_LEVEL = -0.1;
    private static final int TREES_PER_CHUNK_IN_CLEARING = 1;
    private static final int TREES_PER_CHUNK_IN_FOREST = 9;
    private static final float TALL_LARCH_CHANCE = 0.2F;
    private static final float SPRUCE_CHANCE = 0.25F;
    private static final float PINE_CHANCE = 0.05F;
    private static final float MEGA_SPRUCE_CHANCE = 0.1F;
    private static final float YOUNG_SPRUCE_CHANCE = 0.65F;
    private static final int YOUNG_TREES_PER_CHUNK = 3;
    private static final int SNOW_PATCHES_PER_CHUNK = 4;
    private static final int CHUNKS_PER_MOSSY_ROCK = 3;
    private static final int CHUNKS_PER_ERRATIC = 4;
    private static final int CHUNKS_PER_KETTLE_LAKE = 10;

    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_LARCH_KEY = configuredKey("tall_larch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> YOUNG_LARCH_KEY = configuredKey("young_larch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> YOUNG_SPRUCE_KEY = configuredKey("young_spruce");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_KEY = configuredKey("larch_forest_trees");
    public static final ResourceKey<ConfiguredFeature<?, ?>> YOUNG_TREES_KEY = configuredKey("larch_forest_young_trees");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SNOW_PATCH_KEY = configuredKey("larch_forest_snow_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ERRATIC_KEY = configuredKey("larch_forest_erratic");
    public static final ResourceKey<ConfiguredFeature<?, ?>> KETTLE_LAKE_KEY = configuredKey("larch_forest_kettle_lake");

    public static final ResourceKey<PlacedFeature> TREES_PLACED_KEY = placedKey("larch_forest_trees");
    public static final ResourceKey<PlacedFeature> YOUNG_TREES_PLACED_KEY = placedKey("larch_forest_young_trees");
    public static final ResourceKey<PlacedFeature> SNOW_PATCH_PLACED_KEY = placedKey("larch_forest_snow_patch");
    public static final ResourceKey<PlacedFeature> MOSSY_ROCK_PLACED_KEY = placedKey("larch_forest_mossy_rock");
    public static final ResourceKey<PlacedFeature> ERRATIC_PLACED_KEY = placedKey("larch_forest_erratic");
    public static final ResourceKey<PlacedFeature> KETTLE_LAKE_PLACED_KEY = placedKey("larch_forest_kettle_lake");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(TALL_LARCH_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.LARCH_LOG.get()),
                new StraightTrunkPlacer(16, 5, 0),
                BlockStateProvider.simple(ModBlocks.LARCH_LEAVES.get()),
                new LarchFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), UniformInt.of(3, 5)),
                new TwoLayersFeatureSize(2, 0, 2)).build()));
        context.register(YOUNG_LARCH_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.LARCH_LOG.get()),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(ModBlocks.LARCH_LEAVES.get()),
                new LarchFoliagePlacer(UniformInt.of(1, 2), ConstantInt.of(1), ConstantInt.of(1)),
                new TwoLayersFeatureSize(1, 0, 1)).build()));
        // Vanilla spruces start at 6 logs; the photo's meadow is dotted with knee-high ones.
        context.register(YOUNG_SPRUCE_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.SPRUCE_LOG),
                new StraightTrunkPlacer(2, 1, 0),
                BlockStateProvider.simple(Blocks.SPRUCE_LEAVES),
                new SpruceFoliagePlacer(UniformInt.of(1, 2), UniformInt.of(0, 1), ConstantInt.of(1)),
                new TwoLayersFeatureSize(1, 0, 1)).build()));

        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        PlacementModifier larchCanGrowHere = PlacementUtils.filteredByBlockSurvival(ModBlocks.LARCH_SAPLING.get());

        context.register(TREES_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(TALL_LARCH_KEY), larchCanGrowHere), TALL_LARCH_CHANCE),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TreePlacements.SPRUCE_CHECKED), SPRUCE_CHANCE),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TreePlacements.PINE_CHECKED), PINE_CHANCE),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TreePlacements.MEGA_SPRUCE_CHECKED), MEGA_SPRUCE_CHANCE)),
                PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(ModConfigureFeatures.LARCH_KEY), larchCanGrowHere))));
        context.register(YOUNG_TREES_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(
                        PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(YOUNG_SPRUCE_KEY), PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING)),
                        YOUNG_SPRUCE_CHANCE)),
                PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(YOUNG_LARCH_KEY), larchCanGrowHere))));

        context.register(SNOW_PATCH_KEY, new ConfiguredFeature<>(SNOW_PATCH_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        // Erratics are rock the ice carried in from elsewhere, so they're the stones the local slopes aren't made of.
        context.register(ERRATIC_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(erratic(Blocks.GRANITE), 0.4F),
                        new WeightedPlacedFeature(erratic(Blocks.DIORITE), 0.5F)),
                erratic(Blocks.ANDESITE))));
        context.register(KETTLE_LAKE_KEY, GlacierFeatures.pool(Blocks.WATER, Blocks.STONE));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, TREES_PLACED_KEY, configuredFeatures.getOrThrow(TREES_KEY),
                VegetationPlacements.treePlacement(NoiseThresholdCountPlacement.of(CLEARING_NOISE_LEVEL, TREES_PER_CHUNK_IN_CLEARING, TREES_PER_CHUNK_IN_FOREST)));
        register(context, YOUNG_TREES_PLACED_KEY, configuredFeatures.getOrThrow(YOUNG_TREES_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(YOUNG_TREES_PER_CHUNK)));

        register(context, SNOW_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(SNOW_PATCH_KEY),
                List.of(CountPlacement.of(SNOW_PATCHES_PER_CHUNK), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, MOSSY_ROCK_PLACED_KEY, configuredFeatures.getOrThrow(MiscOverworldFeatures.FOREST_ROCK),
                List.of(RarityFilter.onAverageOnceEvery(CHUNKS_PER_MOSSY_ROCK), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, ERRATIC_PLACED_KEY, configuredFeatures.getOrThrow(ERRATIC_KEY),
                List.of(RarityFilter.onAverageOnceEvery(CHUNKS_PER_ERRATIC), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
        register(context, KETTLE_LAKE_PLACED_KEY, configuredFeatures.getOrThrow(KETTLE_LAKE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(CHUNKS_PER_KETTLE_LAKE), InSquarePlacement.spread(), LakeSafeOrigin.INSTANCE,
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome()));
    }

    private static Holder<PlacedFeature> erratic(Block rock) {
        return PlacementUtils.inlinePlaced(Feature.FOREST_ROCK, new BlockStateConfiguration(rock.defaultBlockState()));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(feature, List.copyOf(modifiers)));
    }
}
