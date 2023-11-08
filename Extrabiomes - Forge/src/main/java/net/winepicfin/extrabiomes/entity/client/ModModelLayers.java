package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModModelLayers {
    public static final ModelLayerLocation PUCKOO_BASE_LAYER= new ModelLayerLocation(
            new ResourceLocation(ExtraBiomes.MOD_ID,"puckoo_base_layer"), "main");
    public static final ModelLayerLocation PUCKOO_KOI_LAYER= new ModelLayerLocation(
            new ResourceLocation(ExtraBiomes.MOD_ID,"puckoo_koi_layer"), "main");
    public static final ModelLayerLocation PUCKOO_SADDLE_LAYER= new ModelLayerLocation(
            new ResourceLocation(ExtraBiomes.MOD_ID,"puckoo_saddle_layer"), "main");
}
