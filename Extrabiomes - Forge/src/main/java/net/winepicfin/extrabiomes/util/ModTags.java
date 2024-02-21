package net.winepicfin.extrabiomes.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModTags {
    public static class Blocks{
        public static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(ExtraBiomes.MOD_ID, name));        }
    }
    public static class Items{
        public static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(ExtraBiomes.MOD_ID, name));        }
    }
    public static class Biomes{
        public static final TagKey<Biome> LUSH_MESA = tag("lush_mesa");
        public static TagKey<Biome> tag(String name){
           return TagKey.create(Registries.BIOME, new ResourceLocation(ExtraBiomes.MOD_ID,name));
        }
    }

}
