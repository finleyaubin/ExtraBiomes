package net.winepicfin.extrabiomes.worldgen.features.mystic;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Java equivalent of Bedrock's mystic_forest.biome.json "sea_material": "extrabiomes:goo"
 * override - see {@link GooConversionFeature} for why this needs a feature rather than a direct
 * per-biome fluid swap. Placed once per column across the chunk (CountPlacement.of(256) is one
 * attempt per each of the 16x16 columns, same idea as vanilla's own FREEZE_TOP_LAYER coverage),
 * anchored to the world surface heightmap so it only ever starts at surface level.
 */
public class MysticFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<GooConversionFeature> GOO_CONVERSION_FEATURE =
            FEATURES.register("mystic_goo_conversion", () -> new GooConversionFeature(NoneFeatureConfiguration.CODEC));

    /** Must be called once from the mod's main class, e.g. {@code MysticFeatures.register(modEventBus);}. */
    public static void register() {
        FEATURES.register();
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_GOO_KEY = registerKey("mystic_goo_conversion");
    public static final ResourceKey<PlacedFeature> MYSTIC_GOO_PLACED_KEY = createKey("mystic_goo_conversion_placed");

    // Own flower patch rather than vanilla's shared minecraft:forest_flowers/flower_default -
    // MysticForest.java used to call BiomeDefaultFeatures.addForestFlowers/addDefaultFlowers, which
    // wired mystic_forest into those two PlacedFeatures' shared global VEGETAL_DECORATION ordering
    // graph. That graph spans every biome (vanilla, this mod's, and every other mod's) that also
    // uses either feature, and one such combination (mystic_forest plus Biomes We've Gone's
    // coconino_meadow/temperate_grove/ebony_woods) produced a "Feature order cycle found" world-load
    // crash that reordering couldn't resolve (a multi-hop contradiction elsewhere in that huge
    // shared graph, not a direct disagreement with any single biome - see ModBiomeModifiers'
    // ARS_ELEMENTAL comment for the same class of bug). A PlacedFeature only this mod's code ever
    // references can't be pulled into anyone else's ordering graph, so it can never be part of a
    // cross-mod cycle again. Richer palette than either vanilla feature it replaces (includes
    // allium, absent from both).
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_FLOWERS_KEY = registerKey("mystic_flowers");
    public static final ResourceKey<PlacedFeature> MYSTIC_FLOWERS_PLACED_KEY = createKey("mystic_flowers_placed");

    // Same reasoning as MYSTIC_FLOWERS_KEY above - swapping the flowers alone didn't clear the
    // cycle (still involved the same 4 biomes afterward), so grass and both mushroom patches -
    // mystic_forest's other remaining vanilla-shared VEGETAL_DECORATION features (forestGrass's
    // patch_grass_forest, defaultMushrooms' brown_mushroom_normal/red_mushroom_normal) - get the
    // same private-feature treatment. Recipes copied from vanilla's real VegetationFeatures/
    // VegetationPlacements (grassPatch/simplePatchConfiguration/getMushroomPlacement helpers,
    // checked against decompiled 1.21.1 bytecode) so behavior matches exactly; only the
    // PlacedFeature identity is now private to this mod.
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_GRASS_KEY = registerKey("mystic_grass");
    public static final ResourceKey<PlacedFeature> MYSTIC_GRASS_PLACED_KEY = createKey("mystic_grass_placed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_BROWN_MUSHROOM_KEY = registerKey("mystic_brown_mushroom");
    public static final ResourceKey<PlacedFeature> MYSTIC_BROWN_MUSHROOM_PLACED_KEY = createKey("mystic_brown_mushroom_placed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_RED_MUSHROOM_KEY = registerKey("mystic_red_mushroom");
    public static final ResourceKey<PlacedFeature> MYSTIC_RED_MUSHROOM_PLACED_KEY = createKey("mystic_red_mushroom_placed");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(MYSTIC_GOO_KEY, new ConfiguredFeature<>(GOO_CONVERSION_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        WeightedStateProvider flowers = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.DANDELION.defaultBlockState(), 2)
                .add(Blocks.POPPY.defaultBlockState(), 2)
                .add(Blocks.ALLIUM.defaultBlockState(), 2)
                .add(Blocks.AZURE_BLUET.defaultBlockState(), 1)
                .add(Blocks.BLUE_ORCHID.defaultBlockState(), 1)
                .add(Blocks.OXEYE_DAISY.defaultBlockState(), 1)
                .add(Blocks.CORNFLOWER.defaultBlockState(), 1)
                .add(Blocks.LILY_OF_THE_VALLEY.defaultBlockState(), 1)
                .add(Blocks.RED_TULIP.defaultBlockState(), 1)
                .add(Blocks.ORANGE_TULIP.defaultBlockState(), 1)
                .add(Blocks.WHITE_TULIP.defaultBlockState(), 1)
                .add(Blocks.PINK_TULIP.defaultBlockState(), 1));
        context.register(MYSTIC_FLOWERS_KEY, new ConfiguredFeature<>(Feature.FLOWER, new RandomPatchConfiguration(
                64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(flowers)))));

        // Same shape as vanilla's PATCH_GRASS (grassPatch(SHORT_GRASS, 32) -> tries 32, xz/y spread 7/3).
        context.register(MYSTIC_GRASS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH, new RandomPatchConfiguration(
                32, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.GRASS))))));
        // Same shape as vanilla's PATCH_BROWN_MUSHROOM/PATCH_RED_MUSHROOM (simplePatchConfiguration's
        // default tries 96, xz/y spread 7/3).
        context.register(MYSTIC_BROWN_MUSHROOM_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH, new RandomPatchConfiguration(
                96, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.BROWN_MUSHROOM))))));
        context.register(MYSTIC_RED_MUSHROOM_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH, new RandomPatchConfiguration(
                96, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.RED_MUSHROOM))))));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> configuration = configuredFeatures.getOrThrow(MYSTIC_GOO_KEY);

        context.register(MYSTIC_GOO_PLACED_KEY, new PlacedFeature(configuration, List.of(
                CountPlacement.of(256), InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome())));

        // count(2): same sparse, occasional-accent density as this mod's other hand-tuned patch
        // features (e.g. GRAND_OASIS_DEAD_BUSH_PLACED_KEY) - each attempt already scatters up to 64
        // flowers via the RandomPatchConfiguration above.
        context.register(MYSTIC_FLOWERS_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(MYSTIC_FLOWERS_KEY), List.of(
                CountPlacement.of(2), InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome())));

        // Same shape as vanilla's PATCH_GRASS_FOREST placement (worldSurfaceSquaredWithCount(2)).
        context.register(MYSTIC_GRASS_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(MYSTIC_GRASS_KEY), List.of(
                CountPlacement.of(2), InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome())));
        // Same shape as vanilla's BROWN_MUSHROOM_NORMAL/RED_MUSHROOM_NORMAL placements
        // (getMushroomPlacement(rarity, null): rarity filter, spread, MOTION_BLOCKING heightmap).
        context.register(MYSTIC_BROWN_MUSHROOM_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(MYSTIC_BROWN_MUSHROOM_KEY), List.of(
                RarityFilter.onAverageOnceEvery(256), InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome())));
        context.register(MYSTIC_RED_MUSHROOM_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(MYSTIC_RED_MUSHROOM_KEY), List.of(
                RarityFilter.onAverageOnceEvery(512), InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome())));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
