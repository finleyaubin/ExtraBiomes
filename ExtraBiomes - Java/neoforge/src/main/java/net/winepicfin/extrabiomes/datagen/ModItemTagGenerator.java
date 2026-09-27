package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, ExtraBiomes.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //this.tag(ItemTags.TRIMMABLE_ARMOR).add(ModItems.FROG_HELMET.get()); does now work with the gecolib model

        this.tag(ItemTags.WOODEN_FENCES).add(
                ModBlocks.MYSTIC_FENCE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_FENCE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_FENCE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_FENCE.get().asItem().builtInRegistryHolder().key()
        );

        this.tag(ItemTags.FENCE_GATES).add(
                ModBlocks.MYSTIC_FENCE_GATE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_FENCE_GATE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_FENCE_GATE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get().asItem().builtInRegistryHolder().key()
        );

        this.tag(ItemTags.LOGS).add(
                ModBlocks.MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_LOG.get().asItem().builtInRegistryHolder().key()
        );
        this.tag(ItemTags.LOGS_THAT_BURN).add(
                ModBlocks.MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_LOG.get().asItem().builtInRegistryHolder().key()
        );
        this.tag(ItemTags.PLANKS).add(
                ModBlocks.MYSTIC_PLANKS.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_PLANKS.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_PLANKS.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_PLANKS.get().asItem().builtInRegistryHolder().key()
        );

        this.tag(ItemTags.BOATS).add(ModItems.BOAT_ITEMS.stream().map(RegistrySupplier::getKey).toArray(ResourceKey[]::new));
        this.tag(ItemTags.CHEST_BOATS).add(ModItems.CHEST_BOAT_ITEMS.stream().map(RegistrySupplier::getKey).toArray(ResourceKey[]::new));
    }
}
