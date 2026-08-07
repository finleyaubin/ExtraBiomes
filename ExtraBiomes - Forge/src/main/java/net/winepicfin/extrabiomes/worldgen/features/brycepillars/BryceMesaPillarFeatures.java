package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * ConfiguredFeature/PlacedFeature bootstrap for {@link BrycePillarsFeature}, one entry per
 * distinct clay/hard-clay material combination found across the Bedrock biomes with
 * "bryce_pillars": true on their "minecraft:mesa" surface builder:
 * <pre>
 * cold_mesa_bryce.biome.json / lush_mesa_bryce.biome.json:
 *   hard_clay_material: hardened_clay, clay_material: stained_hardened_clay -> TERRACOTTA_KEY
 * desert_bryce.biome.json:
 *   hard_clay_material: sand,          clay_material: sand                 -> SAND_KEY
 * shattered_swamp.biome.json / shattered_taiga_spikes.biome.json:
 *   hard_clay_material: stone,         clay_material: tuff                 -> TUFF_KEY
 * jungle_pillars.biome.json:
 *   hard_clay_material: stone,         clay_material: stone                -> STONE_KEY
 * </pre>
 * Bedrock's "stained_hardened_clay" has no single Java block equivalent (it's Bedrock's whole
 * colour-banded terracotta family) - TERRACOTTA_KEY approximates it with a weighted palette of
 * the same terracotta colours vanilla's own {@code SurfaceRules.bandlands()} cycles through,
 * rather than one flat colour.
 */
public class BryceMesaPillarFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_KEY = registerKey("bryce_pillars_terracotta");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAND_KEY = registerKey("bryce_pillars_sand");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TUFF_KEY = registerKey("bryce_pillars_tuff");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_KEY = registerKey("bryce_pillars_stone");

    public static final ResourceKey<PlacedFeature> TERRACOTTA_PLACED_KEY = placedKey("bryce_pillars_terracotta");
    public static final ResourceKey<PlacedFeature> SAND_PLACED_KEY = placedKey("bryce_pillars_sand");
    public static final ResourceKey<PlacedFeature> TUFF_PLACED_KEY = placedKey("bryce_pillars_tuff");
    public static final ResourceKey<PlacedFeature> STONE_PLACED_KEY = placedKey("bryce_pillars_stone");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        BlockStateProvider terracottaPalette = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 3)
                .add(Blocks.RED_TERRACOTTA.defaultBlockState(), 2)
                .add(Blocks.YELLOW_TERRACOTTA.defaultBlockState(), 2)
                .add(Blocks.BROWN_TERRACOTTA.defaultBlockState(), 1)
                .add(Blocks.WHITE_TERRACOTTA.defaultBlockState(), 1)
                .add(Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(), 1));

        context.register(TERRACOTTA_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(BlockStateProvider.simple(Blocks.TERRACOTTA), terracottaPalette)));
        context.register(SAND_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(BlockStateProvider.simple(Blocks.SAND), BlockStateProvider.simple(Blocks.SAND))));
        context.register(TUFF_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(BlockStateProvider.simple(Blocks.STONE), BlockStateProvider.simple(Blocks.TUFF))));
        context.register(STONE_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(BlockStateProvider.simple(Blocks.STONE), BlockStateProvider.simple(Blocks.STONE))));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        List<net.minecraft.world.level.levelgen.placement.PlacementModifier> modifiers = List.of(BiomeFilter.biome());

        context.register(TERRACOTTA_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(TERRACOTTA_KEY), modifiers));
        context.register(SAND_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SAND_KEY), modifiers));
        context.register(TUFF_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(TUFF_KEY), modifiers));
        context.register(STONE_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(STONE_KEY), modifiers));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
}
