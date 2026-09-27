package net.winepicfin.extrabiomes.entity.client.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.PuckooModel;
import net.winepicfin.extrabiomes.entity.client.state.PuckooRenderState;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooKoiMarkings;

import java.util.Map;

public class PuckooKoiLayer extends RenderLayer<PuckooRenderState, PuckooModel<PuckooRenderState>> {
    private static final Map<PuckooKoiMarkings, Identifier> LOCATION_BY_MARKINGS = Util.make(Maps.newEnumMap(PuckooKoiMarkings.class), (map) -> {
        map.put(PuckooKoiMarkings.BLANK, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi0.png"));
        map.put(PuckooKoiMarkings.RED, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi1.png"));
        map.put(PuckooKoiMarkings.FULL_ORANGE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi2.png"));
        map.put(PuckooKoiMarkings.SEMI_ORANGE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"textures/entity/puckoo/koi3.png"));
    });

    public PuckooKoiLayer(RenderLayerParent<PuckooRenderState, PuckooModel<PuckooRenderState>> entityPuckooModelRenderLayerParent) {
        super(entityPuckooModelRenderLayerParent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, PuckooRenderState state, float limbSwing, float limbSwingAmount) {
        Identifier resourcelocation = LOCATION_BY_MARKINGS.get(state.markings);
        if (resourcelocation != null && !state.isInvisible) {
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            submitNodeCollector.order(1).submitModel(this.getParentModel(), state, poseStack, RenderTypes.entityTranslucent(resourcelocation), packedLight, overlay, state.outlineColor, null);
        }
    }
}
