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
        // Curated (hand-authored, not dynamically inherited from BiomeTags.IS_FOREST/IS_JUNGLE or the
        // old ModTags.Biomes.IS_PLAINS) membership lists for select_boulder/select_stick_pile - see
        // ModBiomeModifiers' ADD_BOULDER/ADD_STICK_PILE. Those used to key off the broad vanilla tags
        // directly, so any third-party mod's biome carrying is_forest/is_jungle got these features
        // automatically - which repeatedly produced vanilla's "Feature order cycle found" crash once a
        // large enough modpack's cross-mod feature-ordering graph got a new shared edge stitched into
        // it (confirmed against Ars Elemental's flourishing_forest and Biomes We've Gone's
        // coconino_meadow/temperate_grove/ebony_woods - see git history). Hand-curating this list to
        // vanilla + this mod's own biomes only (the same membership the old tags already had at this
        // mod's own datagen time, before any third-party mod's dynamic tag contributions) keeps the
        // feature but removes ExtraBiomes as a contributor to that whole bug class - a third-party
        // biome now needs this mod's explicit knowledge, not just a shared vanilla tag, to get boulders
        // or stick piles.
        public static final TagKey<Biome> GETS_BOULDERS = tag("gets_boulders");
        public static final TagKey<Biome> GETS_STICK_PILES = tag("gets_stick_piles");
        // Biomes that get extrabiomes:swamp_huge_mushroom (vanilla Dark Forest plus this mod's swampy/forest biomes) and
        // biomes that get the mushroom-island huge mushroom and mycelium floor (the mushroom convention tag plus Fungle
        // Jungle). These features are delivered by a biome modifier in EVERY member biome, never baked into this mod's own
        // biome definitions. Mods such as Dynamic Trees and Wilder Wild append their own vegetal-step features to every
        // biome through modifiers that run before this mod's, so a baked-in copy sits before those features in our biome
        // while the modifier-delivered copy sits after them in vanilla's - opposite orders for the same pair, which
        // vanilla's FeatureSorter reports as "Feature order cycle found". Delivering it the same way everywhere gives
        // every biome the same relative order, whatever other mods append.
        public static final TagKey<Biome> GETS_SWAMP_HUGE_MUSHROOMS = tag("gets_swamp_huge_mushrooms");
        public static final TagKey<Biome> GETS_MUSHROOM_ISLAND_FEATURES = tag("gets_mushroom_island_features");
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
