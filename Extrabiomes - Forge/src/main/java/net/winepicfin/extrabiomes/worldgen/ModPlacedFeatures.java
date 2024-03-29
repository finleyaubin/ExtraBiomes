package net.winepicfin.extrabiomes.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.worldgen.features.ore.ModOrePlacement;

import java.util.List;

public class ModPlacedFeatures{
    public static final ResourceKey<PlacedFeature> MYSTIC_PLACED_KEY = createKey("mystic_placed");
    public static final ResourceKey<PlacedFeature> PALM_PLACED_KEY = createKey("palm_placed");
    public static final ResourceKey<PlacedFeature> CHARRED_PLACED_KEY = createKey("charred_placed");
    public static final ResourceKey<PlacedFeature> LUSH_GRASS_PLACED_KEY = createKey("lush_grass_placed");
    public static void bootstrap(BootstapContext<PlacedFeature>context){
        HolderGetter<ConfiguredFeature<?, ?>>configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        register(context, MYSTIC_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.MYSTIC_KEY), VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f,2), ModBlocks.MYSTIC_SAPLING.get()));//first int div float must = an int not a float
        register(context, PALM_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.PALM_KEY), VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.5f,2), ModBlocks.PALM_SAPLING.get()));//first int div float must = an int not a float
        register(context, CHARRED_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.CHARRED_KEY), VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f,2), Blocks.OAK_SAPLING));//first int div float must = an int not a float
        register(context, LUSH_GRASS_PLACED_KEY,configuredFeatures.getOrThrow(ModConfigureFeatures.LUSH_GRASS_KEY), ModOrePlacement.commonOrePlacement(15, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE)));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}
