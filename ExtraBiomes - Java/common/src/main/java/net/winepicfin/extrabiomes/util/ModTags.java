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
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
        }
    }
    public static class Items{
        // Mirrors vanilla's ItemTags.REPAIRS_LEATHER_ARMOR-style convention: ArmorMaterial's repair
        // ingredient became a TagKey<Item> as of 1.21.2, replacing the old Ingredient.of(...) supplier.
        public static final TagKey<Item> REPAIRS_FROG_ARMOR = tag("repairs_frog_armor");

        public static TagKey<Item> tag(String name){
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
        }
    }
    public static class Biomes{
        public static final TagKey<Biome> LUSH_MESA = tag("lush_mesa");
        public static final TagKey<Biome> MYSTIC_FOREST = tag("mystic_forest");
        // Vanilla has no BiomeTags.IS_PLAINS equivalent (only HAS_VILLAGE_PLAINS); needed to port Bedrock's boulder_placer.json biome_filter - see ModBiomeModifiers' ADD_BOULDER_PLAINS.
        public static final TagKey<Biome> IS_PLAINS = tag("is_plains");
        // Piranha's swamp spawn reads forge:is_swamp / c:swamp directly (see ModBiomeModifiers/
        // FabricBiomeModifiers) - those cross-loader convention tags already cover any third-party
        // mod's swamp biome, so there's no need for our own equivalent. This tag is just that
        // convention tag plus Moorlands (boggy but not a true swamp, so not worth contributing to
        // the shared convention itself) - see ModBiomeModifiers' ADD_SPAWN_TREEFROG_SWAMP.
        public static final TagKey<Biome> IS_WETLAND = tag("is_wetland");
        // Named after the spawn it gates, matching vanilla's own SPAWNS_SNOW_FOXES/SPAWNS_GOLD_RABBITS/etc
        // convention. Composed from forge:is_mushroom / c:mushroom (so a third-party mushroom biome is
        // covered automatically) plus Crimson/Warped Forest, which aren't part of that convention.
        public static final TagKey<Biome> SPAWNS_HOPPLESHROOM = tag("spawns_hoppleshroom");
        public static final TagKey<Biome> SPAWNS_JELLYFISH = tag("spawns_jellyfish");
        public static TagKey<Biome> tag(String name){
           return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
        }
    }

}
