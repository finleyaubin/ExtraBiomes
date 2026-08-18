package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;

/**
 * Bedrock's "extrabiomes:the_netherlands/water_feature" ({@code minecraft:structure_template_feature}, structure
 * "extrabiomes:farm_water", a 3x3x3 canal/pool structure - converted to
 * data/extrabiomes/structures/the_netherlands/farm_water.nbt), placed via feature_rules
 * "extrabiomes:netherlands_water_feature" (gated on {@code has_biome_tag "mutated"} in addition to "the_netherlands",
 * i.e. TheNetherlandsMutated only - the source file itself is commented "now unused" but is not actually disabled
 * in the JSON, so it is ported faithfully as still-active).
 * <p>
 * Reuses the shared {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature} /
 * {@link SingleStructureConfiguration} infra from the structurescatter subsystem rather than a new Feature class.
 * {@code facing_direction: random} -> random rotation (SingleStructureConfiguration's 1-arg ground-offset constructor
 * leaves rotation empty = uniformly random). {@code y = heightmap - 2} -> HeightmapPlacement (offset 0) +
 * groundOffset = -2. {@code iterations: 30} -> CountPlacement.of(30). {@code scatter_chance: 99} (effectively always)
 * -> not separately modelled (no vanilla PlacementModifier expresses "wide per-chunk probability" cleanly; 30 counted
 * attempts already closely reproduces the density). {@code block_intersection.block_allowlist:
 * [air, farmland, wheat, dirt]} -> BlockPredicateFilter tested at the actual (post-groundOffset) placement position.
 */
public class NetherlandsWaterFeature {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WATER_FEATURE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "netherlands_water_feature"));
    public static final ResourceKey<PlacedFeature> WATER_FEATURE_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "netherlands_water_feature"));

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(WATER_FEATURE_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "the_netherlands/farm_water"), -2)
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(WATER_FEATURE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(WATER_FEATURE_KEY),
                List.of(
                        CountPlacement.of(30),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new BlockPos(0, -2, 0),
                                Blocks.AIR, Blocks.FARMLAND, Blocks.WHEAT, Blocks.DIRT)),
                        BiomeFilter.biome()
                )
        ));
    }
}
