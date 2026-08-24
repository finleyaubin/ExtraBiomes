package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * Bedrock's windmill structure ("ExtraBiomes - Bedrock/packs/BP/features/windmill.json",
 * {@code minecraft:structure_template_feature}, structure "extrabiomes:windmill",
 * {@code facing_direction: "south"} - a FIXED direction, converted to
 * data/extrabiomes/structures/the_netherlands/windmill.nbt), placed by TWO separate feature_rules with different
 * odds sharing the same underlying feature identifier "extrabiomes:windmill":
 * <ul>
 *   <li>feature_rules/the_netherlands/netherlands_windmill_feature.json - biome_tag "the_netherlands", scatter_chance
 *       5/50 (10%), iterations 1 -> {@link #WINDMILL_NETHERLANDS_PLACED_KEY}. Deliberately detuned from that 10%
 *       Bedrock rate to RarityFilter.onAverageOnceEvery(30) (~3.3%) - felt too frequent in-game.</li>
 *   <li>feature_rules/windmill_feature.json (repo root, NOT under the_netherlands/) - biome_tag "plains", scatter_chance
 *       1/50 (2%), iterations 1 -> {@link #WINDMILL_PLAINS_PLACED_KEY}, RarityFilter.onAverageOnceEvery(50). This is
 *       the SAME windmill feature Moorlands.java also needs (see wiringInstructions) since Bedrock's plains
 *       feature_rule additionally matches Moorlands via its "plains" biome tag.</li>
 * </ul>
 * Both share one {@link #WINDMILL_KEY} ConfiguredFeature (reusing the structurescatter subsystem's
 * SingleStructureFeature/SingleStructureConfiguration infra - see that subsystem's own summary for the general
 * mapping rules) with a FIXED rotation (Bedrock's facing_direction "south" is not random, so
 * {@code Optional.of(Rotation.NONE)} is used, treating the structure's captured orientation as already "south").
 * {@code y: query.above_top_solid(...)} - the top SOLID block, ignoring water/liquid on top of it - ->
 * HeightmapPlacement.onHeightmap(OCEAN_FLOOR_WG) (NOT WORLD_SURFACE_WG, which counts water as non-air and would
 * place the windmill floating on the water surface instead of on the ground/seafloor below it), groundOffset 0.
 * {@code x/z uniform[0,16]} -> InSquarePlacement.spread(). {@link SurfaceWaterDepthFilter#forMaxDepth(int)} with
 * maxDepth 0 is an ADDITION beyond the literal Bedrock port (Bedrock has no equivalent check here) - it rejects any
 * column with water above the OCEAN_FLOOR_WG surface outright, so windmills never generate on/in water at all,
 * rather than merely resting on the seafloor beneath it.
 * {@code block_intersection.block_allowlist: [air, wheat, red_flower, orange_tulip, pink_tulip, white_tulip, red_tulip]}
 * -> BlockPredicateFilter tested at the placement origin against the matching vanilla blocks (air, wheat, and all
 * four tulip colours - Bedrock's legacy "red_flower" base id plus its four tulip variants collapse onto the same
 * four Java tulip blocks already used by {@link NetherlandsTulipFeatures}).
 */
public class NetherlandsWindmillFeature {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WINDMILL_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "windmill"));
    public static final ResourceKey<PlacedFeature> WINDMILL_NETHERLANDS_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "windmill_netherlands"));
    public static final ResourceKey<PlacedFeature> WINDMILL_PLAINS_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "windmill_plains"));

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(WINDMILL_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                // requireGroundedFloor requires solid ground under the whole footprint, since a single-column HeightmapPlacement only checks the origin and this structure is wide enough to hang over a ledge otherwise.
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "the_netherlands/windmill"),
                        Optional.of(Rotation.NONE), 0, 0.0F, true)
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        BlockPredicateFilter allowedGround = BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(BlockPos.ZERO,
                Blocks.AIR, Blocks.WHEAT, Blocks.ORANGE_TULIP, Blocks.PINK_TULIP, Blocks.RED_TULIP, Blocks.WHITE_TULIP));

        context.register(WINDMILL_NETHERLANDS_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(WINDMILL_KEY),
                List.of(
                        // Tuned down from the Bedrock-matching onAverageOnceEvery(10) - that felt too frequent in practice.
                        RarityFilter.onAverageOnceEvery(30),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        allowedGround,
                        BiomeFilter.biome()
                )
        ));
        context.register(WINDMILL_PLAINS_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(WINDMILL_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(50),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        allowedGround,
                        BiomeFilter.biome()
                )
        ));
    }
}
