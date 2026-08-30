package net.winepicfin.extrabiomes.entity.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.PuckooModel;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import org.jetbrains.annotations.NotNull;

public class PuckooSaddleLayer extends RenderLayer<PuckooEntity, PuckooModel<PuckooEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/saddle.png");

    public PuckooSaddleLayer(RenderLayerParent<PuckooEntity, PuckooModel<PuckooEntity>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PuckooEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, PuckooEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isSaddled() && !entity.isInvisible()) {
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), net.minecraft.util.FastColor.ARGB32.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
        }
    }
}
