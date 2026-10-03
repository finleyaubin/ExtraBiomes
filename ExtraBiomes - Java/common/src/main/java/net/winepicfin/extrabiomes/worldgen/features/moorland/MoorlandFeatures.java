package net.winepicfin.extrabiomes.worldgen.features.moorland;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.ore.ModOrePlacement;

import java.util.ArrayList;
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

    // Registered in Registries.FEATURE_TYPE (not just DeferredRegister) so codecs get stable registry names for Feature serialization/datagen.
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE_TYPE);

    public static final RegistrySupplier<MapCodec<DoubleTallGrassFeature>> DOUBLE_TALL_GRASS_FEATURE =
            FEATURES.register("moorland_double_tall_grass", () -> DoubleTallGrassFeature.CODEC);
    public static final RegistrySupplier<MapCodec<WaterLilyFixupFeature>> WATERLILY_FIXUP_FEATURE =
            FEATURES.register("moorland_waterlily_fixup", () -> WaterLilyFixupFeature.CODEC);

    /** Must be called once from the mod's main class, e.g. {@code MoorlandFeatures.register(modEventBus);}. */
    public static void register() {
        FEATURES.register();
    }

    public static final ResourceKey<Feature> MOORLAND_DOUBLE_TALL_GRASS_KEY = registerKey("moorland_double_tall_grass");
    public static final ResourceKey<Feature> MOORLAND_SHORT_DRY_GRASS_KEY = registerKey("moorland_short_dry_grass");
    public static final ResourceKey<Feature> MOORLAND_TALL_DRY_GRASS_KEY = registerKey("moorland_tall_dry_grass");
    public static final ResourceKey<Feature> MOORLAND_WATERLILY_KEY = registerKey("moorland_waterlily");

    public static final ResourceKey<PlacedFeature> MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY = createKey("moorland_double_tall_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_WATERLILY_PLACED_KEY = createKey("moorland_waterlily_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_SHORT_DRY_GRASS_PLACED_KEY = createKey("moorland_short_dry_grass_placed");
    public static final ResourceKey<PlacedFeature> MOORLAND_TALL_DRY_GRASS_PLACED_KEY = createKey("moorland_tall_dry_grass_placed");

    public static void bootstrapConfigured(BootstrapContext<Feature> context) {
        // 30/8/4 mirror each Bedrock scatter_feature's own inner gaussian jitter around the outer placement
        // position; that inner jitter is now folded into the placed feature's own modifiers (bootstrapPlaced
        // below) since 26.1 removed the random_patch feature/RandomPatchConfiguration.
        context.register(MOORLAND_DOUBLE_TALL_GRASS_KEY, new DoubleTallGrassFeature());

        context.register(MOORLAND_SHORT_DRY_GRASS_KEY, new SimpleBlockFeature(BlockStateProvider.holderOf(Blocks.SHORT_DRY_GRASS)));

        context.register(MOORLAND_TALL_DRY_GRASS_KEY, new SimpleBlockFeature(BlockStateProvider.holderOf(Blocks.TALL_DRY_GRASS)));

        context.register(MOORLAND_WATERLILY_KEY, new WaterLilyFixupFeature());
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> configuredFeatures = context.lookup(Registries.FEATURE);

        // The y = heightmap +/- 4 spread is now the random_offset modifier appended below (30/8/4 tries/xz/y,
        // same as the removed RandomPatchConfiguration's own numbers).
        PlacementModifier airOnly = BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
        register(context, MOORLAND_SHORT_DRY_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_SHORT_DRY_GRASS_KEY),
                withPatchModifiers(ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)),
                        30, 8, 4, airOnly));
        register(context, MOORLAND_TALL_DRY_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_TALL_DRY_GRASS_KEY),
                withPatchModifiers(ModOrePlacement.commonOrePlacement(30, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)),
                        30, 8, 4, airOnly));

        // No BiomeFilter: the feature checks the biome per column, as a single chunk-corner sample would skip chunks straddling a border.
        register(context, MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_DOUBLE_TALL_GRASS_KEY),
                List.of(HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));

        register(context, MOORLAND_WATERLILY_PLACED_KEY, configuredFeatures.getOrThrow(MOORLAND_WATERLILY_KEY),
                ModOrePlacement.commonOrePlacement(4, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
    }

    private static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                  Holder<Feature> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    /** Appends the removed random_patch feature's tries/xz_spread/y_spread (as count + random_offset) plus any filter. */
    private static List<PlacementModifier> withPatchModifiers(List<PlacementModifier> base, int tries, int xzSpread, int ySpread, PlacementModifier... extra) {
        List<PlacementModifier> result = new ArrayList<>(base);
        result.add(CountPlacement.of(tries));
        result.add(OffsetPlacement.ofTriangle(xzSpread, ySpread));
        result.addAll(List.of(extra));
        return result;
    }
}
