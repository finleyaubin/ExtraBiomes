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

    public static final ModelLayerLocation MYSTIC_BOAT = layer("mystic_boat");
    public static final ModelLayerLocation MYSTIC_CHEST_BOAT = layer("mystic_chest_boat");
    public static final ModelLayerLocation PALM_BOAT = layer("palm_boat");
    public static final ModelLayerLocation PALM_CHEST_BOAT = layer("palm_chest_boat");
    public static final ModelLayerLocation SKY_BOAT = layer("sky_boat");
    public static final ModelLayerLocation SKY_CHEST_BOAT = layer("sky_chest_boat");
    public static final ModelLayerLocation GILDED_SKY_BOAT = layer("gilded_sky_boat");
    public static final ModelLayerLocation GILDED_SKY_CHEST_BOAT = layer("gilded_sky_chest_boat");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(ExtraBiomes.MOD_ID, name), "main");
    }
}
