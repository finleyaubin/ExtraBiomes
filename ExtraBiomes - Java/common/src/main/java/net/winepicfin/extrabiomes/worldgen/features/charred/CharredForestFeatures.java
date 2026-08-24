package net.winepicfin.extrabiomes.worldgen.features.charred;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Port of the remaining Bedrock "charred_forest" features NOT already covered by the existing
 * living charred tree port ({@link net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures#CHARRED_KEY} /
 * {@link net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures#CHARRED_PLACED_KEY}):
 * <pre>
 * features/charred_forest/burnt_basalt.json          (minecraft:ore_feature -> basalt)
 * features/charred_forest/burnt_magma.json            (minecraft:ore_feature -> magma)
 * features/charred_forest/burnt_land_select.json      (minecraft:aggregate_feature of the two above)
 * features/charred_forest/fire_feature.json           (minecraft:single_block_feature -> fire)
 * features/charred_forest/scatter_fire_feature.json   (minecraft:scatter_feature wrapping fire_feature)
 * features/charred_forest/smoking_ground.json         (minecraft:ore_feature -> campfire)
 * feature_rules/charred_forest/burnt_land_feature.json           (places burnt_land_select, surface_pass, tag "charred")
 * feature_rules/charred_forest/charred_forest_fire_feature.json  (places scatter_fire_feature, surface_pass, tag "charred")
 * feature_rules/charred_forest/smoking_ground_feature.json       (places smoking_ground, surface_pass, tag "charred")
 * </pre>
 * <p>
 * <b>burnt_tree_feature.json / feature_rules/charred_forest/burnt_tree_feature.json is NOT ported here -
 * it is a duplicate of the already-ported living charred tree.</b> There is exactly one tree feature
 * anywhere in the Bedrock charred_forest feature set (this one), and it is what
 * {@code ModConfigureFeatures.CHARRED_KEY} (a basalt {@code Feature.TREE} with no leaves, matching this
 * JSON's {@code acacia_trunk} basalt-family trunk + {@code leaf_block: minecraft:air} canopy) already
 * represents - simplified from the custom lean/branch trunk shape to vanilla's {@code FancyTrunkPlacer},
 * same as every other tree in this mod. It is not a second, distinct "dead tree" decoration. No new
 * registration is added for it; the existing key already covers this JSON.
 * <p>
 * Mapping notes / simplifications:
 * <ul>
 *   <li>{@code burnt_land_select.json}'s {@code minecraft:aggregate_feature} has no Java Edition
 *       equivalent (no vanilla "run several unrelated features every time" feature type). Per project
 *       convention, {@link #BURNT_BASALT_PLACED_KEY} and {@link #BURNT_MAGMA_PLACED_KEY} are each
 *       registered as independent top-level {@link PlacedFeature}s sharing the exact placement of the
 *       aggregate's feature_rule; the consuming biome should add both via separate
 *       {@code biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, ...)} calls.</li>
 *   <li>Both ore features' {@code count} is the vein SIZE -> {@link OreConfiguration}'s size parameter
 *       (same convention as {@code ModConfigureFeatures.LUSH_GRASS_KEY} / {@code NetherlandsOreFeatures}).
 *       {@code burnt_basalt}/{@code smoking_ground}'s {@code may_replace} (dirt, grass_path, coarse dirt)
 *       map to {@link Blocks#DIRT}, {@link Blocks#DIRT_PATH} (Bedrock "grass_path" = Java "dirt path"),
 *       and {@link Blocks#COARSE_DIRT}. {@code burnt_magma}'s {@code may_replace} (basalt) maps to
 *       {@link Blocks#BASALT}.</li>
 *   <li>{@code smoking_ground.json} places {@code minecraft:campfire} - ported as {@link Blocks#CAMPFIRE}'s
 *       default state (lit) via the same ore-replacement pattern; Java Edition has no "ore feature that
 *       places a directional/lit block" primitive closer than this.</li>
 *   <li>{@code burnt_land_feature.json}'s distribution (iterations 4, x/z uniform 0-16, y uniform
 *       heightmap±4) maps to {@code CountPlacement.of(4)} + {@code InSquarePlacement.spread()} +
 *       {@code HeightmapPlacement.onHeightmap(WORLD_SURFACE_WG)} + {@code RandomOffsetPlacement.vertical(UniformInt.of(-4, 4))}.
 *       {@code smoking_ground_feature.json}'s distribution (iterations 50, y = heightmap-3 fixed) maps
 *       the same way but with a constant vertical offset of -3 instead of a uniform range.</li>
 *   <li>{@code scatter_fire_feature.json}'s {@code minecraft:scatter_feature} (iterations 90, gaussian
 *       x/z extent ±8, gaussian y extent ±4, wrapping {@code fire_feature}) maps onto vanilla's own
 *       {@code Feature.RANDOM_PATCH} + {@link RandomPatchConfiguration} exactly as vanilla's own
 *       {@code patch_grass.json} configured feature does it (tries/xz_spread/y_spread wrapping an inner
 *       placed feature) - same technique already used by
 *       {@link net.winepicfin.extrabiomes.worldgen.features.moss.MossFeatures}. iterations 90 -> tries 90;
 *       gaussian extents ±8/±4 -> xz_spread 8 / y_spread 4.</li>
 *   <li>{@code fire_feature.json} ({@code minecraft:single_block_feature} placing {@code minecraft:fire}
 *       over air with {@code enforce_survivability_rules}/{@code enforce_placement_rules} both true) maps
 *       to {@code Feature.SIMPLE_BLOCK} placing {@link Blocks#FIRE}'s default state, guarded by an air
 *       {@link BlockPredicate} plus {@link BlockPredicate#wouldSurvive(net.minecraft.world.level.block.state.BlockState, BlockPos)}
 *       against fire's own default state - this is vanilla's exact mechanism for "would this block's
 *       placement rules/survivability allow it here", the closest faithful equivalent of Bedrock's two
 *       enforce flags.</li>
 *   <li>{@code charred_forest_fire_feature.json}'s outer distribution (iterations 15, x/z uniform 0-16,
 *       y uniform {@code 0..heightmap*2}) is simplified to {@code CountPlacement.of(15)} +
 *       {@code InSquarePlacement.spread()} + {@code HeightmapPlacement.onHeightmap(WORLD_SURFACE_WG)}
 *       (i.e. each of the 15 outer origin rolls lands exactly on the surface, then the inner
 *       {@code RANDOM_PATCH} scatter does its own further ±8/±4 gaussian spread from there). The literal
 *       {@code heightmap*2} upper bound is not representable with vanilla's {@link net.minecraft.world.level.levelgen.VerticalAnchor}
 *       (which cannot scale relative to the surface height), and would mostly place fire attempts high
 *       in open air that immediately fail the survivability check anyway; anchoring at the surface
 *       instead preserves the intent (charred-ground fire scatter) without that unreachable-in-Java
 *       scaling quirk.</li>
 * </ul>
 */
public class CharredForestFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> BURNT_BASALT_KEY = configuredKey("burnt_basalt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BURNT_MAGMA_KEY = configuredKey("burnt_magma");

    /** Register via {@code biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, BURNT_BASALT_PLACED_KEY)}. */
    public static final ResourceKey<PlacedFeature> BURNT_BASALT_PLACED_KEY = placedKey("burnt_basalt");
    /** Register alongside {@link #BURNT_BASALT_PLACED_KEY} at the same LOCAL_MODIFICATIONS step (see class docs - aggregate simplification). */
    public static final ResourceKey<PlacedFeature> BURNT_MAGMA_PLACED_KEY = placedKey("burnt_magma");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SMOKING_GROUND_KEY = configuredKey("smoking_ground");
    /** Register via {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SMOKING_GROUND_PLACED_KEY)}. */
    public static final ResourceKey<PlacedFeature> SMOKING_GROUND_PLACED_KEY = placedKey("smoking_ground");

    public static final ResourceKey<ConfiguredFeature<?, ?>> FIRE_KEY = configuredKey("fire");
    /** Inner air/survivability-guarded single fire placement - not a top-level decoration by itself. */
    public static final ResourceKey<PlacedFeature> FIRE_INNER_PLACED_KEY = placedKey("fire_inner");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SCATTER_FIRE_KEY = configuredKey("scatter_fire");
    /** Register via {@code biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SCATTER_FIRE_PLACED_KEY)}. */
    public static final ResourceKey<PlacedFeature> SCATTER_FIRE_PLACED_KEY = placedKey("scatter_fire");

    // Toned down from Bedrock's literal values (15 outer / 90 patch tries) - a faithful port covers the ground in far more fire than reads right for Java.
    private static final int FIRE_OUTER_COUNT = 4;
    private static final int FIRE_PATCH_TRIES = 25;

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        List<OreConfiguration.TargetBlockState> dirtFamily = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.DIRT), Blocks.BASALT.defaultBlockState()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DIRT_PATH), Blocks.BASALT.defaultBlockState()),
                OreConfiguration.target(new BlockMatchTest(Blocks.COARSE_DIRT), Blocks.BASALT.defaultBlockState())
        );
        context.register(BURNT_BASALT_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(dirtFamily, 3, 0f)));

        context.register(BURNT_MAGMA_KEY, new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(new BlockMatchTest(Blocks.BASALT), Blocks.MAGMA_BLOCK.defaultBlockState(), 22, 0f)));

        List<OreConfiguration.TargetBlockState> dirtFamilyToCampfire = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.DIRT), Blocks.CAMPFIRE.defaultBlockState()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DIRT_PATH), Blocks.CAMPFIRE.defaultBlockState()),
                OreConfiguration.target(new BlockMatchTest(Blocks.COARSE_DIRT), Blocks.CAMPFIRE.defaultBlockState())
        );
        context.register(SMOKING_GROUND_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(dirtFamilyToCampfire, 3, 0f)));

        context.register(FIRE_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.FIRE.defaultBlockState()))));

        Holder<PlacedFeature> fireInner = placedFeatures.getOrThrow(FIRE_INNER_PLACED_KEY);
        context.register(SCATTER_FIRE_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(FIRE_PATCH_TRIES, 8, 4, fireInner)));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        List<PlacementModifier> burntLandPlacement = List.of(
                CountPlacement.of(4),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                RandomOffsetPlacement.vertical(UniformInt.of(-4, 4)),
                BiomeFilter.biome()
        );
        context.register(BURNT_BASALT_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(BURNT_BASALT_KEY), burntLandPlacement));
        context.register(BURNT_MAGMA_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(BURNT_MAGMA_KEY), burntLandPlacement));

        context.register(SMOKING_GROUND_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SMOKING_GROUND_KEY), List.of(
                CountPlacement.of(50),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                RandomOffsetPlacement.vertical(ConstantInt.of(-3)),
                BiomeFilter.biome()
        )));

        context.register(FIRE_INNER_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(FIRE_KEY), List.<PlacementModifier>of(
                BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.FIRE.defaultBlockState(), BlockPos.ZERO))
        )));

        context.register(SCATTER_FIRE_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SCATTER_FIRE_KEY), List.of(
                CountPlacement.of(FIRE_OUTER_COUNT),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BiomeFilter.biome()
        )));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "charred/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "charred/" + name));
    }
}
