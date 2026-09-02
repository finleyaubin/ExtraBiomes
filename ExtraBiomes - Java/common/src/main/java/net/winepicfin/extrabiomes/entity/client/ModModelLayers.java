package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModModelLayers {
    public static final ModelLayerLocation WORM = layer("worm");
    public static final ModelLayerLocation TREEFROG = layer("treefrog");
    public static final ModelLayerLocation HOPPLESHROOM = layer("hoppleshroom");
    public static final ModelLayerLocation GIANT_TORTOISE = layer("giant_tortoise");
    public static final ModelLayerLocation JELLYFISH = layer("jellyfish");
    public static final ModelLayerLocation PIRANHA = layer("piranha");
    public static final ModelLayerLocation HARPY = layer("harpy");
    public static final ModelLayerLocation BAIT = layer("bait");

    // BoatRenderer (1.21.2+) derives its texture straight from this location's "model" ResourceLocation
    // path, as "textures/entity/<path>.png" - so the path segment here IS the texture path, and must
    // match an actual file under textures/entity/. The "layer" string only needs to be unique per
    // registered mesh (plain boat vs chest boat), it plays no part in texture resolution. Reusing the
    // same "boat/boat_<texture>" path for both the boat and chest boat mesh means both render with the
    // one texture we have per wood type (no separate chest-boat art yet).
    // GILDED_SKY has no boat art of its own yet - it deliberately points at the sky boat texture as a
    // placeholder ("gilded_main"/"gilded_chest" keep the layer keys distinct from SKY's own so the two
    // registrations don't collide). Swap to boatLayer("gilded_sky", ...) once boat_gilded_sky.png exists.
    public static final ModelLayerLocation MYSTIC_BOAT = boatLayer("mystic", "main");
    public static final ModelLayerLocation MYSTIC_CHEST_BOAT = boatLayer("mystic", "chest");
    public static final ModelLayerLocation PALM_BOAT = boatLayer("palm", "main");
    public static final ModelLayerLocation PALM_CHEST_BOAT = boatLayer("palm", "chest");
    public static final ModelLayerLocation SKY_BOAT = boatLayer("sky", "main");
    public static final ModelLayerLocation SKY_CHEST_BOAT = boatLayer("sky", "chest");
    public static final ModelLayerLocation GILDED_SKY_BOAT = boatLayer("sky", "gilded_main");
    public static final ModelLayerLocation GILDED_SKY_CHEST_BOAT = boatLayer("sky", "gilded_chest");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name), "main");
    }

    private static ModelLayerLocation boatLayer(String texture, String layer) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "boat/boat_" + texture), layer);
    }
}
