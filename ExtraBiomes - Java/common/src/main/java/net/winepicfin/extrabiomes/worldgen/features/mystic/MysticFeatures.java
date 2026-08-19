package net.winepicfin.extrabiomes.worldgen.features.mystic;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;

/**
 * Java equivalent of Bedrock's mystic_forest.biome.json "sea_material": "extrabiomes:goo"
 * override - see {@link GooConversionFeature} for why this needs a feature rather than a direct
 * per-biome fluid swap. Placed once per column across the chunk (CountPlacement.of(256) is one
 * attempt per each of the 16x16 columns, same idea as vanilla's own FREEZE_TOP_LAYER coverage),
 * anchored to the world surface heightmap so it only ever starts at surface level.
 */
public class MysticFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<GooConversionFeature> GOO_CONVERSION_FEATURE =
            FEATURES.register("mystic_goo_conversion", () -> new GooConversionFeature(NoneFeatureConfiguration.CODEC));

    /** Must be called once from the mod's main class, e.g. {@code MysticFeatures.register(modEventBus);}. */
    public static void register() {
        FEATURES.register();
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_GOO_KEY = registerKey("mystic_goo_conversion");
    public static final ResourceKey<PlacedFeature> MYSTIC_GOO_PLACED_KEY = createKey("mystic_goo_conversion_placed");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(MYSTIC_GOO_KEY, new ConfiguredFeature<>(GOO_CONVERSION_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> configuration = configuredFeatures.getOrThrow(MYSTIC_GOO_KEY);

        context.register(MYSTIC_GOO_PLACED_KEY, new PlacedFeature(configuration, List.of(
                CountPlacement.of(256), InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome())));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
