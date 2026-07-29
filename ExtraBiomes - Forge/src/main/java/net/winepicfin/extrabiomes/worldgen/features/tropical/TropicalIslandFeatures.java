package net.winepicfin.extrabiomes.worldgen.features.tropical;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;

import java.util.List;

/**
 * Port of the two Bedrock "tropical_island" feature-subsystem pieces that were never carried over
 * by the underground-jungle/moss ports:
 * <pre>
 * features/tropical_island/tropical_melon_feature.json (minecraft:single_block_feature -> minecraft:melon_block,
 *   enforce_survivability_rules false, enforce_placement_rules false, may_attach_to.bottom = [grass, moss_block])
 * features/tropical_island/island_grass_floor_feature.json (minecraft:vegetation_patch_feature,
 *   replaceable_blocks=[sand], ground_block=grass, surface floor, depth 1-1, vertical_range 5,
 *   vegetation_chance 0, horizontal_radius 32-32, extra_edge_column_chance 0.3)
 * </pre>
 * <p>
 * The third member of the tropical_island "growth" chain (moss carpet / tall grass / jungle bush)
 * is already covered by {@link net.winepicfin.extrabiomes.worldgen.features.moss.MossFeatures} and
 * is not duplicated here.
 * <p>
 * {@code island_grass_floor_feature.json}'s {@code vegetation_chance: 0} means the vegetation
 * ingredient (Bedrock's {@code select_moss_or_jungle_tree_feature}) never actually fires - this is
 * intentionally preserved below by passing a {@code vegetationChance} of {@code 0.0F}, making
 * {@link #GRASS_FLOOR_PLACED_KEY} functionally just a sand-to-grass floor patch with no vegetation
 * on top, matching Bedrock exactly.
 */
public class TropicalIslandFeatures {

    // -----------------------------------------------------------------
    // tropical_melon_feature.json
    // -----------------------------------------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> MELON_KEY = configuredKey("tropical_melon");

    /**
     * extrabiomes:tropical/tropical_melon - places a single {@link Blocks#MELON} where the block
     * below is grass or moss (Bedrock's {@code may_attach_to.bottom} allowlist). Register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TropicalIslandFeatures.MELON_PLACED_KEY)}.
     */
    public static final ResourceKey<PlacedFeature> MELON_PLACED_KEY = placedKey("tropical_melon");

    // -----------------------------------------------------------------
    // island_grass_floor_feature.json
    // -----------------------------------------------------------------
    public static final TagKey<Block> ISLAND_GRASS_FLOOR_REPLACEABLE = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "island_grass_floor_replaceable"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRASS_FLOOR_KEY = configuredKey("island_grass_floor");

    /**
     * extrabiomes:tropical/island_grass_floor - converts sand to grass on the surface (with the
     * vegetation ingredient disabled to match Bedrock's {@code vegetation_chance: 0}). Register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TropicalIslandFeatures.GRASS_FLOOR_PLACED_KEY)}.
     */
    public static final ResourceKey<PlacedFeature> GRASS_FLOOR_PLACED_KEY = placedKey("island_grass_floor");

    // ===================================================================
    // configured features
    // ===================================================================
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(MELON_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.MELON.defaultBlockState()))));

        // island_grass_floor_feature.json: depth 1, vertical_range 5, vegetation_chance 0,
        // horizontal_radius 8, extra_edge_column_chance 0.3.
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        context.register(GRASS_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        ISLAND_GRASS_FLOOR_REPLACEABLE,
                        BlockStateProvider.simple(Blocks.GRASS.defaultBlockState()),
                        placedFeatures.getOrThrow(UndergroundJungleFeatures.SELECT_MOSS_OR_JUNGLE_TREE_PLACED_KEY),
                        CaveSurface.FLOOR,
                        ConstantInt.of(1),
                        0.0F,
                        5,
                        0.0F,
                        ConstantInt.of(8),
                        0.3F
                )));
    }

    // ===================================================================
    // placed features
    // ===================================================================
    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // may_attach_to.bottom = [minecraft:grass, minecraft:moss_block], min_sides_must_attach 1.
        context.register(MELON_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(MELON_KEY),
                List.of(
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new BlockPos(0, -1, 0), Blocks.GRASS, Blocks.MOSS_BLOCK)),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        context.register(GRASS_FLOOR_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(GRASS_FLOOR_KEY),
                List.of(
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "tropical/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "tropical/" + name));
    }
}
