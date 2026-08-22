package net.winepicfin.extrabiomes.worldgen.features.stonepillars;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;

/**
 * Port of Bedrock's "stone_pillars" subsystem, converted from:
 * <pre>
 * features/stone_pillars/select_stone_pillars_feature.json (minecraft:weighted_random_feature):
 *   weighted 1:1:1 between stone_pillar_1 / stone_pillar_2 / stone_pillar_3
 * features/stone_pillars/stone_pillar_{1,2,3}.json (minecraft:structure_template_feature):
 *   structure_name: "extrabiomes:stone_pillar_{1,2,3}"
 *   constraints.block_intersection.block_allowlist: [air, grass, dirt, water, stone]
 * feature_rules/stone_pillars/stone_pillars_feature.json:
 *   tag "stone_pillars", placement_pass "before_underground_pass" (mapped to SURFACE_STRUCTURES step)
 *   distribution: iterations 1, scatter_chance 1, x/z uniform [0,16],
 *                 y = query.above_top_solid(worldx, worldz) - 5
 * </pre>
 * Each of the three raw structures was converted with tools/mc2java.py into
 * data/extrabiomes/structures/stone_pillars/stone_pillar_{1,2,3}.nbt (this conversion also
 * uncovered and fixed a pre-existing gap in tools/block_map.py: plain "minecraft:stone" with a
 * Bedrock "stone_type" state had no mapping at all and was coming through as unmapped/air; a
 * STONE_TYPE table mapping stone/granite/diorite/andesite (+ their "smooth"/polished variants)
 * was added so these pillars - which are ~100% minecraft:stone - convert correctly).
 * <p>
 * The weighted random pick reuses the shared {@code SingleStructureFeature}/
 * {@link SingleStructureConfiguration} infra (one ConfiguredFeature per pillar variant, each with
 * random rotation since Bedrock's structure_template_feature entries specify no fixed
 * facing_direction, and groundOffset -5 matching "above_top_solid - 5"), wrapped in a single
 * vanilla {@link Feature#RANDOM_SELECTOR} ConfiguredFeature for the equal 1:1:1 weighting.
 * <p>
 * Applies only to the JunglePillars Java biome, which wires it in via
 * {@code biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, STONE_PILLARS_PLACED_KEY)}.
 */
public class StonePillarsFeature {
    private static final ResourceKey<ConfiguredFeature<?, ?>> STONE_PILLAR_1_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillar_1"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> STONE_PILLAR_2_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillar_2"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> STONE_PILLAR_3_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillar_3"));

    private static final ResourceKey<PlacedFeature> STONE_PILLAR_1_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillar_1"));
    private static final ResourceKey<PlacedFeature> STONE_PILLAR_2_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillar_2"));
    private static final ResourceKey<PlacedFeature> STONE_PILLAR_3_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillar_3"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_STONE_PILLARS_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "select_stone_pillars"));
    public static final ResourceKey<PlacedFeature> STONE_PILLARS_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "select_stone_pillars"));

    private static final int GROUND_OFFSET = -5; // query.above_top_solid(worldx, worldz) - 5

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(STONE_PILLAR_1_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillars/stone_pillar_1"), GROUND_OFFSET)
        ));
        context.register(STONE_PILLAR_2_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillars/stone_pillar_2"), GROUND_OFFSET)
        ));
        context.register(STONE_PILLAR_3_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "stone_pillars/stone_pillar_3"), GROUND_OFFSET)
        ));

        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        context.register(SELECT_STONE_PILLARS_KEY, new ConfiguredFeature<>(
                Feature.RANDOM_SELECTOR,
                new RandomFeatureConfiguration(
                        List.of(
                                new WeightedPlacedFeature(placedFeatures.getOrThrow(STONE_PILLAR_1_PLACED_KEY), 1.0f / 3.0f),
                                new WeightedPlacedFeature(placedFeatures.getOrThrow(STONE_PILLAR_2_PLACED_KEY), 1.0f / 2.0f)
                        ),
                        placedFeatures.getOrThrow(STONE_PILLAR_3_PLACED_KEY)
                )
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Shared block-below constraint for all three pillar variants + the top-level selector:
        // Bedrock's constraints.block_intersection.block_allowlist [air, grass, dirt, water, stone].
        List<PlacementModifier> perPillarModifiers = List.of(
                BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                        new BlockPos(0, -1, 0),
                        Blocks.AIR, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.WATER, Blocks.STONE
                ))
        );

        context.register(STONE_PILLAR_1_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(STONE_PILLAR_1_KEY), perPillarModifiers));
        context.register(STONE_PILLAR_2_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(STONE_PILLAR_2_KEY), perPillarModifiers));
        context.register(STONE_PILLAR_3_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(STONE_PILLAR_3_KEY), perPillarModifiers));

        context.register(STONE_PILLARS_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_STONE_PILLARS_KEY),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }
}
