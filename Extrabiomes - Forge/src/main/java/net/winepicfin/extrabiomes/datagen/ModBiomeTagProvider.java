package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.biomes.ModBiomes;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends BiomeTagsProvider {
    public ModBiomeTagProvider(PackOutput p_255800_, CompletableFuture<HolderLookup.Provider> p_256205_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_255800_, p_256205_, ExtraBiomes.MOD_ID, existingFileHelper);
    }
    protected void addTags(HolderLookup.Provider p_256485_) {
        this.tag(BiomeTags.HAS_MINESHAFT_MESA).add(ModBiomes.CHARRED_FOREST);
    }
}
