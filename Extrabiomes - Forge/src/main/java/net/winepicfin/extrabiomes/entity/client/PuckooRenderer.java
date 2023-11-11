package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.layers.PuckooBaseModelLayers;
import net.winepicfin.extrabiomes.entity.client.layers.PuckooKoiLayer;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import org.jetbrains.annotations.NotNull;

public class PuckooRenderer extends MobRenderer<PuckooEntity, PuckooModel<PuckooEntity>> {
    public PuckooRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new PuckooModel<>(pContext.bakeLayer(PuckooBaseModelLayers.PUCKOO_BASE_LAYER)),0.5f);
        this.addLayer(new PuckooKoiLayer(this));

    }
    @Override
    public void render(PuckooEntity pEntity, float pEntityYaw, float pParticleTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight){
        if(pEntity.isBaby()){
            pMatrixStack.scale(0.5f,0.5f,0.5f);
        }
        super.render(pEntity, pEntityYaw, pParticleTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PuckooEntity entity) {
        return switch (entity.getVariant()) {
            default -> new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_0.png");
            case BROWN -> new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_1.png");
            case PINK -> new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_2.png");
            case YELLOW -> new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/puckoo/puckoo_base_3.png");
        };
    }
}
