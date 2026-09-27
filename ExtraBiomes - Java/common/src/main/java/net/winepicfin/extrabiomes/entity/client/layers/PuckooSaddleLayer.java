package net.winepicfin.extrabiomes.entity.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.PuckooModel;
import net.winepicfin.extrabiomes.entity.client.state.PuckooRenderState;

public class PuckooSaddleLayer extends RenderLayer<PuckooRenderState, PuckooModel<PuckooRenderState>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/saddle.png");

    public PuckooSaddleLayer(RenderLayerParent<PuckooRenderState, PuckooModel<PuckooRenderState>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, PuckooRenderState state, float limbSwing, float limbSwingAmount) {
        if (state.isSaddled && !state.isInvisible) {
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            submitNodeCollector.submitCustomGeometry(poseStack, RenderType.entityTranslucent(TEXTURE), (pose, vertexConsumer) -> {
                PoseStack modelPoseStack = new PoseStack();
                modelPoseStack.last().set(pose);
                this.getParentModel().renderToBuffer(modelPoseStack, vertexConsumer, packedLight, overlay, ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
            });
        }
    }
}
