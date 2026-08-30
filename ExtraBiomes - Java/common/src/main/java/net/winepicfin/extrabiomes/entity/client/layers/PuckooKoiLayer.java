package net.winepicfin.extrabiomes.entity.client.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.PuckooModel;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooKoiMarkings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PuckooKoiLayer extends RenderLayer<PuckooEntity,PuckooModel<PuckooEntity>> {
    private static final Map<PuckooKoiMarkings, ResourceLocation> LOCATION_BY_MARKINGS = Util.make(Maps.newEnumMap(PuckooKoiMarkings.class), (map) -> {
        map.put(PuckooKoiMarkings.BLANK, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi0.png"));
        map.put(PuckooKoiMarkings.RED, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi1.png"));
        map.put(PuckooKoiMarkings.FULL_ORANGE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi2.png"));
        map.put(PuckooKoiMarkings.SEMI_ORANGE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi3.png"));
    });

    public PuckooKoiLayer(RenderLayerParent<PuckooEntity, PuckooModel<PuckooEntity>> entityPuckooModelRenderLayerParent) {
        super(entityPuckooModelRenderLayerParent);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PuckooEntity entity) {
        return LOCATION_BY_MARKINGS.get(entity.getMarkings());
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, PuckooEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation resourcelocation = LOCATION_BY_MARKINGS.get(entity.getMarkings());
        if (resourcelocation != null && !entity.isInvisible()) {
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(resourcelocation));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), net.minecraft.util.FastColor.ARGB32.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
        }
    }
}
