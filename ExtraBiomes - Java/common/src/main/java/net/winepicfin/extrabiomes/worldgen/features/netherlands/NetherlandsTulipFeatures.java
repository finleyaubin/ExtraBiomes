package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * The four tulip colours ported from Bedrock's "extrabiomes:the_netherlands/{orange,pink,red,white}_tulip"
 * ({@code minecraft:structure_template_feature}, each converted structure is a single 1x1x1 flower block -
 * verified directly from the .mcstructure block_palette: minecraft:red_flower with flower_type tulip_orange/
 * tulip_pink/tulip_red/tulip_white) wrapped by "..._floor_feature" ({@code minecraft:vegetation_patch_feature}),
 * plus their "extrabiomes:netherlands_{orange,pink,red,white}_tulip_feature" feature_rules.
 * <p>
 * Since each Bedrock tulip structure is exactly one block, this is ported as {@link Feature#SIMPLE_BLOCK} (placing
 * the matching vanilla tulip block state directly - Blocks.ORANGE_TULIP/PINK_TULIP/RED_TULIP/WHITE_TULIP) rather
 * than as a converted .nbt structure; there is no fidelity loss since a 1x1x1 structure IS just a single block.
 * <p>
 * {@code replaceable_blocks: [minecraft:farmland, minecraft:grass]} -> the shared
 * {@code extrabiomes:netherlands_tulip_replaceable} block tag (data/extrabiomes/tags/blocks/netherlands_tulip_replaceable.json).
 * {@code ground_block: minecraft:grass} -> BlockStateProvider.simple(Blocks.GRASS_BLOCK) (Bedrock's legacy "grass" id).
 * {@code surface: floor} -> CaveSurface.FLOOR. {@code depth: 1-1} -> ConstantInt.of(1). {@code vertical_range: 2} -> 2.
 * {@code vegetation_chance: 1} -> 1.0F. {@code horizontal_radius: 1-1} -> ConstantInt.of(1).
 * {@code extra_edge_column_chance: 0} -> 0.0F (last VegetationPatchConfiguration parameter). No Bedrock field maps to
 * Java's extraBottomBlockChance parameter, so that is 0.0F.
 * <p>
 * SIMPLIFICATION: each colour's feature_rules distribution is an unusual "fixed_grid" x-extent [5,25] (wider than one
 * chunk) combined with a FIXED z coordinate per colour (e.g. orange z=5, pink z=9, red z=1, white z=13) - i.e. Bedrock
 * plants each colour in its own straight row per chunk rather than a true scatter. Vanilla PlacementModifiers have no
 * "fixed single-axis row" primitive, so this is ported as an ordinary per-chunk scatter matching the same iteration
 * count (10): CountPlacement.of(10) + InSquarePlacement.spread() + HeightmapPlacement.onHeightmap(WORLD_SURFACE_WG)
 * (Bedrock's query.above_top_solid) + BiomeFilter.biome(). This preserves the same flower density per chunk while
 * losing the stripped-row visual pattern.
 */
public class NetherlandsTulipFeatures {
    public static final TagKey<Block> TULIP_REPLACEABLE = TagKey.create(Registries.BLOCK, new ResourceLocation(ExtraBiomes.MOD_ID, "netherlands_tulip_replaceable"));

    private static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_TULIP_KEY = key("netherlands_orange_tulip");
    private static final ResourceKey<ConfiguredFeature<?, ?>> PINK_TULIP_KEY = key("netherlands_pink_tulip");
    private static final ResourceKey<ConfiguredFeature<?, ?>> RED_TULIP_KEY = key("netherlands_red_tulip");
    private static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_TULIP_KEY = key("netherlands_white_tulip");

    private static final ResourceKey<PlacedFeature> ORANGE_TULIP_PLACED_KEY = placedKey("netherlands_orange_tulip");
    private static final ResourceKey<PlacedFeature> PINK_TULIP_PLACED_KEY = placedKey("netherlands_pink_tulip");
    private static final ResourceKey<PlacedFeature> RED_TULIP_PLACED_KEY = placedKey("netherlands_red_tulip");
    private static final ResourceKey<PlacedFeature> WHITE_TULIP_PLACED_KEY = placedKey("netherlands_white_tulip");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_TULIP_FLOOR_KEY = key("netherlands_orange_tulip_floor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PINK_TULIP_FLOOR_KEY = key("netherlands_pink_tulip_floor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_TULIP_FLOOR_KEY = key("netherlands_red_tulip_floor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_TULIP_FLOOR_KEY = key("netherlands_white_tulip_floor");

    public static final ResourceKey<PlacedFeature> ORANGE_TULIP_FLOOR_PLACED_KEY = placedKey("netherlands_orange_tulip_floor");
    public static final ResourceKey<PlacedFeature> PINK_TULIP_FLOOR_PLACED_KEY = placedKey("netherlands_pink_tulip_floor");
    public static final ResourceKey<PlacedFeature> RED_TULIP_FLOOR_PLACED_KEY = placedKey("netherlands_red_tulip_floor");
    public static final ResourceKey<PlacedFeature> WHITE_TULIP_FLOOR_PLACED_KEY = placedKey("netherlands_white_tulip_floor");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(ORANGE_TULIP_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.ORANGE_TULIP))));
        context.register(PINK_TULIP_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.PINK_TULIP))));
        context.register(RED_TULIP_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.RED_TULIP))));
        context.register(WHITE_TULIP_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.WHITE_TULIP))));

        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        BlockStateProvider grass = BlockStateProvider.simple(Blocks.GRASS_BLOCK);
        context.register(ORANGE_TULIP_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                TULIP_REPLACEABLE, grass, placedFeatures.getOrThrow(ORANGE_TULIP_PLACED_KEY), CaveSurface.FLOOR,
                ConstantInt.of(1), 0.0F, 2, 1.0F, ConstantInt.of(1), 0.0F)));
        context.register(PINK_TULIP_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                TULIP_REPLACEABLE, grass, placedFeatures.getOrThrow(PINK_TULIP_PLACED_KEY), CaveSurface.FLOOR,
                ConstantInt.of(1), 0.0F, 2, 1.0F, ConstantInt.of(1), 0.0F)));
        context.register(RED_TULIP_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                TULIP_REPLACEABLE, grass, placedFeatures.getOrThrow(RED_TULIP_PLACED_KEY), CaveSurface.FLOOR,
                ConstantInt.of(1), 0.0F, 2, 1.0F, ConstantInt.of(1), 0.0F)));
        context.register(WHITE_TULIP_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                TULIP_REPLACEABLE, grass, placedFeatures.getOrThrow(WHITE_TULIP_PLACED_KEY), CaveSurface.FLOOR,
                ConstantInt.of(1), 0.0F, 2, 1.0F, ConstantInt.of(1), 0.0F)));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        // Inner single-block features: no placement modifiers of their own - the outer vegetation patch feature
        // (registered below) fully controls where each individual tulip block within the patch actually lands.
        context.register(ORANGE_TULIP_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(ORANGE_TULIP_KEY), List.of()));
        context.register(PINK_TULIP_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(PINK_TULIP_KEY), List.of()));
        context.register(RED_TULIP_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(RED_TULIP_KEY), List.of()));
        context.register(WHITE_TULIP_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(WHITE_TULIP_KEY), List.of()));

        // Outer per-chunk scatter: iterations 10 (all four colours) read from
        // feature_rules/the_netherlands/netherlands_{orange,pink,red,white}_tulip_feature.json
        List<net.minecraft.world.level.levelgen.placement.PlacementModifier> scatter = List.of(
                CountPlacement.of(10), InSquarePlacement.spread(), HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome());
        context.register(ORANGE_TULIP_FLOOR_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(ORANGE_TULIP_FLOOR_KEY), scatter));
        context.register(PINK_TULIP_FLOOR_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(PINK_TULIP_FLOOR_KEY), scatter));
        context.register(RED_TULIP_FLOOR_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(RED_TULIP_FLOOR_KEY), scatter));
        context.register(WHITE_TULIP_FLOOR_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(WHITE_TULIP_FLOOR_KEY), scatter));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
