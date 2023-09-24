package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.cert.Extension;
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

        this.tag(BlockTags.MINEABLE_WITH_AXE);
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL);
        this.tag(BlockTags.MINEABLE_WITH_HOE);
        this.tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(
                ModBlocks.DENSE_CLOUD.get()
        );

        this.tag(Tags.Blocks.FENCES).add(
                ModBlocks.MYSTIC_FENCE.get()
        );

        this.tag(Tags.Blocks.FENCE_GATES).add(
                ModBlocks.MYSTIC_FENCE_GATE.get()
        );

    }
}
