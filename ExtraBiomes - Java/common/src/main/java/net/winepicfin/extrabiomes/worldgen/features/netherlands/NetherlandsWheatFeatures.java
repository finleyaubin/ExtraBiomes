package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Bedrock's wheat-field subsystem: "extrabiomes:the_netherlands/wheat_big" / "wheat_small"
 * ({@code minecraft:structure_template_feature}, each a single-block .mcstructure - verified from the
 * block_palette: minecraft:wheat with growth 7 / growth 4) and "select_wheat" ({@code minecraft:weighted_random_feature},
 * wheat_big:wheat_small = 5:3), placed via feature_rules "extrabiomes:netherlands_wheat_feature" - which is gated
 * on {@code has_biome_tag "mutated"} in ADDITION to "the_netherlands", i.e. this only applies to
 * TheNetherlandsMutated, not the base TheNetherlands biome.
 * <p>
 * wheat_big -> Blocks.WHEAT with CropBlock.AGE = 7 (fully grown), 62.5% chance. wheat_small -> AGE = 4,
 * 37.5% chance - the same 5:3 ratio Bedrock's select_wheat used, now rolled per-column directly inside
 * {@link NetherlandsWheatFieldFeature} rather than via a wrapping RANDOM_SELECTOR feature.
 * <p>
 * DEVIATION FROM BEDROCK: Bedrock's ground conversion ("wheat_floor_feature", a {@code minecraft:vegetation_patch_feature}
 * with a 1-10 block random-radius patch) and its scatter-based placement were both dropped. VEGETATION_PATCH grows
 * a cross/blob shape per attempt (not a filled rectangle), and even a scattered CountPlacement/InSquarePlacement
 * combo samples columns independently with replacement, so no attempt count guarantees full coverage - both left
 * visible gaps of untouched terrain, which defeats the point of a solid wheat field. Instead,
 * {@link net.winepicfin.extrabiomes.worldgen.biomes.surface.ModSurfaceRules} now paints this biome's entire floor as
 * FARMLAND directly (deterministic, no gaps by construction), and {@link NetherlandsWheatFieldFeature} is invoked
 * once per chunk and iterates every column itself - deterministic full coverage, no wasted placement attempts.
 * Hydration ponds (not from Bedrock) are rolled in that same per-column pass rather than as a separate
 * feature - see its javadoc for why.
 */
public class NetherlandsWheatFeatures {
    // Registered in Registries.FEATURE (not just DeferredRegister) so the codec gets a stable registry name for ConfiguredFeature serialization/datagen.
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<NetherlandsWheatFieldFeature> WHEAT_FIELD_FEATURE =
            FEATURES.register("netherlands_wheat_field", () -> new NetherlandsWheatFieldFeature(NoneFeatureConfiguration.CODEC));

    /** Must be called once from the mod's main class, e.g. {@code NetherlandsWheatFeatures.register();}. */
    public static void register() {
        FEATURES.register();
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> WHEAT_FLOOR_KEY = key("netherlands_wheat_floor");
    public static final ResourceKey<PlacedFeature> WHEAT_FLOOR_PLACED_KEY = placedKey("netherlands_wheat_floor");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(WHEAT_FLOOR_KEY, new ConfiguredFeature<>(WHEAT_FIELD_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // No CountPlacement/InSquarePlacement: the feature iterates every column of its chunk itself, so
        // this only needs to run exactly once per chunk. Deliberately no BiomeFilter either - without
        // CountPlacement/InSquarePlacement this only ever samples ONE random point per chunk, and near a
        // biome boundary that single sample can land just outside the_netherlands_mutated even though
        // ModSurfaceRules (which checks biome per-column, not once per chunk) already painted farmland
        // across most of the chunk - skipping the whole chunk's wheat/hydration pass and leaving bare
        // farmland with chunk-aligned gaps. The feature's own per-column farmland check already only
        // ever touches columns ModSurfaceRules actually painted for this biome, so the outer BiomeFilter
        // was redundant on top of being unreliable.
        List<PlacementModifier> once = List.of(HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG));
        context.register(WHEAT_FLOOR_PLACED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(WHEAT_FLOOR_KEY), once));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
