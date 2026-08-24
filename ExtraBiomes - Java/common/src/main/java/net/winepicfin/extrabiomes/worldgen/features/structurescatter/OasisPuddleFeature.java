package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;
import java.util.Optional;

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
 *       "extrabiomes:structurescatter/oasis_puddle". Besides water and the 11x11 sand rim, the
 *       template carries a loaded Barrel (Bedrock's "loot_tables/chests/shipwrecktreasure.json" ->
 *       Java's vanilla "minecraft:chests/shipwreck_treasure", carried through by
 *       tools/mc2java.py's container block-entity handling) plus four coral types and a sea pickle
 *       scattered around the water's edge.</li>
 *   <li>{@code iterations: 1} + {@code scatter_chance: 1} (always run once) -> {@link CountPlacement#of(int)} with 1,
 *       thinned further by a {@link RarityFilter} not present in the Bedrock source. Bedrock also runs this
 *       placement pass "before_surface_pass" (before its surface/sand texturing finalizes), which lets the
 *       terrain conform around the puddle; Java's decoration step runs after surface rules are already baked
 *       in, so stamping one of these into every single chunk reads as far more common in practice than in
 *       Bedrock - hence the added rarity.</li>
 *   <li>{@code x/z uniform [0,16]} (once per chunk, spread across it) -> {@link InSquarePlacement#spread()}.</li>
 *   <li>{@code y = heightmap - 4} -> {@link HeightmapPlacement} on WORLD_SURFACE_WG (offset 0) combined with
 *       {@code groundOffset = -4} in the {@link SingleStructureConfiguration}, which is applied inside
 *       {@link SingleStructureFeature#place} right before the structure is stamped down.</li>
 *   <li>{@code constraints.grounded} + {@code block_intersection.block_allowlist} (the puddle must sit on
 *       sand/red sand) -> {@link SingleStructureConfiguration}'s {@code requireGroundedFloor} +
 *       {@code requiredFloorBlocks} (SAND, RED_SAND), checked against the structure's real post-rotation
 *       footprint (a prior PlacementModifier-based version checked pre-rotation offsets and over-rejected
 *       almost every site).</li>
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
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "structurescatter/oasis_puddle"),
                        Optional.empty(), -4, true, List.of(Blocks.SAND, Blocks.RED_SAND))
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(OASIS_PUDDLE_SCATTER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(OASIS_PUDDLE_SCATTER_KEY),
                List.of(
                        CountPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }
}
