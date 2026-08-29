package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.advancements.ModAdvancements;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // BiomeTagsProvider validates every tag entry against this lookup, but the plain
        // event.getLookupProvider() future doesn't include our datapack-registered biomes (those only
        // exist via ModWorldGenProvider.BUILDER's own registry patch) - so tagging our own biomes would
        // fail with "missing following references". Patch the lookup with our biome/feature registries
        // before handing it to the biome tag provider.
        //
        // RegistrySetBuilder#buildPatch() gained a required Cloner.Factory arg as of 1.20.4 (and its
        // return type changed from HolderLookup.Provider to a PatchedRegistries record) - rather than
        // build that Factory by hand, RegistryPatchGenerator.createLookup() is vanilla's own helper for
        // exactly this "patch an existing lookup with a RegistrySetBuilder" pattern (used by Forge's own
        // DatapackBuiltinEntriesProvider the same way).
        CompletableFuture<HolderLookup.Provider> biomeTagLookupProvider = RegistryPatchGenerator.createLookup(lookupProvider, ModWorldGenProvider.BUILDER)
                .thenApply(RegistrySetBuilder.PatchedRegistries::full);

        generator.addProvider(event.includeServer(),new ModRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(),ModLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(),new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(),new ModItemModelProvider(packOutput, existingFileHelper));

        ModBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(), new ModBlockTagGenerator(packOutput,lookupProvider, existingFileHelper));
        ModBiomeTagProvider biomeTagGenerator =generator.addProvider(event.includeServer(), new ModBiomeTagProvider(packOutput,biomeTagLookupProvider,existingFileHelper));
        generator.addProvider(event.includeClient(),new ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(),new ModWorldGenProvider(packOutput,lookupProvider));

        generator.addProvider(event.includeServer(), new AdvancementProvider(packOutput, lookupProvider,
                List.of(new ModAdvancements())));
    }
}
