package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;

// Model layer locations for the entities ported from Bedrock.
public class ModModelLayers {
    public static final ModelLayerLocation WORM = layer("worm");
    public static final ModelLayerLocation TREEFROG = layer("treefrog");
    public static final ModelLayerLocation HOPPLESHROOM = layer("hoppleshroom");
    public static final ModelLayerLocation GIANT_TORTOISE = layer("giant_tortoise");
    public static final ModelLayerLocation JELLYFISH = layer("jellyfish");
    public static final ModelLayerLocation PIRANHA = layer("piranha");
    public static final ModelLayerLocation HARPY = layer("harpy");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name), "main");
    }
}
