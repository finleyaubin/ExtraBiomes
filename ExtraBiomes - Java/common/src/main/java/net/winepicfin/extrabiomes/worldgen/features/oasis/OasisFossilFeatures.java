package net.winepicfin.extrabiomes.worldgen.features.oasis;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * Surface fossils for Grand Oasis, in the spirit of Soul Sand Valley's exposed bone piles rather
 * than vanilla desert/swamp's buried {@code Feature.FOSSIL} (which generates encased in stone
 * underground and is never visible without digging). Reuses vanilla's own eight
 * {@code minecraft:fossil/spine_*}/{@code skull_*} structure templates - the same bone pieces
 * desert fossils are built from - via the mod's {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature}
 * infrastructure, but placed directly on the sand heightmap instead of underground.
 */
public class OasisFossilFeatures {
    private static final String[] BONE_PIECES = {
            "fossil/spine_1", "fossil/spine_2", "fossil/spine_3", "fossil/spine_4",
            "fossil/skull_1", "fossil/skull_2", "fossil/skull_3", "fossil/skull_4"
    };

    public static final ResourceKey<ConfiguredFeature<?, ?>> SPINE_1_KEY = configuredKey("spine_1");
    public static final ResourceKey<PlacedFeature> SPINE_1_PLACED_KEY = placedKey("spine_1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPINE_2_KEY = configuredKey("spine_2");
    public static final ResourceKey<PlacedFeature> SPINE_2_PLACED_KEY = placedKey("spine_2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPINE_3_KEY = configuredKey("spine_3");
    public static final ResourceKey<PlacedFeature> SPINE_3_PLACED_KEY = placedKey("spine_3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPINE_4_KEY = configuredKey("spine_4");
    public static final ResourceKey<PlacedFeature> SPINE_4_PLACED_KEY = placedKey("spine_4");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKULL_1_KEY = configuredKey("skull_1");
    public static final ResourceKey<PlacedFeature> SKULL_1_PLACED_KEY = placedKey("skull_1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKULL_2_KEY = configuredKey("skull_2");
    public static final ResourceKey<PlacedFeature> SKULL_2_PLACED_KEY = placedKey("skull_2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKULL_3_KEY = configuredKey("skull_3");
    public static final ResourceKey<PlacedFeature> SKULL_3_PLACED_KEY = placedKey("skull_3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKULL_4_KEY = configuredKey("skull_4");
    public static final ResourceKey<PlacedFeature> SKULL_4_PLACED_KEY = placedKey("skull_4");

    private static final List<ResourceKey<ConfiguredFeature<?, ?>>> PIECE_KEYS = List.of(
            SPINE_1_KEY, SPINE_2_KEY, SPINE_3_KEY, SPINE_4_KEY, SKULL_1_KEY, SKULL_2_KEY, SKULL_3_KEY, SKULL_4_KEY
    );
    private static final List<ResourceKey<PlacedFeature>> PIECE_PLACED_KEYS = List.of(
            SPINE_1_PLACED_KEY, SPINE_2_PLACED_KEY, SPINE_3_PLACED_KEY, SPINE_4_PLACED_KEY,
            SKULL_1_PLACED_KEY, SKULL_2_PLACED_KEY, SKULL_3_PLACED_KEY, SKULL_4_PLACED_KEY
    );

    /**
     * extrabiomes:oasis/select_fossil - the single feature GrandOasis registers via
     * {@code biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SELECT_FOSSIL_PLACED_KEY)}.
     */
    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_FOSSIL_KEY = configuredKey("select_fossil");
    public static final ResourceKey<PlacedFeature> SELECT_FOSSIL_PLACED_KEY = placedKey("select_fossil");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        for (int i = 0; i < BONE_PIECES.length; i++) {
            ResourceLocation structure = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, BONE_PIECES[i]);
            context.register(PIECE_KEYS.get(i), new ConfiguredFeature<>(
                    ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                    new SingleStructureConfiguration(structure, Optional.empty(), 0, true, List.of(Blocks.SAND, Blocks.RED_SAND))
            ));
        }

        // Equal 1-in-8 chance per piece via the same sequential-trial conversion used by BoulderFeatures; the last piece is the guaranteed RANDOM_SELECTOR default.
        context.register(SELECT_FOSSIL_KEY, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(SPINE_1_PLACED_KEY), 1.0F / 8.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(SPINE_2_PLACED_KEY), 1.0F / 7.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(SPINE_3_PLACED_KEY), 1.0F / 6.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(SPINE_4_PLACED_KEY), 1.0F / 5.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(SKULL_1_PLACED_KEY), 1.0F / 4.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(SKULL_2_PLACED_KEY), 1.0F / 3.0F),
                        new WeightedPlacedFeature(placedFeatures.getOrThrow(SKULL_3_PLACED_KEY), 1.0F / 2.0F)
                ),
                placedFeatures.getOrThrow(SKULL_4_PLACED_KEY)
        )));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Bone pieces are only ever invoked as a WeightedPlacedFeature entry of SELECT_FOSSIL, which carries the real scatter/surface modifiers.
        for (int i = 0; i < PIECE_KEYS.size(); i++) {
            context.register(PIECE_PLACED_KEYS.get(i), new PlacedFeature(configuredFeatures.getOrThrow(PIECE_KEYS.get(i)), List.<PlacementModifier>of()));
        }

        // Bumped from 1-in-48 to 1-in-10 after playtest feedback; floor check moved to SingleStructureConfiguration.
        context.register(SELECT_FOSSIL_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(SELECT_FOSSIL_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "oasis/" + name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "oasis/" + name));
    }
}
