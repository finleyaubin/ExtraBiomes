package net.winepicfin.extrabiomes.commondatagen;

import java.util.List;
import java.util.Set;

// Single source of truth for which subfolder under textures/block and textures/item a texture stem lives in.
public final class TexturePaths {
    private static final String STRIPPED = "stripped_";
    private static final List<String> WOOD_SETS = List.of("gilded_sky", "mystic", "palm", "sky");
    private static final Set<String> FOOD_ITEMS = Set.of("frogs_legs", "cooked_frogs_legs", "cooked_piranha", "jellyfish_jam_bottle");

    private TexturePaths() {
    }

    public static String block(String stem) {
        return "block/" + blockFolder(stem) + stem;
    }

    public static String item(String stem) {
        return "item/" + itemFolder(stem) + stem;
    }

    private static String blockFolder(String stem) {
        if (stem.contains("mushroom")) return "mushroom/";
        if (stem.contains("black_sand")) return "black_sand/";
        if (stem.startsWith("dense_cloud")) return "cloud/";
        if (stem.startsWith("nether_")) return "nether_ore/";
        if (stem.startsWith("grass_stone")) return "grass_stone/";
        String unstripped = stem.startsWith(STRIPPED) ? stem.substring(STRIPPED.length()) : stem;
        for (String wood : WOOD_SETS) {
            if (unstripped.startsWith(wood + "_")) return "wood/" + wood + "/";
        }
        return "";
    }

    private static String itemFolder(String stem) {
        if (stem.endsWith("_spawn_egg")) return "spawn_egg/";
        if (stem.startsWith("boat_")) return "boat/";
        if (stem.endsWith("_door") || stem.endsWith("_sign")) return "wood/";
        if (FOOD_ITEMS.contains(stem)) return "food/";
        if (stem.endsWith("razor_feather") || stem.startsWith("jellyfishing_net")) return "tool/";
        return "";
    }
}
