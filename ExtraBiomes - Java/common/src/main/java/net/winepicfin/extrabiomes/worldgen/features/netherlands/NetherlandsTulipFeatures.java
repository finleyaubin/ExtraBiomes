package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.placement.ChunkOriginSnap;
import net.winepicfin.extrabiomes.worldgen.placement.InBiomeChunkSample;

import java.util.List;

public class NetherlandsTulipFeatures {
    public static final TagKey<Block> TULIP_REPLACEABLE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "netherlands_tulip_replaceable"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> TULIP_FIELD_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "netherlands_tulip_field"));
    public static final ResourceKey<PlacedFeature> TULIP_FIELD_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "netherlands_tulip_field"));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(TULIP_FIELD_KEY, new ConfiguredFeature<>(NetherlandsWheatFeatures.TULIP_FIELD_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    // The feature checks the biome per column; the sample/snap pair only lets BiomeFilter test a column in the biome instead of the chunk corner.
    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(TULIP_FIELD_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(TULIP_FIELD_KEY),
                List.of(InBiomeChunkSample.INSTANCE, BiomeFilter.biome(), ChunkOriginSnap.INSTANCE, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG))));
    }
}
