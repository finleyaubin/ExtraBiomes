package net.winepicfin.extrabiomes.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModTags {
    public static class Blocks{
        public static TagKey<Block> tag(String name){
            return TagKey.create(Registries.BLOCK, new ResourceLocation(ExtraBiomes.MOD_ID, name));
        }
    }
    public static class Items{
        public static TagKey<Item> tag(String name){
            return TagKey.create(Registries.ITEM, new ResourceLocation(ExtraBiomes.MOD_ID, name));
        }
    }
    public static class Biomes{
        public static final TagKey<Biome> LUSH_MESA = tag("lush_mesa");
        public static final TagKey<Biome> MYSTIC_FOREST = tag("mystic_forest");
        // Vanilla has no BiomeTags.IS_PLAINS equivalent (checked via javap - only
        // HAS_VILLAGE_PLAINS exists), needed to port Bedrock's boulder_placer.json biome_filter
        // ("plains"/"forest"/"jungle") - see ModBiomeModifiers' ADD_BOULDER_PLAINS.
        public static final TagKey<Biome> IS_PLAINS = tag("is_plains");
        public static TagKey<Biome> tag(String name){
           return TagKey.create(Registries.BIOME, new ResourceLocation(ExtraBiomes.MOD_ID, name));
        }
    }

}
