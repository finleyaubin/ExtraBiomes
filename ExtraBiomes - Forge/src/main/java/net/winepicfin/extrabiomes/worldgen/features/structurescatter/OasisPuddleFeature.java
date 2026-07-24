package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Worked example / reference implementation of {@link SingleStructureFeature}, converted from
 * Bedrock's "extrabiomes:oasis/oasis_puddle" ({@code minecraft:structure_template_feature}) plus
 * its feature_rules distribution ("extrabiomes:oasis_puddle_placer").
 * <p>
 * Bedrock source:
 * <pre>
 * features/oasis/oasis_puddle.json:
 *   structure_name: "extrabiomes:oasis_puddle"
 *   constraints: { block_intersection.block_allowlist: [minecraft:sand, minecraft:red_sand], grounded: {} }
 * feature_rules/oasis/oasis_puddle_placer.json:
 *   distribution: iterations 1, scatter_chance 1, x/z uniform [0,16], y = heightmap(worldx,worldz) - 4
 *   conditions: biome_filter has_biome_tag "oasis"
 * </pre>
 * Mapping notes (see the class-level docs on {@link SingleStructureConfiguration} for the general rules):
 * <ul>
 *   <li>{@code structure_name} -> the converted .nbt at
 *       data/extrabiomes/structures/structurescatter/oasis_puddle.nbt, referenced here as
 *       "extrabiomes:structurescatter/oasis_puddle".</li>
 *   <li>{@code iterations: 1} + {@code scatter_chance: 1} (always run once) -> {@link CountPlacement#of(int)} with 1.</li>
 *   <li>{@code x/z uniform [0,16]} (once per chunk, spread across it) -> {@link InSquarePlacement#spread()}.</li>
 *   <li>{@code y = heightmap - 4} -> {@link HeightmapPlacement} on WORLD_SURFACE_WG (offset 0) combined with
 *       {@code groundOffset = -4} in the {@link SingleStructureConfiguration}, which is applied inside
 *       {@link SingleStructureFeature#place} right before the structure is stamped down.</li>
 *   <li>{@code constraints.grounded} + {@code block_intersection.block_allowlist} (the puddle must sit on
 *       sand/red sand) -> a {@link BlockPredicateFilter} testing the block one below the (pre-groundOffset)
 *       heightmap surface position against {@link BlockPredicate#matchesBlocks}, i.e. this is expressed as a
 *       PlacementModifier rather than as feature configuration, exactly as Bedrock's "constraints" block is
 *       itself a placement-time constraint, not part of the structure feature's own definition.</li>
 *   <li>{@code has_biome_tag "oasis"} -> intentionally NOT included here; per project convention the biome
 *       wiring pass adds this PlacedFeature to the relevant biome(s) directly via
 *       biomeBuilder.addFeature(...), and {@link BiomeFilter#biome()} (last modifier below) is what makes
 *       that per-biome registration actually take effect during generation.</li>
 * </ul>
 */
public class OasisPuddleFeature {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OASIS_PUDDLE_SCATTER_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "oasis_puddle_scatter"));
    public static final ResourceKey<PlacedFeature> OASIS_PUDDLE_SCATTER_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "oasis_puddle_scatter"));

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(OASIS_PUDDLE_SCATTER_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "structurescatter/oasis_puddle"), -4)
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(OASIS_PUDDLE_SCATTER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(OASIS_PUDDLE_SCATTER_KEY),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new BlockPos(0, -1, 0), Blocks.SAND, Blocks.RED_SAND)),
                        BiomeFilter.biome()
                )
        ));
    }
}
