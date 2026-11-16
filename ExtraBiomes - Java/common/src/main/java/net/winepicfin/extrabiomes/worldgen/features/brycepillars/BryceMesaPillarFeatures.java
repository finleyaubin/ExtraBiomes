package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
 *   hard_clay_material: sand,          clay_material: sand                 -> SAND_KEY (sandstone-bodied on Java)
 * shattered_swamp.biome.json / shattered_taiga_spikes.biome.json:
 *   hard_clay_material: stone,         clay_material: tuff                 -> TUFF_KEY
 * jungle_pillars.biome.json:
 *   hard_clay_material: stone,         clay_material: stone                -> STONE_KEY
 * </pre>
 * Bedrock's "stained_hardened_clay" has no single Java block equivalent (it's Bedrock's whole
 * colour-banded terracotta family) - TERRACOTTA_KEY approximates it with the same terracotta
 * colours vanilla's own {@code SurfaceRules.bandlands()} cycles through. Each entry below is a
 * background + streak-palette RECIPE - {@link BrycePillarsFeature} builds the actual 192-layer,
 * world-seed-derived array from that recipe (per the wiki's own description of vanilla's
 * badlands banding), rather than these being a fixed colour list or a per-block random pick.
 * That's what keeps a pillar's colouring consistent with the real terracotta banding at whatever
 * height it's standing at, instead of jumping between unrelated colours as height changes. The
 * same mechanism now also backs TUFF_KEY and STONE_KEY (previously flat single materials) for the
 * same reason.
 */
