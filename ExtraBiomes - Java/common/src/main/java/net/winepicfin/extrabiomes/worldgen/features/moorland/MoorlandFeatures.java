package net.winepicfin.extrabiomes.worldgen.features.moorland;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.placement.ChunkOriginSnap;
import net.winepicfin.extrabiomes.worldgen.placement.InBiomeChunkSample;
import net.winepicfin.extrabiomes.worldgen.features.ore.ModOrePlacement;

import java.util.List;

/**
 * Java port of the Bedrock "moorland" feature subsystem:
 * <ul>
 *   <li>moorlands_podzol_feature / moorland_after_surface_podzol_feature: not a feature on Java - podzol is
 *       painted by noise in ModSurfaceRules so it forms big blobs that cross chunk borders.</li>
 *   <li>features/moorland/select_grass_feature.json (aggregate of the 4 grass scatter_features below,
 *       unconditionally run together) + feature_rules/moorland/moorland_scatter_tall_grass_feature.json
 *       (surface_pass, iterations 30, x/z uniform [0,16], y = heightmap +/- 4)
 *     Only the double tall grass member is ported, as a per-chunk column walk (see {@link DoubleTallGrassFeature}):
 *     the Java port deliberately drops the short grass and dry grass (dead bush) scatters so the moorland
 *     floor is almost entirely tall grass.</li>
 *   <li>feature_rules/moorland/moorlands_surface_waterlily_feature.json (surface_pass, iterations 4,
 *       places minecraft:fixup_waterlily_position_feature)</li>
 * </ul>
 * Simplifications (Bedrock vanilla feature bodies aren't shipped as JSON we can read, since they're
 * built into the game - these are ported to their closest vanilla Java 1.20.1 equivalents):
 * <ul>
 *   <li>minecraft:grass_double_plant_patch_feature -> {@link DoubleTallGrassFeature} placing both
 *       halves of {@link Blocks#TALL_GRASS} on ~95% of the biome's columns.</li>
 *   <li>minecraft:fixup_waterlily_position_feature -> {@link WaterLilyFixupFeature}: searches
 *       downward from the placement column for a water surface and places a lily pad.</li>
 * </ul>
 */
public class MoorlandFeatures {

    // Registered in Registries.FEATURE (not just DeferredRegister) so codecs get stable registry names for ConfiguredFeature serialization/datagen.
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<DoubleTallGrassFeature> DOUBLE_TALL_GRASS_FEATURE =
            FEATURES.register("moorland_double_tall_grass", () -> new DoubleTallGrassFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<WaterLilyFixupFeature> WATERLILY_FIXUP_FEATURE =
            FEATURES.register("moorland_waterlily_fixup", () -> new WaterLilyFixupFeature(NoneFeatureConfiguration.CODEC));

    /** Must be called once from the mod's main class, e.g. {@code MoorlandFeatures.register(modEventBus);}. */
    public static void register() {
        FEATURES.register();
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_DOUBLE_TALL_GRASS_KEY = registerKey("moorland_double_tall_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_WATERLILY_KEY = registerKey("moorland_waterlily");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_SHORT_DRY_GRASS_KEY = registerKey("moorland_short_dry_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_TALL_DRY_GRASS_KEY = registerKey("moorland_tall_dry_grass");

    public static final ResourceKey<PlacedFeature> MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY = createKey("moorland_double_tall_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_WATERLILY_PLACED_KEY = createKey("moorland_waterlily_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_SHORT_DRY_GRASS_PLACED_KEY = createKey("moorland_short_dry_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_TALL_DRY_GRASS_PLACED_KEY = createKey("moorland_tall_dry_grass_placed");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(MOORLAND_DOUBLE_TALL_GRASS_KEY, new ConfiguredFeature<>(DOUBLE_TALL_GRASS_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        context.register(MOORLAND_WATERLILY_KEY, new ConfiguredFeature<>(WATERLILY_FIXUP_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        // 30/8/4 mirror each Bedrock scatter_feature's own inner gaussian jitter around the outer placement position.
        context.register(MOORLAND_SHORT_DRY_GRASS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(30, 8, 4,
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_DRY_GRASS)),
                                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)))));

        context.register(MOORLAND_TALL_DRY_GRASS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(30, 8, 4,
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.TALL_DRY_GRASS)),
                                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)))));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // The feature checks the biome per column; the sample/snap pair only lets BiomeFilter test a column in the biome instead of the chunk corner.
        register(context, MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_DOUBLE_TALL_GRASS_KEY),
                List.of(InBiomeChunkSample.INSTANCE, BiomeFilter.biome(), ChunkOriginSnap.INSTANCE, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));

        register(context, MOORLAND_WATERLILY_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_WATERLILY_KEY),
                ModOrePlacement.commonOrePlacement(4, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));

        // The y = heightmap +/- 4 spread is already folded into each configured feature's own RandomPatchConfiguration y_spread above.
        register(context, MOORLAND_SHORT_DRY_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_SHORT_DRY_GRASS_KEY),
                ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
        register(context, MOORLAND_TALL_DRY_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_TALL_DRY_GRASS_KEY),
                ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                  Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
