package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.advancements.ModAdvancements;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// NeoForge 21.4 reshaped GatherDataEvent: it's now abstract, fired separately as
// GatherDataEvent.Server / GatherDataEvent.Client instead of one event carrying
// includeServer()/includeClient() booleans, addProvider(T) no longer takes a boolean gate (each
// subtype only fires when that half of datagen is actually running), and getExistingFileHelper()
// is gone along with the ExistingFileHelper class itself (see ModBlockStateProvider's header
// comment) - confirmed via javap on neoforge-21.4.157-universal.jar's GatherDataEvent.class.
@EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // BiomeTagsProvider validates every tag entry against this lookup, but the plain
        // event.getLookupProvider() future doesn't include our datapack-registered biomes (those only
        // exist via ModWorldGenProvider.BUILDER's own registry patch) - so tagging our own biomes would
        // fail with "missing following references". Patch the lookup with our biome/feature registries
        // before handing it to the biome tag provider.
        //
        // RegistrySetBuilder#buildPatch() gained a required Cloner.Factory arg as of 1.20.4 (and its
        // return type changed from HolderLookup.Provider to a PatchedRegistries record) - vanilla's own
        // RegistryPatchGenerator.createLookup() helper does this "patch an existing lookup with a
        // RegistrySetBuilder" pattern without needing to build that Factory by hand (see forge/'s
        // identical DataGenerators.java fix for the same issue).
        CompletableFuture<HolderLookup.Provider> biomeTagLookupProvider = RegistryPatchGenerator.createLookup(lookupProvider, ModWorldGenProvider.BUILDER)
                .thenApply(RegistrySetBuilder.PatchedRegistries::full);

        event.addProvider(new ModRecipeProvider(packOutput, lookupProvider));
        event.addProvider(ModLootTableProvider.create(packOutput, lookupProvider));

        ModBlockTagGenerator blockTagGenerator = event.addProvider(new ModBlockTagGenerator(packOutput, lookupProvider));
        event.addProvider(new ModBiomeTagProvider(packOutput, biomeTagLookupProvider));
        event.addProvider(new ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter()));

        event.addProvider(new ModWorldGenProvider(packOutput, lookupProvider));

        // ModAdvancements resolves biome Holders via registries.lookupOrThrow(Registries.BIOME) (the
        // 1.20.6 LocationPredicate.Builder.inBiome(Holder<Biome>) rework, replacing the old
        // setBiome(ResourceKey<Biome>) overload that needed no registry lookup) - same
        // datapack-registered-biomes gap as ModBiomeTagProvider above, so this needs the patched
        // lookup too, not the plain one.
        event.addProvider(new AdvancementProvider(packOutput, biomeTagLookupProvider,
                List.of(new ModAdvancements())));
    }

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        event.addProvider(new ModBlockStateProvider(packOutput));
        event.addProvider(new ModItemModelProvider(packOutput));
    }
}
