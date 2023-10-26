package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, ExtraBiomes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //this.tag(ItemTags.TRIMMABLE_ARMOR).add(ModItems.FROG_HELMET.get()); does now work with the gecolib model

        this.tag(ItemTags.FENCES).add(
                ModBlocks.MYSTIC_FENCE.get().asItem(),
                ModBlocks.SKY_FENCE.get().asItem(),
                ModBlocks.PALM_FENCE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE.get().asItem()
        );

        this.tag(ItemTags.FENCE_GATES).add(
                ModBlocks.MYSTIC_FENCE_GATE.get().asItem(),
                ModBlocks.SKY_FENCE_GATE.get().asItem(),
                ModBlocks.PALM_FENCE_GATE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get().asItem()
        );

        this.tag(ItemTags.LOGS).add(
                ModBlocks.MYSTIC_LOG.get().asItem(),
                ModBlocks.STRIPED_MYSTIC_LOG.get().asItem(),
                ModBlocks.MYSTIC_WOOD.get().asItem(),
                ModBlocks.STRIPED_MYSTIC_WOOD.get().asItem(),
                ModBlocks.SKY_LOG.get().asItem(),
                ModBlocks.STRIPED_SKY_LOG.get().asItem(),
                ModBlocks.SKY_WOOD.get().asItem(),
                ModBlocks.STRIPED_SKY_WOOD.get().asItem(),
                ModBlocks.GILDED_SKY_LOG.get().asItem()
        );
        this.tag(ItemTags.LOGS_THAT_BURN).add(
                ModBlocks.MYSTIC_LOG.get().asItem(),
                ModBlocks.STRIPED_MYSTIC_LOG.get().asItem(),
                ModBlocks.MYSTIC_WOOD.get().asItem(),
                ModBlocks.STRIPED_MYSTIC_WOOD.get().asItem(),
                ModBlocks.SKY_LOG.get().asItem(),
                ModBlocks.STRIPED_SKY_LOG.get().asItem(),
                ModBlocks.SKY_WOOD.get().asItem(),
                ModBlocks.STRIPED_SKY_WOOD.get().asItem(),
                ModBlocks.GILDED_SKY_LOG.get().asItem()
        );
        this.tag(ItemTags.PLANKS).add(
                ModBlocks.MYSTIC_PLANKS.get().asItem(),
                ModBlocks.SKY_PLANKS.get().asItem(),
                ModBlocks.PALM_PLANKS.get().asItem(),
                ModBlocks.GILDED_SKY_PLANKS.get().asItem()
        );
    }
}
