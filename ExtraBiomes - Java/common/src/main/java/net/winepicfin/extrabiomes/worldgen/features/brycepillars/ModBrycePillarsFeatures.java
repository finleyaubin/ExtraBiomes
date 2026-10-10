package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Registers the {@link BrycePillarsFeature} Feature TYPE (its {@code MapCodec}, in
 * Registries.FEATURE_TYPE) so it has a stable id ("extrabiomes:bryce_pillars") and can be
 * referenced from any biome's Feature - mirrors
 * {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures}.
 * Must be called once from the mod's main class, separately from the per-world-generation
 * Feature/PlacedFeature bootstrap in {@link BryceMesaPillarFeatures}.
 */
public class ModBrycePillarsFeatures {
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE_TYPE);

    public static final RegistrySupplier<MapCodec<BrycePillarsFeature>> BRYCE_PILLARS =
            FEATURES.register("bryce_pillars", () -> BrycePillarsFeature.CODEC);

    public static void register() {
        FEATURES.register();
    }
}
