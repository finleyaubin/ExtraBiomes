package net.winepicfin.extrabiomes.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.worldgen.features.ore.ModOrePlacement;
import net.winepicfin.extrabiomes.worldgen.features.palm.PalmTreeFeatures;

import java.util.List;

public class ModPlacedFeatures{
    public static final ResourceKey<PlacedFeature> MYSTIC_PLACED_KEY = createKey("mystic_placed");
    public static final ResourceKey<PlacedFeature> PALM_PLACED_KEY = createKey("palm_placed");
    public static final ResourceKey<PlacedFeature> GRAND_OASIS_PALM_PLACED_KEY = createKey("grand_oasis_palm_placed");
    public static final ResourceKey<PlacedFeature> CHARRED_PLACED_KEY = createKey("charred_placed");
    public static final ResourceKey<PlacedFeature> LUSH_GRASS_PLACED_KEY = createKey("lush_grass_placed");
    public static final ResourceKey<PlacedFeature> SKY_PLACED_KEY = createKey("sky_placed");
    // PlacementUtils.countExtra(count, extraChance, extraCount): only the middle argument is a float - passing a float as count silently picks the wrong overload, so keep the literals typed as written below.
    public static void bootstrap(BootstapContext<PlacedFeature>context){
        HolderGetter<ConfiguredFeature<?, ?>>configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        // Bedrock's mystic_tree_feature.json allows the structure to intersect minecraft:water and
        // extrabiomes:goo (unburied is the only ground constraint) so trees can grow straight out of
        // the swamp's goo pools - vanilla's treePlacement() hardcodes max_water_depth 0, which would
        // block that entirely, so this mirrors it with a shallow allowance instead.
        register(context, MYSTIC_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.MYSTIC_SELECT_KEY), treePlacementWithWaterDepth(PlacementUtils.countExtra(3, 0.1f,2), ModBlocks.MYSTIC_SAPLING.get(), 3));
        // Palm trees anchor on their true base-log column, so vanilla treePlacement's own SurfaceWaterDepthFilter already keeps them off water/uneven ground - no extra filter needed here.
        register(context, PALM_PLACED_KEY,configuredFeatures.getOrThrow(PalmTreeFeatures.SELECT_PALM_KEY), VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.5f,2), ModBlocks.PALM_SAPLING.get()));
        // Own placed feature so density can be tuned separately from PALM_PLACED_KEY; thinned after playtesting found the original count read as forest-dense rather than a scattered oasis.
        register(context, GRAND_OASIS_PALM_PLACED_KEY,configuredFeatures.getOrThrow(PalmTreeFeatures.SELECT_PALM_KEY), VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.5f,1), ModBlocks.PALM_SAPLING.get()));
        register(context, SKY_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.SKY_KEY), VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f,2), ModBlocks.SKY_SAPLING.get()));
        register(context, CHARRED_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.CHARRED_KEY), VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f,2), Blocks.OAK_SAPLING));
        register(context, LUSH_GRASS_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.LUSH_GRASS_KEY), ModOrePlacement.commonOrePlacement(15, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE)));
    }

    /** Same shape as vanilla's {@link VegetationPlacements#treePlacement}, but with a configurable max water/goo depth instead of the hardcoded 0. */
    private static List<PlacementModifier> treePlacementWithWaterDepth(PlacementModifier countModifier, Block sapling, int maxWaterDepth) {
        return List.of(countModifier, InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(maxWaterDepth),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR), BiomeFilter.biome(),
                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(sapling.defaultBlockState(), BlockPos.ZERO)));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
