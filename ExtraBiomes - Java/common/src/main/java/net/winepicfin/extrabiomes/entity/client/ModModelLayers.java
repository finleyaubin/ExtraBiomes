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
    // registered mesh (plain boat vs chest boat), it plays no part in texture resolution.
    //
    // Every wood type gets its own editable file under textures/entity/boat/ (plain) and
    // textures/entity/chest_boat/ (chest) - mystic/palm/sky's plain-boat file is the real art ported
    // from the Bedrock module; every chest-boat file, and gilded_sky's plain-boat file, are placeholder
    // copies of vanilla's own boat/chest_boat oak.png (there's no dedicated or Bedrock-ported chest boat
    // art at all, and no Bedrock gilded_sky art) ready to be repainted.
    public static final ModelLayerLocation MYSTIC_BOAT = boatLayer("mystic", "main");
    public static final ModelLayerLocation MYSTIC_CHEST_BOAT = chestBoatLayer("mystic", "chest");
    public static final ModelLayerLocation PALM_BOAT = boatLayer("palm", "main");
    public static final ModelLayerLocation PALM_CHEST_BOAT = chestBoatLayer("palm", "chest");
    public static final ModelLayerLocation SKY_BOAT = boatLayer("sky", "main");
    public static final ModelLayerLocation SKY_CHEST_BOAT = chestBoatLayer("sky", "chest");
    public static final ModelLayerLocation GILDED_SKY_BOAT = boatLayer("gilded_sky", "main");
    public static final ModelLayerLocation GILDED_SKY_CHEST_BOAT = chestBoatLayer("gilded_sky", "chest");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name), "main");
    }

    private static ModelLayerLocation boatLayer(String texture, String layer) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "boat/boat_" + texture), layer);
    }

    private static ModelLayerLocation chestBoatLayer(String texture, String layer) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "chest_boat/boat_" + texture), layer);
    }
}
