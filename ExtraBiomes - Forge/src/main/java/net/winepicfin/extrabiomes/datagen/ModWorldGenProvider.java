package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.ModBiomeModifiers;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.OasisPuddleFeature;
import net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.moss.MossFeatures;
import net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures;
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

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries,BUILDER, Set.of(ExtraBiomes.MOD_ID));
    }
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfigureFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, OasisPuddleFeature::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, OasisPuddleFeature::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, BoulderFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, BoulderFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, MushroomFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MushroomFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, MossFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MossFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, MoorlandFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MoorlandFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, GlacierFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, GlacierFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, NetherlandsOreFeatures::bootstrapConfigured)
            .add(Registries.CONFIGURED_FEATURE, NetherlandsTulipFeatures::bootstrapConfigured)
            .add(Registries.CONFIGURED_FEATURE, NetherlandsWheatFeatures::bootstrapConfigured)
            .add(Registries.CONFIGURED_FEATURE, NetherlandsWaterFeature::bootstrapConfigured)
            .add(Registries.CONFIGURED_FEATURE, NetherlandsWindmillFeature::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, NetherlandsOreFeatures::bootstrapPlaced)
            .add(Registries.PLACED_FEATURE, NetherlandsTulipFeatures::bootstrapPlaced)
            .add(Registries.PLACED_FEATURE, NetherlandsWheatFeatures::bootstrapPlaced)
            .add(Registries.PLACED_FEATURE, NetherlandsWaterFeature::bootstrapPlaced)
            .add(Registries.PLACED_FEATURE, NetherlandsWindmillFeature::bootstrapPlaced)
            .add(Registries.CONFIGURED_CARVER, NetherlandsCaveCarver::bootstrapCarver)
            .add(Registries.CONFIGURED_FEATURE, JellyCoralFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, JellyCoralFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, StonePillarsFeature::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, StonePillarsFeature::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, TaigaSpikeFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, TaigaSpikeFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, MesaFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MesaFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, UndergroundJungleFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, UndergroundJungleFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, CharredForestFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, CharredForestFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, FutureTreeFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, FutureTreeFeatures::bootstrapPlaced)
            .add(Registries.CONFIGURED_FEATURE, ShatteredSwampFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, ShatteredSwampFeatures::bootstrapPlaced)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.BIOME, ModBiomes::boostrap);
}
