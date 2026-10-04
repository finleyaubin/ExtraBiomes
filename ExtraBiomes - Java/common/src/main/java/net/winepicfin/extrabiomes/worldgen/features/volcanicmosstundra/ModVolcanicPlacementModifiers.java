package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CavePillarFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CeilingLavafallFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CrevasseFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.GlacierFinishFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.IceEncasedLootFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.MeltwaterStreamFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CavePillarFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CeilingLavafallFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.CrevasseFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.GlacierFinishFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.IceEncasedLootFeature;
import net.winepicfin.extrabiomes.worldgen.features.glacier.MeltwaterStreamFeature;
import net.winepicfin.extrabiomes.worldgen.placement.ChunkOriginSnap;
import net.winepicfin.extrabiomes.worldgen.placement.InBiomeChunkSample;
import net.winepicfin.extrabiomes.worldgen.placement.LakeSafeOrigin;

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
    public static final RegistrySupplier<PlacementModifierType<InBiomeChunkSample>> IN_BIOME_CHUNK_SAMPLE = MODIFIERS.register("in_biome_chunk_sample", () -> () -> InBiomeChunkSample.CODEC);
    public static final RegistrySupplier<PlacementModifierType<LakeSafeOrigin>> LAKE_SAFE_ORIGIN = MODIFIERS.register("lake_safe_origin", () -> () -> LakeSafeOrigin.CODEC);
    public static final RegistrySupplier<PlacementModifierType<ChunkOriginSnap>> CHUNK_ORIGIN_SNAP = MODIFIERS.register("chunk_origin_snap", () -> () -> ChunkOriginSnap.CODEC);

    public static final RegistrySupplier<BasaltBankFeature> BASALT_BANK = FEATURES.register("basalt_bank", () -> new BasaltBankFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<LavaFlowKickstartFeature> LAVA_FLOW_KICKSTART = FEATURES.register("lava_flow_kickstart", () -> new LavaFlowKickstartFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistrySupplier<CeilingLavafallFeature> GLACIER_CEILING_LAVAFALL = FEATURES.register("glacier_ceiling_lavafall", () -> new CeilingLavafallFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<IceEncasedLootFeature> GLACIER_ICE_ENCASED_LOOT = FEATURES.register("glacier_ice_encased_loot", () -> new IceEncasedLootFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<CrevasseFeature> GLACIER_CREVASSE = FEATURES.register("glacier_crevasse", () -> new CrevasseFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<GlacierFinishFeature> GLACIER_FINISH = FEATURES.register("glacier_finish", () -> new GlacierFinishFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<MeltwaterStreamFeature> GLACIER_MELTWATER_STREAM = FEATURES.register("glacier_meltwater_stream", () -> new MeltwaterStreamFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistrySupplier<CavePillarFeature> GLACIER_CAVE_PILLAR = FEATURES.register("glacier_cave_pillar", () -> new CavePillarFeature(CavePillarFeature.Configuration.CODEC));




    public static void register() {
        MODIFIERS.register();
        FEATURES.register();
    }
}