public class BryceMesaPillarFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_KEY = registerKey("bryce_pillars_terracotta");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAND_KEY = registerKey("bryce_pillars_sand");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TUFF_KEY = registerKey("bryce_pillars_tuff");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_KEY = registerKey("bryce_pillars_stone");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SHORT_MUD_STALAGMITE_KEY = registerKey("desert_bryce_short_mud_stalagmite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_MUD_STALAGMITE_KEY = registerKey("desert_bryce_tall_mud_stalagmite");
    public static final ResourceKey<PlacedFeature> SHORT_MUD_STALAGMITE_PLACED_KEY = placedKey("desert_bryce_short_mud_stalagmite");
    public static final ResourceKey<PlacedFeature> TALL_MUD_STALAGMITE_PLACED_KEY = placedKey("desert_bryce_tall_mud_stalagmite");

    // Most attempts miss: only the ones landing on a packed mud patch pass the placement filter.
    private static final int SHORT_MUD_STALAGMITE_ATTEMPTS_PER_CHUNK = 16;
    private static final int TALL_MUD_STALAGMITE_ATTEMPTS_PER_CHUNK = 8;

    public static final ResourceKey<PlacedFeature> TERRACOTTA_PLACED_KEY = placedKey("bryce_pillars_terracotta");
    public static final ResourceKey<PlacedFeature> SAND_PLACED_KEY = placedKey("bryce_pillars_sand");
    public static final ResourceKey<PlacedFeature> TUFF_PLACED_KEY = placedKey("bryce_pillars_tuff");
    public static final ResourceKey<PlacedFeature> STONE_PLACED_KEY = placedKey("bryce_pillars_stone");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        registerMudStalagmites(context);
        // streakPalette supplies the occasional colour bands scattered through the plain TERRACOTTA background; see generateBands() in BrycePillarsFeature for the distribution.
        List<BlockState> terracottaStreaks = List.of(
                Blocks.ORANGE_TERRACOTTA.defaultBlockState(),
                Blocks.YELLOW_TERRACOTTA.defaultBlockState(),
                Blocks.RED_TERRACOTTA.defaultBlockState(),
                Blocks.WHITE_TERRACOTTA.defaultBlockState(),
                Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(),
                Blocks.BROWN_TERRACOTTA.defaultBlockState()
        );

        // Per-material geometry, not just per-material colour: real Bryce Canyon-style erosion looks different in soft vs. hard rock, so each entry below tunes height/thickness/edge-roughness to its own material rather than all four sharing BrycePillarsConfiguration's one-size-fits-all defaults. maxRadius is now a fin's thickness basis (BrycePillarsFeature halves it) and erosionStrength is edge wobble, not the old cone's radial noise.
        // Terracotta is the namesake Bryce Canyon material - kept at the original, moderate tuning every other entry is now tuned relative to.
        context.register(TERRACOTTA_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(Blocks.TERRACOTTA.defaultBlockState(), terracottaStreaks, 10, 40, 0.97F, 6, 1.5F, java.util.Optional.empty())));
        // Loose sand fins collapse on the first block update, so the body is banded smooth sandstone and only the weathered cap is sand.
        // Streaks cover most of the band array, so repeating the body block in the palette is what keeps accent bands occasional instead of every few layers.
        BlockState smoothSandstone = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
        List<BlockState> sandstoneStreaks = List.of(
                smoothSandstone, smoothSandstone, smoothSandstone, smoothSandstone,
                Blocks.SANDSTONE.defaultBlockState(),
                Blocks.DRIPSTONE_BLOCK.defaultBlockState(),
                Blocks.WHITE_TERRACOTTA.defaultBlockState()
        );
        context.register(SAND_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(smoothSandstone, sandstoneStreaks, 10, 34, 0.96F, 6, 1.6F, java.util.Optional.of(Blocks.SAND.defaultBlockState()))));
        // Tuff is hard rock - taller, thinner fins with a lighter edge wobble so they stay sharp instead of crumbling; this is the material the shattered_swamp/shattered_taiga_spikes reference screenshot is aiming for.
        context.register(TUFF_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(Blocks.STONE.defaultBlockState(), List.of(Blocks.TUFF.defaultBlockState()), 12, 34, 0.97F, 4, 0.9F, java.util.Optional.empty())));
        // Bedrock's jungle_pillars config is flat stone/stone - an empty streak palette collapses the 192-layer array to one repeated colour, still running through the same banding code path.
        // Grass-capped (see BrycePillarsConfiguration#capMaterial) so JunglePillars.java's existing addJungleTrees/addJungleGrass calls have valid ground to land jungle vegetation on top of these formations, not just on the flat ground between them; kept a bit thicker than tuff so those caps have enough room for trees.
        context.register(STONE_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(Blocks.STONE.defaultBlockState(), List.of(), 10, 28, 0.97F, 6, 1.0F, java.util.Optional.of(Blocks.GRASS_BLOCK.defaultBlockState()))));
    }

    private static void registerMudStalagmites(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(SHORT_MUD_STALAGMITE_KEY, stalagmite(
                BlockColumnConfiguration.layer(UniformInt.of(0, 1), dripstone(DripstoneThickness.FRUSTUM)),
                BlockColumnConfiguration.layer(ConstantInt.of(1), dripstone(DripstoneThickness.TIP))));
        context.register(TALL_MUD_STALAGMITE_KEY, stalagmite(
                BlockColumnConfiguration.layer(ConstantInt.of(1), dripstone(DripstoneThickness.BASE)),
                BlockColumnConfiguration.layer(UniformInt.of(0, 2), dripstone(DripstoneThickness.MIDDLE)),
                BlockColumnConfiguration.layer(ConstantInt.of(1), dripstone(DripstoneThickness.FRUSTUM)),
                BlockColumnConfiguration.layer(ConstantInt.of(1), dripstone(DripstoneThickness.TIP))));
    }

    private static ConfiguredFeature<?, ?> stalagmite(BlockColumnConfiguration.Layer... bottomToTopLayers) {
        return new ConfiguredFeature<>(Feature.BLOCK_COLUMN,
                new BlockColumnConfiguration(List.of(bottomToTopLayers), Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));
    }

    private static BlockStateProvider dripstone(DripstoneThickness thickness) {
        return BlockStateProvider.simple(Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.UP)
                .setValue(PointedDripstoneBlock.THICKNESS, thickness));
    }

    private static List<PlacementModifier> onPackedMud(int attemptsPerChunk) {
        return List.of(
                CountPlacement.of(attemptsPerChunk),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), List.of(Blocks.PACKED_MUD))),
                BiomeFilter.biome());
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(SHORT_MUD_STALAGMITE_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SHORT_MUD_STALAGMITE_KEY), onPackedMud(SHORT_MUD_STALAGMITE_ATTEMPTS_PER_CHUNK)));
        context.register(TALL_MUD_STALAGMITE_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(TALL_MUD_STALAGMITE_KEY), onPackedMud(TALL_MUD_STALAGMITE_ATTEMPTS_PER_CHUNK)));
        List<PlacementModifier> modifiers = List.of(BiomeFilter.biome());

        context.register(TERRACOTTA_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(TERRACOTTA_KEY), modifiers));
        context.register(SAND_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SAND_KEY), modifiers));
        context.register(TUFF_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(TUFF_KEY), modifiers));
        context.register(STONE_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(STONE_KEY), modifiers));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
}
