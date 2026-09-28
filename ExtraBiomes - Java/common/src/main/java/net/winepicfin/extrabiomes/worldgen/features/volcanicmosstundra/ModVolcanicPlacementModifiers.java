package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
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
    public static final DeferredRegister<MapCodec<? extends PlacementModifier>> MODIFIERS = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.PLACEMENT_MODIFIER_TYPE);
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE_TYPE);

    public static final RegistrySupplier<MapCodec<RiverNoiseFilter>> RIVER_NOISE_FILTER = MODIFIERS.register("river_noise_filter", () -> RiverNoiseFilter.CODEC);
    public static final RegistrySupplier<MapCodec<MinYFilter>> MIN_Y_FILTER = MODIFIERS.register("min_y_filter", () -> MinYFilter.CODEC);

    public static final RegistrySupplier<MapCodec<BasaltBankFeature>> BASALT_BANK = FEATURES.register("basalt_bank", () -> BasaltBankFeature.CODEC);
    public static final RegistrySupplier<MapCodec<LavaFlowKickstartFeature>> LAVA_FLOW_KICKSTART = FEATURES.register("lava_flow_kickstart", () -> LavaFlowKickstartFeature.CODEC);

    public static void register() {
        MODIFIERS.register();
        FEATURES.register();
    }
}
