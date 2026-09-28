package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.concurrent.CompletableFuture;

// NeoForge 21.4 reshaped GatherDataEvent: it's now abstract, fired separately as
// GatherDataEvent.Server / GatherDataEvent.Client instead of one event carrying
// includeServer()/includeClient() booleans, addProvider(T) no longer takes a boolean gate (each
// subtype only fires when that half of datagen is actually running), and getExistingFileHelper()
// is gone along with the ExistingFileHelper class itself (see ModBlockStateProvider's header
// comment) - confirmed via javap on neoforge-21.4.157-universal.jar's GatherDataEvent.class.
//
// As of 26.3, recipes/advancements/loot tables are themselves full BootstrapContext-backed
// registries (Registries.RECIPE/ADVANCEMENT/LOOT_TABLE) rather than DataProviders, wired in via
// event.createWorldRegistryObjects()/createReloadableRegistryObjects() instead of addProvider() -
// see ModWorldGenProvider and ModRecipeProvider for the RegistrySetBuilders themselves.
@EventBusSubscriber(modid = ExtraBiomes.MOD_ID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event){
        // World registries (biomes, features, placed features, structures, noise, biome modifiers).
        // This also patches event.getWorldLookupProvider() with our own entries, so the tag
        // providers below see our custom biomes without any manual RegistryPatchGenerator step.
        event.createWorldRegistryObjects(ModWorldGenProvider.BUILDER);

        // Reloadable registries: recipes + their unlock advancements (see ModRecipeProvider for why
        // they must share one MultiRegistryBootstrap) and loot tables.
        RegistrySetBuilder reloadableBuilder = new RegistrySetBuilder()
                .add(ModRecipeProvider.BOOTSTRAP)
                .add(Registries.LOOT_TABLE, ModLootTableProvider.create(event.getWorldLookupProvider()));
        event.createReloadableRegistryObjects(reloadableBuilder);

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();
        var packOutput = event.getGenerator().getPackOutput();
        event.addProvider(new ModBlockTagGenerator(packOutput, lookupProvider));
        event.addProvider(new ModBiomeTagProvider(packOutput, lookupProvider));
        event.addProvider(new ModItemTagGenerator(packOutput, lookupProvider));
    }

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event){
        var packOutput = event.getGenerator().getPackOutput();
        event.addProvider(new ModBlockStateProvider(packOutput));
        event.addProvider(new ModItemModelProvider(packOutput));
    }
}
