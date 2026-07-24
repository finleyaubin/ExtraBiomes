package net.winepicfin.extrabiomes.worldgen.features.mesa;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.ore.ModOrePlacement;

import java.util.List;

/**
 * Port of the Bedrock "underground_mesa" feature subsystem:
 * <pre>
 * features/underground_mesa/select_terracotta.json  (minecraft:weighted_random_feature, 8 entries)
 * features/underground_mesa/terracotta.json + terracotta_{blue,brown,cyan,light_grey,red,white,yellow}.json
 *                                              (minecraft:ore_feature, count 60, replaces stone/deepslate)
 * feature_rules/underground_mesa/mesa_underground_terracota_feature.json
 *                                              (biome tag "mesa", iterations 300, scatter_chance 100,
 *                                               x/z uniform [0,16], y uniform [-64,60])
 * </pre>
 * <p>
 * Mapping notes:
 * <ul>
 *   <li>Each "ore_feature" -> {@link Feature#ORE} + {@link OreConfiguration}, mirroring the existing
 *       {@code LUSH_GRASS_KEY} pattern in {@code ModConfigureFeatures}. Bedrock's {@code count: 60}
 *       maps directly to the {@link OreConfiguration} size parameter (same convention used there).
 *       {@code replace_rules.may_replace: [stone, deepslate]} becomes two {@link BlockMatchTest}
 *       target entries (one for {@link Blocks#STONE}, one for {@link Blocks#DEEPSLATE}) both mapping
 *       to the same output color, since Bedrock places the identical output block regardless of
 *       which of the two source blocks it replaces.</li>
 *   <li>{@code terracotta.json}'s unstained {@code minecraft:hardened_clay} -> vanilla
 *       {@link Blocks#TERRACOTTA} (plain/orange terracotta); the seven
 *       {@code minecraft:stained_hardened_clay} colors -> their vanilla {@code X_TERRACOTTA} block
 *       equivalents (Bedrock's {@code "silver"} color name -> vanilla {@code LIGHT_GRAY_TERRACOTTA}).</li>
 *   <li>"minecraft:weighted_random_feature" (select_terracotta.json) -> {@link Feature#RANDOM_SELECTOR}
 *       + {@link RandomFeatureConfiguration}, using the same sequential-trial weight conversion as
 *       {@code BoulderFeatures} (chance_i = weight_i / (sum of weight_i and all weights after it) in
 *       list order): weights terracotta 6, red 3, yellow 1, light_grey 3, cyan 4, blue 1, brown 4,
 *       white 4 (total 26). White is the heaviest/last-listed entry and lands on chance 1.0, so it
 *       becomes the RANDOM_SELECTOR's guaranteed "default" - this reproduces the exact same
 *       per-color probabilities as Bedrock's true weighted pick.</li>
 *   <li>The feature_rule's {@code iterations: 300} / {@code scatter_chance: 100} (i.e. always attempt,
 *       300 times per chunk) + uniform x/z [0,16] + uniform y [-64,60] map directly onto the existing
 *       {@link ModOrePlacement#commonOrePlacement(int, PlacementModifier)} helper (CountPlacement +
 *       InSquarePlacement.spread() + HeightRangePlacement + BiomeFilter), exactly like other ore
 *       placements in this mod - no {@code RarityFilter} is needed since scatter_chance is 100%.</li>
 *   <li>The Bedrock {@code has_biome_tag == "mesa"} biome filter is not reproduced here; per project
 *       convention the biome-wiring pass adds {@link #SELECT_TERRACOTTA_PLACED_KEY} directly to each
 *       relevant biome class instead.</li>
 * </ul>
 */
