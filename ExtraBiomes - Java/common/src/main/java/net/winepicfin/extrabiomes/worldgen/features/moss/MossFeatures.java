package net.winepicfin.extrabiomes.worldgen.features.moss;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Port of the Bedrock "moss" feature subsystem used by tropical_island's growth chain:
 * <pre>
 * features/moss/custom_moss_select_feature.json (minecraft:aggregate_feature)
 *   -> extrabiomes:moorland/moorlands_scatter_tall_grass_feature (minecraft:scatter_feature,
 *      iterations 30, x/z gaussian [-8,8], y gaussian [-4,4], places minecraft:tall_grass_feature)
 *   -> extrabiomes:moss/scatter_carpet_feature (minecraft:scatter_feature, iterations 10,
 *      x=y=z=0, places extrabiomes:moss/carpet_patch)
 *   -> minecraft:jungle_bush_feature (vanilla, reused as-is)
 * features/moss/carpet_patch.json (minecraft:single_block_feature -> minecraft:moss_carpet)
 * </pre>
 * <p>
 * Mapping notes / simplifications:
 * <ul>
 *   <li>Bedrock's {@code aggregate_feature} runs all of its sub-features unconditionally and has no
 *       direct Java Edition equivalent (there is no vanilla "run several unrelated features every
 *       time" feature type). Per project convention this is NOT reproduced as a single nested
 *       Java feature; instead each of the three aggregate members is registered here as its own
 *       independent top-level {@link PlacedFeature}, and the consuming biome is expected to add all
 *       three via separate {@code biomeBuilder.addFeature(...)} calls at the same decoration step
 *       (see {@link #JUNGLE_BUSH_PLACED_KEY} javadoc for the wiring). This means the three will no
 *       longer be perfectly co-located per placement attempt the way the Bedrock aggregate was, but
 *       each individually behaves identically to its Bedrock counterpart.</li>
 *   <li>{@code moorlands_scatter_tall_grass_feature.json}'s {@code minecraft:scatter_feature}
 *       (iterations + gaussian x/y/z extents wrapping a places_feature) maps cleanly onto vanilla's
 *       own {@link Feature#RANDOM_PATCH} + {@link RandomPatchConfiguration} (tries/xz_spread/y_spread
 *       wrapping an inner placed feature) - this is in fact exactly how vanilla itself implements its
 *       own grass-patch decorations (see {@code data/minecraft/worldgen/configured_feature/patch_grass.json}).
 *       iterations 30 -> tries 30; gaussian extents [-8,8]/[-4,4] -> xz_spread 8 / y_spread 3
 *       (vanilla's RandomPatchFeature already applies a normal-ish distribution internally, same as
 *       Bedrock's "gaussian" scatter). Bedrock's {@code minecraft:tall_grass_feature} here places the
 *       single-block short grass tuft (Bedrock has a separate
 *       {@code moorlands_scatter_double_tall_grass_feature.json} for the true double-tall variant),
 *       so the inner placed feature places {@link Blocks#SHORT_GRASS} (1.20.1's short grass block) via
 *       {@link Feature#SIMPLE_BLOCK}, guarded by a {@code minecraft:air} {@link BlockPredicateFilter}
 *       - again mirroring vanilla's own {@code patch_grass.json} inner feature exactly.</li>
 *   <li>{@code moss/scatter_carpet_feature.json} has iterations 10 but x=y=z=0 (no spread at all),
 *       meaning every one of the 10 attempts lands on the exact same block - functionally identical
 *       to placing the carpet once. This is simplified to a single {@code minecraft:air}
 *       {@link BlockPredicateFilter}-guarded placement of {@link Blocks#MOSS_CARPET} (from
 *       {@code carpet_patch.json}'s {@code minecraft:single_block_feature}) rather than looping the
 *       identical placement 10 times. Bedrock's {@code enforce_survivability_rules}/
 *       {@code enforce_placement_rules} (the carpet needs a solid block below it) has no separate
 *       Java placement-modifier equivalent, but is naturally enforced by the engine anyway: setting
 *       a {@code moss_carpet} block triggers a neighbor shape update
 *       that pops it back off immediately if unsupported, same end result.</li>
 *   <li>{@code minecraft:jungle_bush_feature} is reused as literally vanilla's own feature - no new
 *       Java feature is registered for it. {@link #JUNGLE_BUSH_PLACED_KEY} is just a convenience
 *       {@link ResourceKey} pointing at vanilla's existing
 *       {@code data/minecraft/worldgen/placed_feature/jungle_bush.json} entry.</li>
 *   <li>The outer {@code custom_moss_patch_feature.json} ({@code minecraft:vegetation_patch_feature},
 *       which scans for floor and converts the ground to {@code moss_block} before invoking the
 *       aggregate above as its {@code vegetation_feature}) and the
 *       {@code tropical_growth_1_feature.json} feature_rule that invokes it are both out of scope for
 *       this subsystem - only the aggregate (moss/tall-grass/jungle-bush decoration) described above
 *       is ported here, per this port's task scope. The three placed features below therefore each
 *       carry their own standalone surface-placement logic (heightmap + horizontal spread) rather
 *       than relying on a vegetation-patch ground scan.</li>
 * </ul>
 */
public class MossFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_CARPET_KEY = configuredKey("moss_carpet");

    /**
     * extrabiomes:moss/moss_carpet_scatter - places a single {@link Blocks#MOSS_CARPET} on top of
     * whatever block the heightmap finds, guarded by an air check. Register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MossFeatures.MOSS_CARPET_SCATTER_PLACED_KEY)}.
     */
    public static final ResourceKey<PlacedFeature> MOSS_CARPET_SCATTER_PLACED_KEY = placedKey("moss_carpet_scatter");

    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_GRASS_KEY = configuredKey("tall_grass");
    /** Inner air-guarded placement of a single {@link Blocks#SHORT_GRASS} block - not a top-level decoration by itself. */
    public static final ResourceKey<PlacedFeature> TALL_GRASS_INNER_PLACED_KEY = placedKey("tall_grass_inner");

    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_GRASS_PATCH_KEY = configuredKey("tall_grass_patch");

    /**
     * extrabiomes:moss/tall_grass_scatter - the moorland-style tall-grass scatter (30 tries,
     * xz_spread 8, y_spread 3) reused by this subsystem. Register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MossFeatures.TALL_GRASS_SCATTER_PLACED_KEY)}.
     */
    public static final ResourceKey<PlacedFeature> TALL_GRASS_SCATTER_PLACED_KEY = placedKey("tall_grass_scatter");

    /**
     * Vanilla's own {@code minecraft:jungle_bush} placed feature - reused directly rather than
     * re-registered. Register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MossFeatures.JUNGLE_BUSH_PLACED_KEY)}
     * (the biome-wiring pass's {@code context.lookup(Registries.PLACED_FEATURE)} will resolve this
     * key against vanilla's registered entry with no extra work needed here).
     */
    public static final ResourceKey<PlacedFeature> JUNGLE_BUSH_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("minecraft", "jungle_bush"));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        context.register(MOSS_CARPET_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.MOSS_CARPET.defaultBlockState()))));

        context.register(TALL_GRASS_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS.defaultBlockState()))));

        Holder<PlacedFeature> tallGrassInner = placedFeatures.getOrThrow(TALL_GRASS_INNER_PLACED_KEY);
        context.register(TALL_GRASS_PATCH_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(30, 8, 3, tallGrassInner)));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Bedrock's source feature scatters 10 attempts with 0 spread, which always lands on the same block; collapsed to a single placement.
        context.register(MOSS_CARPET_SCATTER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(MOSS_CARPET_KEY),
                List.of(
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        context.register(TALL_GRASS_INNER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(TALL_GRASS_KEY),
                List.<PlacementModifier>of(BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)))
        ));

        context.register(TALL_GRASS_SCATTER_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(TALL_GRASS_PATCH_KEY),
                List.of(
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "moss/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "moss/" + name));
    }
}
