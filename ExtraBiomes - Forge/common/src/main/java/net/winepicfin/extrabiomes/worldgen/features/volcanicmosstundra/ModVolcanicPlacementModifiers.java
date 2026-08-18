package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Registers the custom {@link PlacementModifierType}s this subsystem's two hand-rolled
 * {@link net.minecraft.world.level.levelgen.placement.PlacementModifier}s need to have a stable
 * id (Registries.PLACEMENT_MODIFIER_TYPE), plus {@link BasaltBankFeature}'s own {@code Feature}
 * type (Registries.FEATURE) - same pattern as
 * {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures}
 * registering SingleStructureFeature's Feature type. Must be called once from the mod's main class.
 */
public class ModVolcanicPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> MODIFIERS = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.PLACEMENT_MODIFIER_TYPE);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<PlacementModifierType<RiverNoiseFilter>> RIVER_NOISE_FILTER = MODIFIERS.register("river_noise_filter", () -> () -> RiverNoiseFilter.CODEC);
    public static final RegistrySupplier<PlacementModifierType<MinYFilter>> MIN_Y_FILTER = MODIFIERS.register("min_y_filter", () -> () -> MinYFilter.CODEC);

    public static final RegistrySupplier<BasaltBankFeature> BASALT_BANK = FEATURES.register("basalt_bank", () -> new BasaltBankFeature(NoneFeatureConfiguration.CODEC));

    public static void register() {
        MODIFIERS.register();
        FEATURES.register();
    }
}
