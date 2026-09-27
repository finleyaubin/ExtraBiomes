package net.winepicfin.extrabiomes.fabric.entity.client.armour;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.fabric.item.custom.FrogHelmetItem;
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
                             PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, float partialTick) {
        R geoState = (R) humanoidState;
        fillRenderState((FrogHelmetItem) stack.getItem(), new RenderData(stack, EquipmentSlot.HEAD, wolf, baseModel), geoState, partialTick);
        // GeckoLib reads light from the vanilla lightCoords field, not its PACKED_LIGHT ticket
        humanoidState.lightCoords = packedLight;
        // cameraState only reaches GeoRenderLayers/pre-post-render hooks, none of which this
        // renderer uses, so an empty one is fine here - RenderLayer#submit isn't handed a real one.
        submitRenderTasks(geoState, poseStack, submitNodeCollector, new CameraRenderState(), null);
    }
}
