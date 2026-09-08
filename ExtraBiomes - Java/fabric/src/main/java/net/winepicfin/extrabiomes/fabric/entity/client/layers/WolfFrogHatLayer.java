package net.winepicfin.extrabiomes.fabric.entity.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.fabric.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.fabric.entity.client.state.WolfRenderStateExtension;
import net.winepicfin.extrabiomes.fabric.mixin.WolfModelAccessor;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

// Fabric equivalent of forge/.../forge/entity/client/layers/WolfFrogHatLayer.java - same rendering
// approach, but reads WolfModel's private "head" field via the WolfModelAccessor mixin instead of
// Forge's access-transformer-widened field.
//
// 1.21.2+ split entity rendering into an immutable per-frame WolfRenderState snapshot; render()
// no longer receives the Wolf entity, its partial tick, or its head yaw/pitch directly. headItem
// and isInvisibleToPlayer come from the (Living)EntityRenderState, yRot/xRot on the state are the
// old netHeadYaw/headPitch, and the Wolf entity itself is recovered via WolfRenderStateExtension
// (populated by WolfRendererMixin) since GeoArmorRenderer#prepForRender still needs a live Entity.
public class WolfFrogHatLayer extends RenderLayer<WolfRenderState, WolfModel> {
    private FrogHelmetRenderer renderer;
    private HumanoidModel<?> baseModel;

    public WolfFrogHatLayer(RenderLayerParent<WolfRenderState, WolfModel> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull WolfRenderState state,
                        float limbSwing, float limbSwingAmount) {
        ItemStack headItem = state.headItem;
        if (headItem.getItem() != ModItems.FROG_HELMET.get() || state.isInvisibleToPlayer) return;

        Wolf wolf = ((WolfRenderStateExtension) state).extrabiomes$getWolf();
        if (wolf == null) return;

        if (this.renderer == null)
            this.renderer = new FrogHelmetRenderer();
        if (this.baseModel == null)
            this.baseModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER));

        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);

        poseStack.pushPose();
        ((WolfModelAccessor) this.getParentModel()).extrabiomes$getHead().translateAndRotate(poseStack);
        poseStack.translate(0.05D, -0.6D, -0.02D);
        poseStack.scale(1F, 1F, 1F);
        poseStack.mulPose(Axis.XP.rotationDegrees(0.0F));
        this.renderer.prepForRender(wolf, headItem, EquipmentSlot.HEAD, this.baseModel, buffer, partialTick, state.yRot, state.xRot);
        this.renderer.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
        poseStack.popPose();
    }
}