public class MesaFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_KEY = configuredKey("terracotta");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_PLACED_KEY = placedKey("terracotta");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_RED_KEY = configuredKey("terracotta_red");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_RED_PLACED_KEY = placedKey("terracotta_red");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_YELLOW_KEY = configuredKey("terracotta_yellow");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_YELLOW_PLACED_KEY = placedKey("terracotta_yellow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_LIGHT_GREY_KEY = configuredKey("terracotta_light_grey");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_LIGHT_GREY_PLACED_KEY = placedKey("terracotta_light_grey");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_CYAN_KEY = configuredKey("terracotta_cyan");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_CYAN_PLACED_KEY = placedKey("terracotta_cyan");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_BLUE_KEY = configuredKey("terracotta_blue");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_BLUE_PLACED_KEY = placedKey("terracotta_blue");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_BROWN_KEY = configuredKey("terracotta_brown");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_BROWN_PLACED_KEY = placedKey("terracotta_brown");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRACOTTA_WHITE_KEY = configuredKey("terracotta_white");
    public static final ResourceKey<PlacedFeature> TERRACOTTA_WHITE_PLACED_KEY = placedKey("terracotta_white");

    /**
     * extrabiomes:underground_mesa/select_terracotta - the single feature the
     * mesa_underground_terracota_feature feature_rule places. This is the ResourceKey biome classes
     * should register via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, SELECT_TERRACOTTA_PLACED_KEY)}.
     */
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_TERRACOTTA_KEY = configuredKey("select_terracotta");
    public static final ResourceKey<PlacedFeature> SELECT_TERRACOTTA_PLACED_KEY = placedKey("select_terracotta");

    // ===================================================================
    // configured features
    // ===================================================================
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        registerTerracottaOre(context, TERRACOTTA_KEY, Blocks.TERRACOTTA.defaultBlockState());
        registerTerracottaOre(context, TERRACOTTA_RED_KEY, Blocks.RED_TERRACOTTA.defaultBlockState());
        registerTerracottaOre(context, TERRACOTTA_YELLOW_KEY, Blocks.YELLOW_TERRACOTTA.defaultBlockState());
        registerTerracottaOre(context, TERRACOTTA_LIGHT_GREY_KEY, Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState());
        registerTerracottaOre(context, TERRACOTTA_CYAN_KEY, Blocks.CYAN_TERRACOTTA.defaultBlockState());
        registerTerracottaOre(context, TERRACOTTA_BLUE_KEY, Blocks.BLUE_TERRACOTTA.defaultBlockState());
        registerTerracottaOre(context, TERRACOTTA_BROWN_KEY, Blocks.BROWN_TERRACOTTA.defaultBlockState());
        registerTerracottaOre(context, TERRACOTTA_WHITE_KEY, Blocks.WHITE_TERRACOTTA.defaultBlockState());

        // select_terracotta.json weights (in listed order): terracotta 6, red 3, yellow 1,
        // light_grey 3, cyan 4, blue 1, brown 4, white 4 (total 26). Sequential-trial conversion
        // (chance_i = weight_i / remaining-total-from-here) reproduces the same per-color
        // probabilities as Bedrock's true weighted pick; white (the last entry) ends up guaranteed
        // (chance 1.0) and becomes the RANDOM_SELECTOR's mandatory default.
        context.register(SELECT_TERRACOTTA_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TERRACOTTA_PLACED_KEY), 6.0F / 26.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TERRACOTTA_RED_PLACED_KEY), 3.0F / 20.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TERRACOTTA_YELLOW_PLACED_KEY), 1.0F / 17.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TERRACOTTA_LIGHT_GREY_PLACED_KEY), 3.0F / 16.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TERRACOTTA_CYAN_PLACED_KEY), 4.0F / 13.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TERRACOTTA_BLUE_PLACED_KEY), 1.0F / 9.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(TERRACOTTA_BROWN_PLACED_KEY), 4.0F / 8.0F)
                ),
                placedFeatures.getOrThrow(TERRACOTTA_WHITE_PLACED_KEY)
        )));
    }

    private static void registerTerracottaOre(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, net.minecraft.world.level.block.state.BlockState outputState) {
        RuleTest stone = new BlockMatchTest(Blocks.STONE);
        RuleTest deepslate = new BlockMatchTest(Blocks.DEEPSLATE);
        List<OreConfiguration.TargetBlockState> targets = List.of(
                OreConfiguration.target(stone, outputState),
                OreConfiguration.target(deepslate, outputState)
        );
        context.register(key, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(targets, 60, 0)));
    }

    // ===================================================================
    // placed features
    // ===================================================================
    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        registerNoModifiers(context, configuredFeatures, TERRACOTTA_PLACED_KEY, TERRACOTTA_KEY);
        registerNoModifiers(context, configuredFeatures, TERRACOTTA_RED_PLACED_KEY, TERRACOTTA_RED_KEY);
        registerNoModifiers(context, configuredFeatures, TERRACOTTA_YELLOW_PLACED_KEY, TERRACOTTA_YELLOW_KEY);
        registerNoModifiers(context, configuredFeatures, TERRACOTTA_LIGHT_GREY_PLACED_KEY, TERRACOTTA_LIGHT_GREY_KEY);
        registerNoModifiers(context, configuredFeatures, TERRACOTTA_CYAN_PLACED_KEY, TERRACOTTA_CYAN_KEY);
        registerNoModifiers(context, configuredFeatures, TERRACOTTA_BLUE_PLACED_KEY, TERRACOTTA_BLUE_KEY);
        registerNoModifiers(context, configuredFeatures, TERRACOTTA_BROWN_PLACED_KEY, TERRACOTTA_BROWN_KEY);
        registerNoModifiers(context, configuredFeatures, TERRACOTTA_WHITE_PLACED_KEY, TERRACOTTA_WHITE_KEY);

        // mesa_underground_terracota_feature.json: iterations 300, scatter_chance 100 (always
        // attempt), x/z uniform [0,16], y uniform [-64,60] -> reuse the shared ore placement helper.
        // NOTE: CountPlacement's IntProvider codec caps at 256, so Bedrock's 300 iterations/chunk is
        // clamped to the engine max (commonOrePlacement uses CountPlacement.of(pCount) internally).
        context.register(SELECT_TERRACOTTA_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_TERRACOTTA_KEY),
                ModOrePlacement.commonOrePlacement(256, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(60)))
        ));
    }

    private static void registerNoModifiers(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, ResourceKey<PlacedFeature> placedKey, ResourceKey<ConfiguredFeature<?, ?>> configuredKey) {
        context.register(placedKey, new PlacedFeature(configuredFeatures.getOrThrow(configuredKey), List.<PlacementModifier>of()));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "underground_mesa/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "underground_mesa/" + name));
    }
}
