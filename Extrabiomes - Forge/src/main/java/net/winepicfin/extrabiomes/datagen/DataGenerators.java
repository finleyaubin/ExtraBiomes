package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.Cloner;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.ModBiomeModifiers;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfigureFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.BIOME, ModBiomes::boostrap);
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        generator.addProvider(event.includeServer(),new ModRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(),ModLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(),new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(),new ModItemModelProvider(packOutput, existingFileHelper));

        ModBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(), new ModBlockTagGenerator(packOutput,lookupProvider, existingFileHelper));
        ModBiomeTagProvider biomeTagGenerator =generator.addProvider(event.includeServer(), new ModBiomeTagProvider(packOutput,lookupProvider,existingFileHelper));
        generator.addProvider(event.includeClient(),new ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(), new RegistriesDatapackGenerator(packOutput, event.getLookupProvider().thenApply(r -> constructRegistries(r, BUILDER)), Set.of(ExtraBiomes.MOD_ID)));
    }
    private static HolderLookup.Provider constructRegistries(HolderLookup.Provider lookup, RegistrySetBuilder Builder)
    {
        Cloner.Factory clonerFactory = new Cloner.Factory();
        var builderKeys = new HashSet<>(Builder.getEntryKeys());
        RegistryDataLoader.WORLDGEN_REGISTRIES.stream().forEach(data -> {
            if (!builderKeys.contains(data.key()))
                Builder.add(data.key(), context -> {});

            data.runWithArguments(clonerFactory::addCodec);
        });

        return Builder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), lookup, clonerFactory).patches();

    }
}
