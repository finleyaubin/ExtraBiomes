package net.winepicfin.extrabiomes.entity.client.armour;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.neoforge.item.custom.FrogHelmetItem;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class FrogHelmetRenderer<R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<FrogHelmetItem, R> {
    public FrogHelmetRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "armour/frog_helmet")));
    }
}
