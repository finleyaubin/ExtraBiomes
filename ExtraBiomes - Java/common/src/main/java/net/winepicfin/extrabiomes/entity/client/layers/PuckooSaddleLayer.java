package net.winepicfin.extrabiomes.entity.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, PuckooRenderState state, float limbSwing, float limbSwingAmount) {
        if (state.isSaddled && !state.isInvisible) {
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F), ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
        }
    }
}
