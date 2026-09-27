package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.layers.PuckooBaseModelLayers;
import net.winepicfin.extrabiomes.entity.client.layers.PuckooKoiLayer;
import net.winepicfin.extrabiomes.entity.client.layers.PuckooSaddleLayer;
import net.winepicfin.extrabiomes.entity.client.state.PuckooRenderState;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import org.jetbrains.annotations.NotNull;

public class PuckooRenderer extends MobRenderer<PuckooEntity, PuckooRenderState, PuckooModel<PuckooRenderState>> {
    public PuckooRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new PuckooModel<>(pContext.bakeLayer(PuckooBaseModelLayers.PUCKOO_BASE_LAYER)),0.5f);
        this.addLayer(new PuckooKoiLayer(this));
        this.addLayer(new PuckooSaddleLayer(this));
    }

    @Override
    public PuckooRenderState createRenderState() {
        return new PuckooRenderState();
    }

    @Override
    public void extractRenderState(PuckooEntity entity, PuckooRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.variant = entity.getVariant();
        state.markings = entity.getMarkings();
        state.isSaddled = entity.isSaddled();
    }

    @Override
    public void submit(PuckooRenderState state, PoseStack pMatrixStack, SubmitNodeCollector pSubmitNodeCollector, CameraRenderState pCameraRenderState) {
        if (state.isBaby) {
            pMatrixStack.scale(0.5f,0.5f,0.5f);
        }
        super.submit(state, pMatrixStack, pSubmitNodeCollector, pCameraRenderState);
    }

    @Override
    public @NotNull Identifier getTextureLocation(PuckooRenderState state) {
        return switch (state.variant) {
            default -> Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_0.png");
            case BROWN -> Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_1.png");
            case PINK -> Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_2.png");
            case YELLOW -> Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_3.png");
        };
    }
}
