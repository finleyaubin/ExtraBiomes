package net.winepicfin.extrabiomes.worldgen.features.floatingjungle;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FloatingJungleFeatures {
    public static final ResourceKey<Feature> SELECT_ISLAND_KEY = configuredKey("select_floating_island");
    public static final ResourceKey<PlacedFeature> SELECT_ISLAND_PLACED_KEY = placedKey("select_floating_island");

    // Keep in sync with TEMPLATES in tools/build_floating_islands.py.
    private static final String[] ISLAND_TEMPLATES = {
            "islet_small_1", "islet_small_2", "islet_small_3", "islet_large_1", "islet_large_2",
            "archipelago_1", "archipelago_2", "archipelago_ruin", "islet_temple"
    };
    private static final int[] ISLAND_WEIGHTS = {17, 17, 16, 12, 13, 10, 10, 5, 4};

    // Skips the island if its volume is already taken (terrain, Sky City, another island).
    private static final float MIN_CLEAR_FRACTION = 0.95F;

    public static final ResourceKey<Feature> SELECT_GIANT_TREE_KEY = configuredKey("select_giant_jungle_tree");
    public static final ResourceKey<PlacedFeature> SELECT_GIANT_TREE_PLACED_KEY = placedKey("select_giant_jungle_tree");

    // Keep in sync with GIANT_TREES in tools/build_floating_islands.py.
    private static final String[] GIANT_TREE_TEMPLATES = {"giant_tree_1", "giant_tree_2", "giant_tree_3"};
    private static final int[] GIANT_TREE_WEIGHTS = {1, 1, 1};

    // The roots are buried this deep in the peak (GIANT_ROOT_DEPTH in the generator), so the trunk stands on the real ground.
    private static final int GIANT_TREE_GROUND_OFFSET = -5;
    // Looser than the islands' fraction: a 40-wide footprint on a mountain top always clips some slope.
    private static final float GIANT_TREE_MIN_CLEAR_FRACTION = 0.8F;

    public static void bootstrapConfigured(BootstrapContext<Feature> context) {
        context.register(SELECT_ISLAND_KEY, weightedSelector(context, ISLAND_TEMPLATES, ISLAND_WEIGHTS, 0, MIN_CLEAR_FRACTION));
        context.register(SELECT_GIANT_TREE_KEY, weightedSelector(context, GIANT_TREE_TEMPLATES, GIANT_TREE_WEIGHTS,
                GIANT_TREE_GROUND_OFFSET, GIANT_TREE_MIN_CLEAR_FRACTION));
    }

    private static RandomSelectorFeature weightedSelector(BootstrapContext<Feature> context, String[] templates, int[] weights, int groundOffset, float minClearFraction) {
        List<Holder<PlacedFeature>> placed = new ArrayList<>();
        for (String template : templates) {
            Identifier structure = Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "floating_jungle/" + template);
            placed.add(PlacementUtils.inlinePlaced(Holder.direct(
                    new SingleStructureFeature(structure, Optional.empty(), groundOffset, true, minClearFraction))));
        }

        // RandomSelectorFeature tries entries in order with raw chances, so each weight becomes its share of the mass not yet claimed; the last entry is the default.
        List<WeightedPlacedFeature> entries = new ArrayList<>();
        int remainingWeight = 0;
        for (int weight : weights) {
            remainingWeight += weight;
        }
        for (int i = 0; i < weights.length - 1; i++) {
            entries.add(new WeightedPlacedFeature(placed.get(i), (float) weights[i] / remainingWeight));
            remainingWeight -= weights[i];
        }
        return new RandomSelectorFeature(entries, placed.get(placed.size() - 1));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> configuredFeatures = context.lookup(Registries.FEATURE);

        // Lifted 28-60 blocks clear of the peak (vertical offsets cap at 16, hence three) so the underside cone and hanging vines have room; SingleStructureFeature skips anything that would poke above the build limit.
        context.register(SELECT_ISLAND_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_ISLAND_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(6),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        OffsetPlacement.vertical(ConstantInt.of(16)),
                        OffsetPlacement.vertical(ConstantInt.of(16)),
                        OffsetPlacement.vertical(UniformInt.of(-4, 12)),
                        BiomeFilter.biome()
                )));

        // Rare on purpose: each one is ~40 wide and 100+ tall, and the window check drops about a third of tries on top of this.
        context.register(SELECT_GIANT_TREE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_GIANT_TREE_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(24),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )));
    }

    private static ResourceKey<Feature> configuredKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
}
