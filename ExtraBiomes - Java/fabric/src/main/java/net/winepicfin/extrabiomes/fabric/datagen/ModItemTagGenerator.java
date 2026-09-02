package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
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
public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {

    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, ModBlockTagGenerator blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //this.tag(ItemTags.TRIMMABLE_ARMOR).add(ModItems.FROG_HELMET.get()); does now work with the gecolib model

        this.tag(ItemTags.FENCES).add(keys(
                ModBlocks.MYSTIC_FENCE.get().asItem(),
                ModBlocks.SKY_FENCE.get().asItem(),
                ModBlocks.PALM_FENCE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE.get().asItem()
        ));

        this.tag(ItemTags.FENCE_GATES).add(keys(
                ModBlocks.MYSTIC_FENCE_GATE.get().asItem(),
                ModBlocks.SKY_FENCE_GATE.get().asItem(),
                ModBlocks.PALM_FENCE_GATE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get().asItem()
        ));

        this.tag(ItemTags.LOGS).add(keys(
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
        this.tag(ItemTags.LOGS_THAT_BURN).add(keys(
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
        this.tag(ItemTags.PLANKS).add(keys(
                ModBlocks.MYSTIC_PLANKS.get().asItem(),
                ModBlocks.SKY_PLANKS.get().asItem(),
                ModBlocks.PALM_PLANKS.get().asItem(),
                ModBlocks.GILDED_SKY_PLANKS.get().asItem()
        ));

        this.tag(ItemTags.BOATS).add(keys(ModItems.BOAT_ITEMS.stream().map(RegistrySupplier::get).toArray(net.minecraft.world.item.Item[]::new)));
        this.tag(ItemTags.CHEST_BOATS).add(keys(ModItems.CHEST_BOAT_ITEMS.stream().map(RegistrySupplier::get).toArray(net.minecraft.world.item.Item[]::new)));
    }

    @SafeVarargs
    private static net.minecraft.resources.ResourceKey<net.minecraft.world.item.Item>[] keys(net.minecraft.world.item.Item... items) {
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.Item>[] result = new net.minecraft.resources.ResourceKey[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = net.minecraft.core.registries.BuiltInRegistries.ITEM.getResourceKey(items[i]).orElseThrow();
        }
        return result;
    }
}
