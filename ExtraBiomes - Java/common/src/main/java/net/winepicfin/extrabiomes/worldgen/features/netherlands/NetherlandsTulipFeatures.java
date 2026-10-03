package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

public class NetherlandsTulipFeatures {
    public static final TagKey<Block> TULIP_REPLACEABLE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "netherlands_tulip_replaceable"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> TULIP_FIELD_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "netherlands_tulip_field"));
    public static final ResourceKey<PlacedFeature> TULIP_FIELD_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "netherlands_tulip_field"));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(TULIP_FIELD_KEY, new ConfiguredFeature<>(NetherlandsWheatFeatures.TULIP_FIELD_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    // No BiomeFilter: the feature checks the biome per column, as a single chunk-corner sample would skip chunks straddling a border.
    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(TULIP_FIELD_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(TULIP_FIELD_KEY),
                List.of(HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG))));
    }
}
