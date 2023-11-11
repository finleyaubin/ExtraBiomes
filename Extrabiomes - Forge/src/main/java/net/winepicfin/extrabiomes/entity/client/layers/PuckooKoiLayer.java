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
    private static final Map<PuckooKoiMarkings, ResourceLocation> LOCATION_BY_MARKINGS = Util.make(Maps.newEnumMap(PuckooKoiMarkings.class), (p_114874_) -> {
        p_114874_.put(PuckooKoiMarkings.BLANK, new ResourceLocation(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi0.png"));
        p_114874_.put(PuckooKoiMarkings.RED, new ResourceLocation(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi1.png"));
        p_114874_.put(PuckooKoiMarkings.FULL_ORANGE, new ResourceLocation(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi2.png"));
        p_114874_.put(PuckooKoiMarkings.SEMI_ORANGE, new ResourceLocation(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi3.png"));
    });

    public PuckooKoiLayer(RenderLayerParent<PuckooEntity, PuckooModel<PuckooEntity>> entityPuckooModelRenderLayerParent) {
        super(entityPuckooModelRenderLayerParent);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PuckooEntity entity) {
        return LOCATION_BY_MARKINGS.get(entity.getMarkings());
    }

    @Override
    public void render(@NotNull PoseStack p_117349_, @NotNull MultiBufferSource p_117350_, int p_117351_, PuckooEntity p_117352_, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {
        ResourceLocation resourcelocation = LOCATION_BY_MARKINGS.get(p_117352_.getMarkings());
        if (resourcelocation != null && !p_117352_.isInvisible()) {
            VertexConsumer vertexconsumer = p_117350_.getBuffer(RenderType.entityTranslucent(resourcelocation));
            this.getParentModel().renderToBuffer(p_117349_, vertexconsumer, p_117351_, LivingEntityRenderer.getOverlayCoords(p_117352_, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
