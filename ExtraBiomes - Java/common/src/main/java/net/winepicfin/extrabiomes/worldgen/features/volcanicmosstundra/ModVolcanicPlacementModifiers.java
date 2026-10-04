package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CavePillarFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CeilingLavafallFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CrevasseFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.GlacierFinishFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.IceEncasedLootFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.MeltwaterStreamFeature;

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

    public static final RegistrySupplier<MapCodec<CeilingLavafallFeature>> GLACIER_CEILING_LAVAFALL = FEATURES.register("glacier_ceiling_lavafall", () -> CeilingLavafallFeature.CODEC);
    public static final RegistrySupplier<MapCodec<IceEncasedLootFeature>> GLACIER_ICE_ENCASED_LOOT = FEATURES.register("glacier_ice_encased_loot", () -> IceEncasedLootFeature.CODEC);
    public static final RegistrySupplier<MapCodec<CrevasseFeature>> GLACIER_CREVASSE = FEATURES.register("glacier_crevasse", () -> CrevasseFeature.CODEC);
    public static final RegistrySupplier<MapCodec<GlacierFinishFeature>> GLACIER_FINISH = FEATURES.register("glacier_finish", () -> GlacierFinishFeature.CODEC);

    public static final RegistrySupplier<MapCodec<MeltwaterStreamFeature>> GLACIER_MELTWATER_STREAM = FEATURES.register("glacier_meltwater_stream", () -> MeltwaterStreamFeature.CODEC);

    public static final RegistrySupplier<MapCodec<CavePillarFeature>> GLACIER_CAVE_PILLAR = FEATURES.register("glacier_cave_pillar", () -> CavePillarFeature.CODEC);

    public static void register() {
        MODIFIERS.register();
        FEATURES.register();
    }
}
