package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.GiantTortoiseEntity;
import org.jetbrains.annotations.NotNull;

public class GiantTortoiseRenderer extends MobRenderer<GiantTortoiseEntity, GiantTortoiseModel<GiantTortoiseEntity>> {
    public GiantTortoiseRenderer(EntityRendererProvider.Context context) {
        super(context, new GiantTortoiseModel<>(context.bakeLayer(ModModelLayers.GIANT_TORTOISE)), 0.9f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(GiantTortoiseEntity entity) {
        return new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/giant_tortoise.png");
    }
}
