package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;

public class PuckooRenderer extends MobRenderer<PuckooEntity, PuckooModel<PuckooEntity>> {

    public PuckooRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new PuckooModel<>(pContext.bakeLayer(ModModelLayers.PUCKOO_LAYER)),0.5f);
    }
    @Override
    public void render(PuckooEntity pEntity, float pEntityYaw, float pParticleTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight){
        if(pEntity.isBaby()){
            pMatrixStack.scale(0.5f,0.5f,0.5f);
        }
        super.render(pEntity, pEntityYaw, pParticleTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PuckooEntity p_114482_) {
        return new ResourceLocation(ExtraBiomes.MOD_ID,"textures/entity/puckoo/puckoo_base.png");
    }
}
