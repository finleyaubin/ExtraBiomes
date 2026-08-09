package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.surface.ModNoiseParameters;
import net.winepicfin.extrabiomes.worldgen.ModBiomeModifiers;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.OasisPuddleFeature;
import net.winepicfin.extrabiomes.worldgen.features.oasis.OasisFossilFeatures;
import net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.moss.MossFeatures;
import net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mystic.MysticFeatures;
import net.winepicfin.extrabiomes.worldgen.features.glacier.GlacierFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsOreFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsTulipFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWheatFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWaterFeature;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWindmillFeature;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsCaveCarver;
import net.winepicfin.extrabiomes.worldgen.features.jellycoral.JellyCoralFeatures;
import net.winepicfin.extrabiomes.worldgen.features.stonepillars.StonePillarsFeature;
import net.winepicfin.extrabiomes.worldgen.features.taigaspike.TaigaSpikeFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mesa.MesaFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.features.charred.CharredForestFeatures;
import net.winepicfin.extrabiomes.worldgen.features.future.FutureTreeFeatures;
import net.winepicfin.extrabiomes.worldgen.features.shatteredswamp.ShatteredSwampFeatures;
import net.winepicfin.extrabiomes.worldgen.features.tropical.TropicalIslandFeatures;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BryceMesaPillarFeatures;
import net.winepicfin.extrabiomes.worldgen.features.palm.PalmTreeFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries,BUILDER, Set.of(ExtraBiomes.MOD_ID));
    }
    // NOTE: RegistrySetBuilder.add() must only be called ONCE per registry - calling it repeatedly for
    // the same registry (as this used to, ~17 times each for CONFIGURED_FEATURE/PLACED_FEATURE) makes
    // RegistrySetBuilder.createState() insert that registry key more than once into its internal
    // per-registry lookup map, which throws "Multiple entries with same key: minecraft:worldgen/..."
    // the moment any datagen provider touches these registries (e.g. runData's biome tag provider).
    // Each registry therefore gets exactly one .add() call below, with all of this mod's bootstrap
    // methods for that registry chained together inside a single lambda.
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, context -> {
                ModConfigureFeatures.bootstrap(context);
                OasisPuddleFeature.bootstrapConfigured(context);
                OasisFossilFeatures.bootstrapConfigured(context);
                BoulderFeatures.bootstrapConfigured(context);
                MushroomFeatures.bootstrapConfigured(context);
                MossFeatures.bootstrapConfigured(context);
                MoorlandFeatures.bootstrapConfigured(context);
                MysticFeatures.bootstrapConfigured(context);
                GlacierFeatures.bootstrapConfigured(context);
                NetherlandsOreFeatures.bootstrapConfigured(context);
                NetherlandsTulipFeatures.bootstrapConfigured(context);
                NetherlandsWheatFeatures.bootstrapConfigured(context);
                NetherlandsWaterFeature.bootstrapConfigured(context);
                NetherlandsWindmillFeature.bootstrapConfigured(context);
                JellyCoralFeatures.bootstrapConfigured(context);
                StonePillarsFeature.bootstrapConfigured(context);
                TaigaSpikeFeatures.bootstrapConfigured(context);
                MesaFeatures.bootstrapConfigured(context);
                UndergroundJungleFeatures.bootstrapConfigured(context);
                CharredForestFeatures.bootstrapConfigured(context);
                FutureTreeFeatures.bootstrapConfigured(context);
                ShatteredSwampFeatures.bootstrapConfigured(context);
                TropicalIslandFeatures.bootstrapConfigured(context);
                BryceMesaPillarFeatures.bootstrapConfigured(context);
                PalmTreeFeatures.bootstrapConfigured(context);
            })
            .add(Registries.PLACED_FEATURE, context -> {
                ModPlacedFeatures.bootstrap(context);
                OasisPuddleFeature.bootstrapPlaced(context);
                OasisFossilFeatures.bootstrapPlaced(context);
                BoulderFeatures.bootstrapPlaced(context);
                MushroomFeatures.bootstrapPlaced(context);
                MossFeatures.bootstrapPlaced(context);
                MoorlandFeatures.bootstrapPlaced(context);
                MysticFeatures.bootstrapPlaced(context);
                GlacierFeatures.bootstrapPlaced(context);
                NetherlandsOreFeatures.bootstrapPlaced(context);
                NetherlandsTulipFeatures.bootstrapPlaced(context);
                NetherlandsWheatFeatures.bootstrapPlaced(context);
                NetherlandsWaterFeature.bootstrapPlaced(context);
                NetherlandsWindmillFeature.bootstrapPlaced(context);
                JellyCoralFeatures.bootstrapPlaced(context);
                StonePillarsFeature.bootstrapPlaced(context);
                TaigaSpikeFeatures.bootstrapPlaced(context);
                MesaFeatures.bootstrapPlaced(context);
                UndergroundJungleFeatures.bootstrapPlaced(context);
                CharredForestFeatures.bootstrapPlaced(context);
                FutureTreeFeatures.bootstrapPlaced(context);
                ShatteredSwampFeatures.bootstrapPlaced(context);
                TropicalIslandFeatures.bootstrapPlaced(context);
                BryceMesaPillarFeatures.bootstrapPlaced(context);
            })
            .add(Registries.CONFIGURED_CARVER, NetherlandsCaveCarver::bootstrapCarver)
            .add(Registries.NOISE, ModNoiseParameters::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.BIOME, ModBiomes::boostrap);
}
