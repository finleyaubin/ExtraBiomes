package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Registers the {@link BrycePillarsFeature} Feature type itself (Registries.FEATURE) so it has a
 * stable id ("extrabiomes:bryce_pillars") and can be referenced from any biome's ConfiguredFeature -
 * mirrors {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures}.
 * Must be called once from the mod's main class, separately from the per-world-generation
 * ConfiguredFeature/PlacedFeature bootstrap in {@link BryceMesaPillarFeatures}.
 */
public class ModBrycePillarsFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ExtraBiomes.MOD_ID);

    public static final RegistryObject<BrycePillarsFeature> BRYCE_PILLARS =
            FEATURES.register("bryce_pillars", () -> new BrycePillarsFeature(BrycePillarsConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
