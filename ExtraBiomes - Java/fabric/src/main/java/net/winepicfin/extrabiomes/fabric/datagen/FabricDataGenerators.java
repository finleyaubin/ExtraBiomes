package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.winepicfin.extrabiomes.advancements.ModAdvancements;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.surface.ModNoiseParameters;
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
import net.winepicfin.extrabiomes.worldgen.features.jellycoral.JellyCoralFeatures;
import net.winepicfin.extrabiomes.worldgen.features.stonepillars.StonePillarsFeature;
import net.winepicfin.extrabiomes.worldgen.features.taigaspike.TaigaSpikeFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.features.charred.CharredForestFeatures;
import net.winepicfin.extrabiomes.worldgen.features.future.FutureTreeFeatures;
import net.winepicfin.extrabiomes.worldgen.features.shatteredswamp.ShatteredSwampFeatures;
import net.winepicfin.extrabiomes.worldgen.features.tropical.TropicalIslandFeatures;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BryceMesaPillarFeatures;
import net.winepicfin.extrabiomes.worldgen.features.palm.PalmTreeFeatures;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.VolcanicMossTundraFeatures;

// Fabric-datagen entry point (registered under the "fabric-datagen" key in fabric.mod.json), the
// equivalent of forge/datagen/DataGenerators.java's @SubscribeEvent gatherData method. Registers the
// same set of DataProviders forge's runData does (see fabric/datagen/*), and additionally builds the
// dynamic registry entries (biomes/features/carvers/noise) that forge's ModWorldGenProvider.BUILDER
// static field provides on that loader - here that's done via buildRegistry(RegistrySetBuilder), which
// is Fabric API's dedicated hook for datapack-backed registry bootstrap during datagen (see
// DataGeneratorEntrypoint#buildRegistry). ForgeRegistries.Keys.BIOME_MODIFIERS is intentionally not
// mirrored - see ModDynamicRegistryProvider's class javadoc.
public class FabricDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModLootTableProvider::create);

        ModBlockTagGenerator blockTagGenerator = pack.addProvider(ModBlockTagGenerator::new);
        pack.addProvider(ModBiomeTagProvider::new);
        pack.addProvider((output, registriesFuture) -> new ModItemTagGenerator(output, registriesFuture, blockTagGenerator));

        pack.addProvider(ModDynamicRegistryProvider::new);

        pack.addProvider(ModBlockStateProvider::new);
        pack.addProvider(ModItemModelProvider::new);

        pack.addProvider((output, registriesFuture) -> new AdvancementProvider(output, registriesFuture, java.util.List.of(new ModAdvancements())));
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        // NOTE: RegistrySetBuilder.add() must only be called ONCE per registry - see the matching
        // comment on forge/datagen/ModWorldGenProvider.BUILDER for why every bootstrap method for a
        // given registry is chained into a single lambda below instead of calling .add() per feature.
        registryBuilder
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
                    UndergroundJungleFeatures.bootstrapConfigured(context);
                    CharredForestFeatures.bootstrapConfigured(context);
                    FutureTreeFeatures.bootstrapConfigured(context);
                    ShatteredSwampFeatures.bootstrapConfigured(context);
                    TropicalIslandFeatures.bootstrapConfigured(context);
                    BryceMesaPillarFeatures.bootstrapConfigured(context);
                    PalmTreeFeatures.bootstrapConfigured(context);
                    VolcanicMossTundraFeatures.bootstrapConfigured(context);
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
                    UndergroundJungleFeatures.bootstrapPlaced(context);
                    CharredForestFeatures.bootstrapPlaced(context);
                    FutureTreeFeatures.bootstrapPlaced(context);
                    ShatteredSwampFeatures.bootstrapPlaced(context);
                    TropicalIslandFeatures.bootstrapPlaced(context);
                    BryceMesaPillarFeatures.bootstrapPlaced(context);
                    VolcanicMossTundraFeatures.bootstrapPlaced(context);
                })
                .add(Registries.NOISE, ModNoiseParameters::bootstrap)
                .add(Registries.BIOME, ModBiomes::boostrap);
    }
}
