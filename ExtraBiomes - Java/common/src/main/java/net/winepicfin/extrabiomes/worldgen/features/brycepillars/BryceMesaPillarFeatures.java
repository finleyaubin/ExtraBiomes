package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
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
 *   hard_clay_material: sand,          clay_material: sand                 -> SAND_KEY
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

    public static final ResourceKey<PlacedFeature> TERRACOTTA_PLACED_KEY = placedKey("bryce_pillars_terracotta");
    public static final ResourceKey<PlacedFeature> SAND_PLACED_KEY = placedKey("bryce_pillars_sand");
    public static final ResourceKey<PlacedFeature> TUFF_PLACED_KEY = placedKey("bryce_pillars_tuff");
    public static final ResourceKey<PlacedFeature> STONE_PLACED_KEY = placedKey("bryce_pillars_stone");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
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
        // Sand can't hold a tall thin wall without slumping - shorter cap, a thicker base, and heavier edge wobble so the outline reads as soft/rounded rather than a sandstone-sharp fin.
        context.register(SAND_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(Blocks.SAND.defaultBlockState(), List.of(), 6, 22, 0.96F, 8, 2.2F, java.util.Optional.empty())));
        // Tuff is hard rock - taller, thinner fins with a lighter edge wobble so they stay sharp instead of crumbling; this is the material the shattered_swamp/shattered_taiga_spikes reference screenshot is aiming for.
        context.register(TUFF_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(Blocks.STONE.defaultBlockState(), List.of(Blocks.TUFF.defaultBlockState()), 12, 34, 0.97F, 4, 0.9F, java.util.Optional.empty())));
        // Bedrock's jungle_pillars config is flat stone/stone - an empty streak palette collapses the 192-layer array to one repeated colour, still running through the same banding code path.
        // Grass-capped (see BrycePillarsConfiguration#capMaterial) so JunglePillars.java's existing addJungleTrees/addJungleGrass calls have valid ground to land jungle vegetation on top of these formations, not just on the flat ground between them; kept a bit thicker than tuff so those caps have enough room for trees.
        context.register(STONE_KEY, new ConfiguredFeature<>(ModBrycePillarsFeatures.BRYCE_PILLARS.get(),
                new BrycePillarsConfiguration(Blocks.STONE.defaultBlockState(), List.of(), 10, 28, 0.97F, 6, 1.0F, java.util.Optional.of(Blocks.GRASS_BLOCK.defaultBlockState()))));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
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
