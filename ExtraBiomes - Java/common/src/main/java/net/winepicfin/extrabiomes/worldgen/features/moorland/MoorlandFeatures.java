package net.winepicfin.extrabiomes.worldgen.features.moorland;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.ore.ModOrePlacement;

import java.util.ArrayList;
import java.util.List;

/**
 * Java port of the Bedrock "moorland" feature subsystem:
 * <ul>
 *   <li>features/moorland/moorlands_podzol_feature.json (aggregate -> minecraft:optional_podzol_feature)
 *       + feature_rules/moorland/moorland_after_surface_podzol_feature.json (after_surface_pass,
 *       iterations noise-clamped [15,160], x/z uniform [0,16], y = heightmap)</li>
 *   <li>features/moorland/select_grass_feature.json (aggregate of the 4 grass scatter_features below,
 *       unconditionally run together) + feature_rules/moorland/moorland_scatter_tall_grass_feature.json
 *       (surface_pass, iterations 30, x/z uniform [0,16], y = heightmap +/- 4)
 *     <ul>
 *       <li>moorlands_scatter_tall_grass_feature.json -> minecraft:tall_grass_feature</li>
 *       <li>moorlands_scatter_double_tall_grass_feature .json -> minecraft:grass_double_plant_patch_feature</li>
 *       <li>moorlands_scatter_small_dry_grass_feature.json -> minecraft:short_dry_grass_feature</li>
 *       <li>moorlands_scatter_tall_dry_grass_feature.json -> minecraft:random_dry_grass_block_feature</li>
 *     </ul>
 *     Each Bedrock scatter_feature itself does a further gaussian x/z/y jitter (iterations 30,
 *     x/z extent +/-8, y extent +/-4) around the position chosen by the feature_rule - this nested
 *     "N outer positions, each with an M-iteration inner scatter" shape maps directly onto vanilla's
 *     own two-level RANDOM_PATCH pattern (an outer count/inSquare/heightmap PlacedFeature wrapping a
 *     RandomPatchConfiguration with its own tries/xz_spread/y_spread), which is what's used below.</li>
 *   <li>feature_rules/moorland/moorlands_surface_waterlily_feature.json (surface_pass, iterations 4,
 *       places minecraft:fixup_waterlily_position_feature)</li>
 * </ul>
 * Simplifications (Bedrock vanilla feature bodies aren't shipped as JSON we can read, since they're
 * built into the game - these are ported to their closest vanilla Java 1.20.1 equivalents):
 * <ul>
 *   <li>minecraft:optional_podzol_feature -> {@link PodzolConversionFeature}: converts the surface
 *       block to podzol if it's grass/dirt/coarse dirt, no-op otherwise.</li>
 *   <li>minecraft:tall_grass_feature -> Feature.SIMPLE_BLOCK placing {@link Blocks#SHORT_GRASS} (the short
 *       grass tuft block in 1.20.1, renamed short_grass in later versions), gated by a
 *       {@code BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)} - the same
 *       guard vanilla's own grass patches use - so each of the up to 900 (30 outer x 30 inner)
 *       attempts only actually places where nothing already stands, instead of unconditionally
 *       overwriting whatever block a given attempt happens to land on. Omitting that guard is what
 *       made the four grass/dry-grass layers stack on top of each other into an unnaturally dense
 *       mass.</li>
 *   <li>minecraft:grass_double_plant_patch_feature -> {@link DoubleTallGrassFeature} placing both
 *       halves of {@link Blocks#TALL_GRASS}.</li>
 *   <li>minecraft:short_dry_grass_feature and minecraft:random_dry_grass_block_feature -> scatters of
 *       {@link Blocks#SHORT_DRY_GRASS} and {@link Blocks#TALL_DRY_GRASS}, same air-only guard as above.
 *       Bedrock's color variants for the "random" one have no Java equivalent.</li>
 *   <li>minecraft:fixup_waterlily_position_feature -> {@link WaterLilyFixupFeature}: searches
 *       downward from the placement column for a water surface and places a lily pad.</li>
 * </ul>
 */
public class MoorlandFeatures {

