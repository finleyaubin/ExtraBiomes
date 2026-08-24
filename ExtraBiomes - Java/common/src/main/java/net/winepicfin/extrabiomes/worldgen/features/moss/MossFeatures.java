package net.winepicfin.extrabiomes.worldgen.features.moss;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

// Bedrock's moss aggregate_feature has no Java equivalent, so its three members are registered as separate top-level PlacedFeatures that the biome must add individually at the same decoration step.
public class MossFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_CARPET_KEY = configuredKey("moss_carpet");

    public static final ResourceKey<PlacedFeature> MOSS_CARPET_SCATTER_PLACED_KEY = placedKey("moss_carpet_scatter");

    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_GRASS_KEY = configuredKey("tall_grass");
    // Not a top-level decoration by itself; only the inner placement of TALL_GRASS_PATCH_KEY's RandomPatchConfiguration.
    public static final ResourceKey<PlacedFeature> TALL_GRASS_INNER_PLACED_KEY = placedKey("tall_grass_inner");

    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_GRASS_PATCH_KEY = configuredKey("tall_grass_patch");

    public static final ResourceKey<PlacedFeature> TALL_GRASS_SCATTER_PLACED_KEY = placedKey("tall_grass_scatter");

    // Vanilla's own jungle_bush placed feature, reused directly rather than re-registered.
    public static final ResourceKey<PlacedFeature> JUNGLE_BUSH_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation("minecraft", "jungle_bush"));

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        context.register(MOSS_CARPET_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.MOSS_CARPET.defaultBlockState()))));

        context.register(TALL_GRASS_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.GRASS.defaultBlockState()))));

        Holder<PlacedFeature> tallGrassInner = placedFeatures.getOrThrow(TALL_GRASS_INNER_PLACED_KEY);
        context.register(TALL_GRASS_PATCH_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(30, 8, 3, tallGrassInner)));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Bedrock's source feature scatters 10 attempts with 0 spread, which always lands on the same block; collapsed to a single placement.
        context.register(MOSS_CARPET_SCATTER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(MOSS_CARPET_KEY),
                List.of(
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        // inner grass placement: no modifiers of its own besides the air check (mirrors vanilla patch_grass.json).
        context.register(TALL_GRASS_INNER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(TALL_GRASS_KEY),
                List.<PlacementModifier>of(BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)))
        ));

        // top-level tall grass scatter: one patch attempt per chosen column, spread across the chunk on the surface.
        context.register(TALL_GRASS_SCATTER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(TALL_GRASS_PATCH_KEY),
                List.of(
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "moss/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "moss/" + name));
    }
}
