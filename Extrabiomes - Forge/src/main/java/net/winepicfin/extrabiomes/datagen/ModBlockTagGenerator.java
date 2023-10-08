package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExtraBiomes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        this.tag(BlockTags.NEEDS_STONE_TOOL);

        this.tag(BlockTags.NEEDS_IRON_TOOL).add(
                ModBlocks.NETHER_DIAMOND_ORE.get()
        );

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.DENSE_CLOUD_BRICK.get(),
                ModBlocks.NETHER_DIAMOND_ORE.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                ModBlocks.MYSTIC_PLANKS.get(),
                ModBlocks.MYSTIC_STAIRS.get(),
                ModBlocks.MYSTIC_SLAB.get(),
                ModBlocks.MYSTIC_BUTTON.get(),
                ModBlocks.MYSTIC_BUTTON.get(),
                ModBlocks.MYSTIC_PRESSURE_PLATE.get(),
                ModBlocks.MYSTIC_FENCE.get(),
                ModBlocks.MYSTIC_FENCE_GATE.get(),
                ModBlocks.MYSTIC_DOOR.get(),
                ModBlocks.MYSTIC_TRAPDOOR.get(),
                ModBlocks.SKY_PLANKS.get(),
                ModBlocks.SKY_STAIRS.get(),
                ModBlocks.SKY_SLAB.get(),
                ModBlocks.SKY_BUTTON.get(),
                ModBlocks.SKY_BUTTON.get(),
                ModBlocks.SKY_PRESSURE_PLATE.get(),
                ModBlocks.SKY_FENCE.get(),
                ModBlocks.SKY_FENCE_GATE.get(),
                ModBlocks.SKY_DOOR.get(),
                ModBlocks.SKY_TRAPDOOR.get()

        );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL);
        this.tag(BlockTags.MINEABLE_WITH_HOE);
        this.tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(
                ModBlocks.DENSE_CLOUD.get()
        );

        this.tag(BlockTags.FENCES).add(
                ModBlocks.MYSTIC_FENCE.get(),
                ModBlocks.SKY_FENCE.get()
        );

        this.tag(BlockTags.FENCE_GATES).add(
                ModBlocks.MYSTIC_FENCE_GATE.get(),
                ModBlocks.SKY_FENCE_GATE.get()
        );

        this.tag(BlockTags.LOGS).add(
                ModBlocks.MYSTIC_LOG.get(),
                ModBlocks.STRIPED_MYSTIC_LOG.get(),
                ModBlocks.MYSTIC_WOOD.get(),
                ModBlocks.STRIPED_MYSTIC_WOOD.get(),
                ModBlocks.SKY_LOG.get(),
                ModBlocks.STRIPED_SKY_LOG.get(),
                ModBlocks.SKY_WOOD.get(),
                ModBlocks.STRIPED_SKY_WOOD.get()
        );

    }
}
