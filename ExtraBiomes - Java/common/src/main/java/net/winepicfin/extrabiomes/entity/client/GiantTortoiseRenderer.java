package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.state.GiantTortoiseRenderState;
import net.winepicfin.extrabiomes.entity.custom.GiantTortoiseEntity;
import org.jetbrains.annotations.NotNull;

public class GiantTortoiseRenderer extends MobRenderer<GiantTortoiseEntity, GiantTortoiseRenderState, GiantTortoiseModel<GiantTortoiseRenderState>> {
    public GiantTortoiseRenderer(EntityRendererProvider.Context context) {
        super(context, new GiantTortoiseModel<>(context.bakeLayer(ModModelLayers.GIANT_TORTOISE)), 0.9f);
    }

    @Override
    public GiantTortoiseRenderState createRenderState() {
        return new GiantTortoiseRenderState();
    }

    @Override
    public void extractRenderState(GiantTortoiseEntity entity, GiantTortoiseRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isCharging = entity.isCharging();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(GiantTortoiseRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/giant_tortoise.png");
    }
}
