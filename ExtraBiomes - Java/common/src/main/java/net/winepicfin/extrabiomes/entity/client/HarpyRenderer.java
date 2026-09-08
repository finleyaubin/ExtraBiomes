package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.HarpyEntity;
import org.jetbrains.annotations.NotNull;

public class HarpyRenderer extends MobRenderer<HarpyEntity, LivingEntityRenderState, HarpyModel<LivingEntityRenderState>> {
    public HarpyRenderer(EntityRendererProvider.Context context) {
        super(context, new HarpyModel<>(context.bakeLayer(ModModelLayers.HARPY)), 0.5f);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(LivingEntityRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/harpy.png");
    }
}
