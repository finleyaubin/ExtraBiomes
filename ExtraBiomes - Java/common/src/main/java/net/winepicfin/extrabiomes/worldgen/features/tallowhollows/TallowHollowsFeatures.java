package net.winepicfin.extrabiomes.worldgen.features.tallowhollows;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;

import java.util.List;
import java.util.stream.Stream;

/**
 * Everything Tallow Hollows scatters through its caverns: tallow pillars, forgotten bookshelves,
 * ever-wick blooms, lit candle clusters, ceiling wax drips and honeycomb veins. Placement counts
 * are attempts per chunk; most land in rock and are discarded by the floor/ceiling scan, like
 * vanilla's own dripstone cave features.
 */
public class TallowHollowsFeatures {
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE_TYPE);

    public static final RegistrySupplier<MapCodec<TallowPillarFeature>> TALLOW_PILLAR_FEATURE =
            FEATURES.register("tallow_pillar", () -> TallowPillarFeature.CODEC);
    public static final RegistrySupplier<MapCodec<ForgottenShelvesFeature>> FORGOTTEN_SHELVES_FEATURE =
            FEATURES.register("forgotten_shelves", () -> ForgottenShelvesFeature.CODEC);

    public static void register() {
        FEATURES.register();
    }

    private static final int SCAN_RANGE = 12;
    private static final int PILLAR_ATTEMPTS_PER_CHUNK = 10;
    private static final int CHUNKS_PER_SHELF_CLUSTER_ATTEMPT = 2;
    private static final int SHELF_CLUSTER_ATTEMPTS = 4;
    private static final int WICK_BLOOM_PATCHES_PER_CHUNK = 20;
    private static final int CANDLE_CLUSTER_PATCHES_PER_CHUNK = 8;
    private static final int WAX_DRIP_ATTEMPTS_PER_CHUNK = 96;
    private static final int HONEYCOMB_VEINS_PER_CHUNK = 10;
    private static final int HONEYCOMB_VEIN_SIZE = 6;
    private static final int MIN_CANDLES = 1;
    private static final int MAX_CANDLES = 4;

    public static final ResourceKey<Feature> PILLAR_KEY = configuredKey("tallow_hollows_pillar");
    public static final ResourceKey<Feature> FORGOTTEN_SHELVES_KEY = configuredKey("tallow_hollows_forgotten_shelves");
    public static final ResourceKey<Feature> WICK_BLOOM_KEY = configuredKey("tallow_hollows_wick_bloom");
    public static final ResourceKey<Feature> CANDLE_CLUSTER_KEY = configuredKey("tallow_hollows_candle_cluster");
    public static final ResourceKey<Feature> WAX_DRIP_KEY = configuredKey("tallow_hollows_wax_drip");
    public static final ResourceKey<Feature> HONEYCOMB_VEIN_KEY = configuredKey("tallow_hollows_honeycomb_vein");

    public static final ResourceKey<PlacedFeature> PILLAR_PLACED_KEY = placedKey("tallow_hollows_pillar");
    public static final ResourceKey<PlacedFeature> FORGOTTEN_SHELVES_PLACED_KEY = placedKey("tallow_hollows_forgotten_shelves");
    public static final ResourceKey<PlacedFeature> WICK_BLOOM_PLACED_KEY = placedKey("tallow_hollows_wick_bloom");
    public static final ResourceKey<PlacedFeature> CANDLE_CLUSTER_PLACED_KEY = placedKey("tallow_hollows_candle_cluster");
    public static final ResourceKey<PlacedFeature> WAX_DRIP_PLACED_KEY = placedKey("tallow_hollows_wax_drip");
    public static final ResourceKey<PlacedFeature> HONEYCOMB_VEIN_PLACED_KEY = placedKey("tallow_hollows_honeycomb_vein");

    public static void bootstrapConfigured(BootstrapContext<Feature> context) {
        context.register(PILLAR_KEY, new TallowPillarFeature());
        context.register(FORGOTTEN_SHELVES_KEY, new ForgottenShelvesFeature());
        context.register(WICK_BLOOM_KEY, new SimpleBlockFeature(BlockStateProvider.holderOf(ModBlocks.EVER_WICK.get())));
        context.register(CANDLE_CLUSTER_KEY, new SimpleBlockFeature(new WeightedStateProvider(litCandleCounts())));
        context.register(WAX_DRIP_KEY, new SimpleBlockFeature(BlockStateProvider.holderOf(ModBlocks.WAX_DRIP.get())));
        context.register(HONEYCOMB_VEIN_KEY, new OreFeature(honeycombTargets(), HONEYCOMB_VEIN_SIZE));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> configuredFeatures = context.lookup(Registries.FEATURE);

        register(context, PILLAR_PLACED_KEY, configuredFeatures.getOrThrow(PILLAR_KEY),
                onCaveFloor(List.of(CountPlacement.of(PILLAR_ATTEMPTS_PER_CHUNK))));
        register(context, FORGOTTEN_SHELVES_PLACED_KEY, configuredFeatures.getOrThrow(FORGOTTEN_SHELVES_KEY),
                onCaveFloor(List.of(RarityFilter.onAverageOnceEvery(CHUNKS_PER_SHELF_CLUSTER_ATTEMPT), CountPlacement.of(SHELF_CLUSTER_ATTEMPTS))));
        register(context, WICK_BLOOM_PLACED_KEY, configuredFeatures.getOrThrow(WICK_BLOOM_KEY), concat(
                onCaveFloor(List.of(CountPlacement.of(WICK_BLOOM_PATCHES_PER_CHUNK))),
                floorPatch(UniformInt.of(1, 2), 3, ModBlocks.EVER_WICK.get()),
                List.<PlacementModifier>of(BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new BlockPos(0, -1, 0), List.of(ModBlocks.TALLOWSTONE.get()))))));
        register(context, CANDLE_CLUSTER_PLACED_KEY, configuredFeatures.getOrThrow(CANDLE_CLUSTER_KEY), concat(
                onCaveFloor(List.of(CountPlacement.of(CANDLE_CLUSTER_PATCHES_PER_CHUNK))),
                floorPatch(UniformInt.of(2, 5), 2, Blocks.CANDLE)));
        register(context, WAX_DRIP_PLACED_KEY, configuredFeatures.getOrThrow(WAX_DRIP_KEY), List.of(
                CountPlacement.of(WAX_DRIP_ATTEMPTS_PER_CHUNK),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, SCAN_RANGE),
                OffsetPlacement.vertical(ConstantInt.of(-1)),
                BiomeFilter.biome()));
        register(context, HONEYCOMB_VEIN_PLACED_KEY, configuredFeatures.getOrThrow(HONEYCOMB_VEIN_KEY), List.of(
                CountPlacement.of(HONEYCOMB_VEINS_PER_CHUNK),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                BiomeFilter.biome()));
    }

    private static WeightedList<BlockState> litCandleCounts() {
        WeightedList.Builder<BlockState> candles = WeightedList.builder();
        for (int count = MIN_CANDLES; count <= MAX_CANDLES; count++) {
            candles.add(Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, count).setValue(CandleBlock.LIT, true), 1);
        }
        return candles.build();
    }

    private static List<BlockReplacement> honeycombTargets() {
        BlockState honeycomb = Blocks.HONEYCOMB_BLOCK.defaultBlockState();
        return List.of(
                BlockReplacement.replace(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), honeycomb),
                BlockReplacement.replace(new BlockMatchTest(ModBlocks.TALLOWSTONE.get()), honeycomb));
    }

    // Lands on the first air block above a cave floor, the way vanilla's dripstone and glacier pillars find theirs.
    private static List<PlacementModifier> onCaveFloor(List<PlacementModifier> frequency) {
        return concat(frequency, List.<PlacementModifier>of(
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, SCAN_RANGE),
                OffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()));
    }

    // Spreads a few copies around an already-found floor spot, keeping only those that sit in open air and can stand there.
    private static List<PlacementModifier> floorPatch(UniformInt size, int spread, Block placed) {
        return List.of(
                CountPlacement.of(size),
                OffsetPlacement.ofTriangle(spread, 1),
                BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(placed))));
    }

    @SafeVarargs
    private static List<PlacementModifier> concat(List<PlacementModifier>... parts) {
        return Stream.of(parts).flatMap(List::stream).toList();
    }

    private static ResourceKey<Feature> configuredKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<Feature> feature, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(feature, List.copyOf(modifiers)));
    }
}
