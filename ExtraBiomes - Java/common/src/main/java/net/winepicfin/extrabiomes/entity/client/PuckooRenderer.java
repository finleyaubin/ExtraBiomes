package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
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
    public void render(PuckooRenderState state, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight){
        if (state.isBaby) {
            pMatrixStack.scale(0.5f,0.5f,0.5f);
        }
        super.render(state, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PuckooRenderState state) {
        return switch (state.variant) {
            default -> ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_0.png");
            case BROWN -> ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_1.png");
            case PINK -> ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_2.png");
            case YELLOW -> ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_3.png");
        };
    }
}
