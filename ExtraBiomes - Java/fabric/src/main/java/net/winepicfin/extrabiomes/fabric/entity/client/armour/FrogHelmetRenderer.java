package net.winepicfin.extrabiomes.fabric.entity.client.armour;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.fabric.item.custom.FrogHelmetItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib mixes GeoRenderState into HumanoidRenderState at runtime, so the bound can't be named concretely.
public class FrogHelmetRenderer<R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<FrogHelmetItem, R> {
    public FrogHelmetRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "armour/frog_helmet")));
    }

    // Wolves have no HumanoidRenderState of their own, so the layer lends one to drive the GeoArmorRenderer.
    @SuppressWarnings("unchecked")
    public void renderOnWolf(HumanoidRenderState humanoidState, Wolf wolf, ItemStack stack, HumanoidModel<?> baseModel,
                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        R geoState = (R) humanoidState;
        fillRenderState((FrogHelmetItem) stack.getItem(), new RenderData(stack, EquipmentSlot.HEAD, wolf), geoState, partialTick);
        geoState.addGeckolibData(DataTickets.HUMANOID_MODEL, baseModel);
        geoState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);
        defaultRender(geoState, poseStack, bufferSource, null, null);
    }
}
