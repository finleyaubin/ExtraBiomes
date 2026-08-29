package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.moss.MossFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Port of the Bedrock "extrabiomes:volcanic_moss_tundra/*" feature subsystem (see
 * "ExtraBiomes - Bedrock/packs/BP/features/volcanic_moss_tundra/*" and its
 * "feature_rules/volcanic_moss_tundra/*"):
 * <ul>
 *   <li>lava_river_core / lava_river_bank - two {@link net.minecraft.world.level.levelgen.feature.VegetationPatchFeature}s
 *       gated by {@link RiverNoiseFilter} on the same shared multi-sine "shore" noise field
 *       (core &lt;0.003 = the lava channel itself; bank 0.003-0.006 = the magma rim around it) -
 *       together these render as a winding lava river with magma banks.</li>
 *   <li>basalt_bank - {@link BasaltBankFeature}, a fixed grid of basalt columns, gated by the
 *       same noise field's next ring out (0.006-0.01), clustering basalt pillars along the
 *       river's outer shore.</li>
 *   <li>high_elevation_moss_floor - a {@link net.minecraft.world.level.levelgen.feature.VegetationPatchFeature}
 *       laying down a moss_block floor patch, restricted to Y&gt;=75 via {@link MinYFilter}.</li>
 *   <li>elevation_moss - reuses {@link MossFeatures#MOSS_CARPET_KEY} (moss carpet) with the same
 *       Y&gt;=75 restriction, for moss carpet scattered on top of that high ground.</li>
 *   <li>select_rock_formation / select_volcano - two {@link Feature#RANDOM_SELECTOR}s over the
 *       converted pillar/boulder/elephant_rock and volcano_1/2 .nbt structures respectively, via
 *       the shared {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature}
 *       infrastructure (same pattern as {@link net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures}).
 *       Bedrock specifies no facing_direction for any of these structures, so all use random
 *       rotation.</li>
 * </ul>
 * Bedrock's {@code vegetation_chance: 0} on all three VegetationPatchConfiguration ports below
 * means their "vegetation_feature" sub-placement never actually fires - {@link #NO_OP_PLACED_KEY}
 * exists purely to satisfy the required {@code Holder<PlacedFeature>} parameter.
 */
public class VolcanicMossTundraFeatures {

    public static final TagKey<Block> VOLCANIC_TUNDRA_REPLACEABLE =
            TagKey.create(Registries.BLOCK, new ResourceLocation(ExtraBiomes.MOD_ID, "volcanic_moss_tundra_replaceable"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> NO_OP_KEY = configuredKey("no_op");
    public static final ResourceKey<PlacedFeature> NO_OP_PLACED_KEY = placedKey("no_op");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVA_RIVER_CORE_KEY = configuredKey("lava_river_core");
    public static final ResourceKey<PlacedFeature> LAVA_RIVER_CORE_PLACED_KEY = placedKey("lava_river_core");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVA_RIVER_BANK_KEY = configuredKey("lava_river_bank");
    public static final ResourceKey<PlacedFeature> LAVA_RIVER_BANK_PLACED_KEY = placedKey("lava_river_bank");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HIGH_ELEVATION_MOSS_FLOOR_KEY = configuredKey("high_elevation_moss_floor");
    public static final ResourceKey<PlacedFeature> HIGH_ELEVATION_MOSS_FLOOR_PLACED_KEY = placedKey("high_elevation_moss_floor");

    public static final ResourceKey<PlacedFeature> ELEVATION_MOSS_PLACED_KEY = placedKey("elevation_moss");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BASALT_BANK_KEY = configuredKey("basalt_bank");
    public static final ResourceKey<PlacedFeature> BASALT_BANK_PLACED_KEY = placedKey("basalt_bank");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_ROCK_FORMATION_KEY = configuredKey("select_rock_formation");
    public static final ResourceKey<PlacedFeature> SELECT_ROCK_FORMATION_PLACED_KEY = placedKey("select_rock_formation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_VOLCANO_KEY = configuredKey("select_volcano");
    public static final ResourceKey<PlacedFeature> SELECT_VOLCANO_PLACED_KEY = placedKey("select_volcano");

    private static final int ROCK_FORMATION_GROUND_OFFSET = -3;
    private static final int VOLCANO_GROUND_OFFSET = -6;

    private static final String[] PILLARS = {"pillar_1", "pillar_2", "pillar_3", "pillar_4", "pillar_5", "pillar_6"};
    private static final String[] BOULDERS = {"boulder_1", "boulder_2", "boulder_3", "boulder_4", "boulder_5", "boulder_6", "boulder_7", "boulder_8"};
    private static final String[] ELEPHANT_ROCKS = {"elephant_rock_1", "elephant_rock_2", "elephant_rock_3", "elephant_rock_4"};
    private static final String[] VOLCANOES = {"volcano_1", "volcano_2"};

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        context.register(NO_OP_KEY, new ConfiguredFeature<>(Feature.NO_OP, NoneFeatureConfiguration.INSTANCE));
        Holder<PlacedFeature> noOp = placedFeatures.getOrThrow(NO_OP_PLACED_KEY);

        context.register(LAVA_RIVER_CORE_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                VOLCANIC_TUNDRA_REPLACEABLE, BlockStateProvider.simple(Blocks.LAVA.defaultBlockState()), noOp,
                CaveSurface.FLOOR, ConstantInt.of(2), 0.4F, 6, 0.0F, UniformInt.of(1, 2), 0.6F)));
        context.register(LAVA_RIVER_BANK_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                VOLCANIC_TUNDRA_REPLACEABLE, BlockStateProvider.simple(Blocks.MAGMA_BLOCK.defaultBlockState()), noOp,
                CaveSurface.FLOOR, ConstantInt.of(2), 0.4F, 6, 0.0F, UniformInt.of(2, 3), 0.6F)));
        context.register(HIGH_ELEVATION_MOSS_FLOOR_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(
                VOLCANIC_TUNDRA_REPLACEABLE, BlockStateProvider.simple(Blocks.MOSS_BLOCK.defaultBlockState()), noOp,
                CaveSurface.FLOOR, ConstantInt.of(1), 0.2F, 4, 0.0F, UniformInt.of(3, 6), 0.6F)));

        context.register(BASALT_BANK_KEY, new ConfiguredFeature<>(ModVolcanicPlacementModifiers.BASALT_BANK.get(), NoneFeatureConfiguration.INSTANCE));

        // ROCK_FORMATION_GROUND_OFFSET/VOLCANO_GROUND_OFFSET sink each structure below the ground heightmap so it reads as embedded rather than resting on top.
        for (String pillar : PILLARS) registerSingleStructure(context, pillar, ROCK_FORMATION_GROUND_OFFSET);
        for (String boulder : BOULDERS) registerSingleStructure(context, boulder, ROCK_FORMATION_GROUND_OFFSET);
        for (String elephantRock : ELEPHANT_ROCKS) registerSingleStructure(context, elephantRock, ROCK_FORMATION_GROUND_OFFSET);
        for (String volcano : VOLCANOES) registerSingleStructure(context, volcano, VOLCANO_GROUND_OFFSET);

        // Weights converted to sequential-trial chances (weight / remaining-total-from-here), same technique as BoulderFeatures, with the final entry as the RANDOM_SELECTOR's guaranteed default.
        List<WeightedPlacedFeature> rockEntries = new ArrayList<>();
        float remaining = 58.0F;
        for (String pillar : PILLARS) {
            rockEntries.add(new WeightedPlacedFeature(structurePlaced(placedFeatures, pillar), 3.0F / remaining));
            remaining -= 3.0F;
        }
        for (String boulder : BOULDERS) {
            rockEntries.add(new WeightedPlacedFeature(structurePlaced(placedFeatures, boulder), 4.0F / remaining));
            remaining -= 4.0F;
        }
        for (int i = 0; i < ELEPHANT_ROCKS.length - 1; i++) {
            rockEntries.add(new WeightedPlacedFeature(structurePlaced(placedFeatures, ELEPHANT_ROCKS[i]), 2.0F / remaining));
            remaining -= 2.0F;
        }
        context.register(SELECT_ROCK_FORMATION_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                rockEntries, structurePlaced(placedFeatures, ELEPHANT_ROCKS[ELEPHANT_ROCKS.length - 1]))));

        context.register(SELECT_VOLCANO_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(structurePlaced(placedFeatures, VOLCANOES[0]), 0.5F)),
                structurePlaced(placedFeatures, VOLCANOES[1]))));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(NO_OP_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(NO_OP_KEY), List.of()));

        context.register(LAVA_RIVER_CORE_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(LAVA_RIVER_CORE_KEY),
                List.of(CountPlacement.of(30), InSquarePlacement.spread(), new RiverNoiseFilter(0.0, 0.003),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome())));
        context.register(LAVA_RIVER_BANK_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(LAVA_RIVER_BANK_KEY),
                List.of(CountPlacement.of(30), InSquarePlacement.spread(), new RiverNoiseFilter(0.003, 0.006),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome())));
        context.register(HIGH_ELEVATION_MOSS_FLOOR_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(HIGH_ELEVATION_MOSS_FLOOR_KEY),
                List.of(CountPlacement.of(10), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), new MinYFilter(75), BiomeFilter.biome())));

        context.register(ELEVATION_MOSS_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(MossFeatures.MOSS_CARPET_KEY),
                List.of(CountPlacement.of(60), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), new MinYFilter(75), BiomeFilter.biome())));

        context.register(BASALT_BANK_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(BASALT_BANK_KEY),
                List.of(CountPlacement.of(24), InSquarePlacement.spread(), new RiverNoiseFilter(0.006, 0.01),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome())));

        for (String pillar : PILLARS) registerNoModifiers(context, configuredFeatures, pillar);
        for (String boulder : BOULDERS) registerNoModifiers(context, configuredFeatures, boulder);
        for (String elephantRock : ELEPHANT_ROCKS) registerNoModifiers(context, configuredFeatures, elephantRock);
        for (String volcano : VOLCANOES) registerNoModifiers(context, configuredFeatures, volcano);

        // OCEAN_FLOOR_WG (not WORLD_SURFACE_WG) ignores fluids, so these don't land on top of water; the ground offset is applied separately via SingleStructureConfiguration.
        context.register(SELECT_ROCK_FORMATION_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SELECT_ROCK_FORMATION_KEY),
                List.of(RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG), BiomeFilter.biome())));

        context.register(SELECT_VOLCANO_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(SELECT_VOLCANO_KEY),
                List.of(RarityFilter.onAverageOnceEvery(100), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG), BiomeFilter.biome())));
    }

    private static void registerSingleStructure(BootstrapContext<ConfiguredFeature<?, ?>> context, String name, int groundOffset) {
        ResourceLocation structure = new ResourceLocation(ExtraBiomes.MOD_ID, "volcanic_moss_tundra/" + name);
        context.register(configuredKey(name), new ConfiguredFeature<>(ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(structure, Optional.<net.minecraft.world.level.block.Rotation>empty(), groundOffset)));
    }

    private static void registerNoModifiers(BootstrapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures, String name) {
        context.register(placedKey(name), new PlacedFeature(configuredFeatures.getOrThrow(configuredKey(name)), List.<PlacementModifier>of()));
    }

    private static Holder<PlacedFeature> structurePlaced(HolderGetter<PlacedFeature> placedFeatures, String name) {
        return placedFeatures.getOrThrow(placedKey(name));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "volcanic_moss_tundra/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "volcanic_moss_tundra/" + name));
    }
}
