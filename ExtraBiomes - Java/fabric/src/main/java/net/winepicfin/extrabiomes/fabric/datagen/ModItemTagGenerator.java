package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Fabric port of forge/datagen/ModItemTagGenerator.java, using Fabric API's
// FabricTagProvider.ItemTagProvider (equivalent of Forge's ItemTagsProvider convenience class). Takes
// the sibling ModBlockTagGenerator so ItemTagProvider.copy(...) is available if ever needed, matching
// how Forge's version was constructed with the block tag provider's contentsGetter().
public class ModItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {

    public ModItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, ModBlockTagGenerator blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //this.valueLookupBuilder(ItemTags.TRIMMABLE_ARMOR).add(ModItems.FROG_HELMET.get()); does now work with the gecolib model

        this.valueLookupBuilder(ItemTags.FENCES).add(keys(
                ModBlocks.MYSTIC_FENCE.get().asItem(),
                ModBlocks.SKY_FENCE.get().asItem(),
                ModBlocks.PALM_FENCE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE.get().asItem()
        ));

        this.valueLookupBuilder(ItemTags.FENCE_GATES).add(keys(
                ModBlocks.MYSTIC_FENCE_GATE.get().asItem(),
                ModBlocks.SKY_FENCE_GATE.get().asItem(),
                ModBlocks.PALM_FENCE_GATE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get().asItem()
        ));

        this.valueLookupBuilder(ItemTags.LOGS).add(keys(
                ModBlocks.MYSTIC_LOG.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem(),
                ModBlocks.MYSTIC_WOOD.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem(),
                ModBlocks.SKY_LOG.get().asItem(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem(),
                ModBlocks.SKY_WOOD.get().asItem(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem(),
                ModBlocks.GILDED_SKY_LOG.get().asItem()
        ));
        this.valueLookupBuilder(ItemTags.LOGS_THAT_BURN).add(keys(
                ModBlocks.MYSTIC_LOG.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem(),
                ModBlocks.MYSTIC_WOOD.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem(),
                ModBlocks.SKY_LOG.get().asItem(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem(),
                ModBlocks.SKY_WOOD.get().asItem(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem(),
                ModBlocks.GILDED_SKY_LOG.get().asItem()
        ));
        this.valueLookupBuilder(ItemTags.PLANKS).add(keys(
                ModBlocks.MYSTIC_PLANKS.get().asItem(),
                ModBlocks.SKY_PLANKS.get().asItem(),
                ModBlocks.PALM_PLANKS.get().asItem(),
                ModBlocks.GILDED_SKY_PLANKS.get().asItem()
        ));

        this.valueLookupBuilder(ItemTags.BOATS).add(keys(ModItems.BOAT_ITEMS.stream().map(RegistrySupplier::get).toArray(net.minecraft.world.item.Item[]::new)));
        this.valueLookupBuilder(ItemTags.CHEST_BOATS).add(keys(ModItems.CHEST_BOAT_ITEMS.stream().map(RegistrySupplier::get).toArray(net.minecraft.world.item.Item[]::new)));
    }

    // valueLookupBuilder()'s TagAppender takes Item directly (Fabric API 1.21.6+), so this is now an identity passthrough.
    private static net.minecraft.world.item.Item[] keys(net.minecraft.world.item.Item... items) {
        return items;
    }
}
