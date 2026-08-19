package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Registers the {@link BrycePillarsFeature} Feature type itself (Registries.FEATURE) so it has a
 * stable id ("extrabiomes:bryce_pillars") and can be referenced from any biome's ConfiguredFeature -
 * mirrors {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures}.
 * Must be called once from the mod's main class, separately from the per-world-generation
 * ConfiguredFeature/PlacedFeature bootstrap in {@link BryceMesaPillarFeatures}.
 */
public class ModBrycePillarsFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<BrycePillarsFeature> BRYCE_PILLARS =
            FEATURES.register("bryce_pillars", () -> new BrycePillarsFeature(BrycePillarsConfiguration.CODEC));

    public static void register() {
        FEATURES.register();
    }
}
