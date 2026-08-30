package net.winepicfin.extrabiomes.fabric.entity.client.armour;

import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.fabric.item.custom.FrogHelmetItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class FrogHelmetRenderer extends GeoArmorRenderer<FrogHelmetItem> {
    public FrogHelmetRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "armour/frog_helmet")));
    }
}
