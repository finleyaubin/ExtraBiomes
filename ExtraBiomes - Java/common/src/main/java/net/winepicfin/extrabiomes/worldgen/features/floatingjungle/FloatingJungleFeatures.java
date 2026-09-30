package net.winepicfin.extrabiomes.worldgen.features.floatingjungle;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FloatingJungleFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_ISLAND_KEY = configuredKey("select_floating_island");
    public static final ResourceKey<PlacedFeature> SELECT_ISLAND_PLACED_KEY = placedKey("select_floating_island");

    // Keep in sync with TEMPLATES in tools/build_floating_islands.py.
    private static final String[] ISLAND_TEMPLATES = {
            "islet_small_1", "islet_small_2", "islet_small_3", "islet_large_1", "islet_large_2",
            "archipelago_1", "archipelago_2", "archipelago_ruin"
    };
    private static final int[] ISLAND_WEIGHTS = {17, 17, 16, 12, 13, 10, 10, 5};

    // Skips the island if its volume is already taken (terrain, Sky City, another island).
    private static final float MIN_CLEAR_FRACTION = 0.95F;

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        List<Holder<PlacedFeature>> islands = new ArrayList<>();
        for (String template : ISLAND_TEMPLATES) {
            ResourceLocation structure = new ResourceLocation(ExtraBiomes.MOD_ID, "floating_jungle/" + template);
            SingleStructureConfiguration config = new SingleStructureConfiguration(structure, Optional.empty(), 0, true, MIN_CLEAR_FRACTION);
            Holder<ConfiguredFeature<?, ?>> island = Holder.direct(new ConfiguredFeature<>(ModStructureScatterFeatures.SINGLE_STRUCTURE.get(), config));
            islands.add(PlacementUtils.inlinePlaced(island));
        }

        // RandomFeatureConfiguration tries entries in order with raw chances, so each weight becomes its share of the mass not yet claimed; the last island is the default.
        List<WeightedPlacedFeature> entries = new ArrayList<>();
        int remainingWeight = 0;
        for (int weight : ISLAND_WEIGHTS) {
            remainingWeight += weight;
        }
        for (int i = 0; i < ISLAND_WEIGHTS.length - 1; i++) {
            entries.add(new WeightedPlacedFeature(islands.get(i), (float) ISLAND_WEIGHTS[i] / remainingWeight));
            remainingWeight -= ISLAND_WEIGHTS[i];
        }
        context.register(SELECT_ISLAND_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR,
                new RandomFeatureConfiguration(entries, islands.get(islands.size() - 1))));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Lifted 28-60 blocks clear of the peak (vertical offsets cap at 16, hence three) so the underside cone and hanging vines have room; SingleStructureFeature skips anything that would poke above the build limit.
        context.register(SELECT_ISLAND_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_ISLAND_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(6),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        RandomOffsetPlacement.vertical(ConstantInt.of(16)),
                        RandomOffsetPlacement.vertical(ConstantInt.of(16)),
                        RandomOffsetPlacement.vertical(UniformInt.of(-4, 12)),
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