    // Registered in Registries.FEATURE (not just DeferredRegister) so codecs get stable registry names for ConfiguredFeature serialization/datagen.
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<PodzolConversionFeature> PODZOL_CONVERSION_FEATURE =
            FEATURES.register("moorland_podzol_conversion", () -> new PodzolConversionFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<DoubleTallGrassFeature> DOUBLE_TALL_GRASS_FEATURE =
            FEATURES.register("moorland_double_tall_grass", () -> new DoubleTallGrassFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<WaterLilyFixupFeature> WATERLILY_FIXUP_FEATURE =
            FEATURES.register("moorland_waterlily_fixup", () -> new WaterLilyFixupFeature(NoneFeatureConfiguration.CODEC));

    /** Must be called once from the mod's main class, e.g. {@code MoorlandFeatures.register(modEventBus);}. */
    public static void register() {
        FEATURES.register();
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_PODZOL_KEY = registerKey("moorland_podzol");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_TALL_GRASS_KEY = registerKey("moorland_tall_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_DOUBLE_TALL_GRASS_KEY = registerKey("moorland_double_tall_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_SHORT_DRY_GRASS_KEY = registerKey("moorland_short_dry_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_TALL_DRY_GRASS_KEY = registerKey("moorland_tall_dry_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOORLAND_WATERLILY_KEY = registerKey("moorland_waterlily");

    public static final ResourceKey<PlacedFeature> MOORLAND_PODZOL_PLACED_KEY = createKey("moorland_podzol_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_TALL_GRASS_PLACED_KEY = createKey("moorland_tall_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY = createKey("moorland_double_tall_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_SHORT_DRY_GRASS_PLACED_KEY = createKey("moorland_short_dry_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_TALL_DRY_GRASS_PLACED_KEY = createKey("moorland_tall_dry_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_WATERLILY_PLACED_KEY = createKey("moorland_waterlily_placed");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(MOORLAND_PODZOL_KEY, new ConfiguredFeature<>(PODZOL_CONVERSION_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        // 30/8/4 mirror each Bedrock scatter_feature's own inner gaussian jitter around the outer placement
        // position; that inner jitter is now folded into the placed feature's own modifiers (bootstrapPlaced
        // below) since 26.1 removed the random_patch feature/RandomPatchConfiguration.
        context.register(MOORLAND_TALL_GRASS_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS))));

        context.register(MOORLAND_DOUBLE_TALL_GRASS_KEY, new ConfiguredFeature<>(DOUBLE_TALL_GRASS_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        context.register(MOORLAND_SHORT_DRY_GRASS_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_DRY_GRASS))));

        context.register(MOORLAND_TALL_DRY_GRASS_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.TALL_DRY_GRASS))));

        context.register(MOORLAND_WATERLILY_KEY, new ConfiguredFeature<>(WATERLILY_FIXUP_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Java has no analogue of Bedrock's per-chunk noise-derived placement count, so it's approximated with a uniform random count over the same [15,160] range.
        register(context, MOORLAND_PODZOL_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_PODZOL_KEY),
                List.of(CountPlacement.of(UniformInt.of(15, 160)), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome()));

        // The y = heightmap +/- 4 spread is now the random_offset modifier appended below (30/8/4 tries/xz/y,
        // same as the removed RandomPatchConfiguration's own numbers).
        PlacementModifier airOnly = BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
        register(context, MOORLAND_TALL_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_TALL_GRASS_KEY),
                withPatchModifiers(ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)),
                        8, 4, airOnly));
        register(context, MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_DOUBLE_TALL_GRASS_KEY),
                withPatchModifiers(ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)),
                        8, 4));
        register(context, MOORLAND_SHORT_DRY_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_SHORT_DRY_GRASS_KEY),
                withPatchModifiers(ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)),
                        8, 4, airOnly));
        register(context, MOORLAND_TALL_DRY_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_TALL_DRY_GRASS_KEY),
                withPatchModifiers(ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)),
                        8, 4, airOnly));

        register(context, MOORLAND_WATERLILY_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_WATERLILY_KEY),
                ModOrePlacement.commonOrePlacement(4, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                  Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    /** Appends the removed random_patch feature's tries/xz_spread/y_spread (as count + random_offset) plus any filter. */
    private static List<PlacementModifier> withPatchModifiers(List<PlacementModifier> base, int xzSpread, int ySpread, PlacementModifier... extra) {
        List<PlacementModifier> result = new ArrayList<>(base);
        result.add(CountPlacement.of(30));
        result.add(RandomOffsetPlacement.ofTriangle(xzSpread, ySpread));
        result.addAll(List.of(extra));
        return result;
    }
}
