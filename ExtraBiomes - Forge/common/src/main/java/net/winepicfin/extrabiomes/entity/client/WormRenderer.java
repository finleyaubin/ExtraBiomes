package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.WormEntity;
import org.jetbrains.annotations.NotNull;

public class WormRenderer extends MobRenderer<WormEntity, WormModel<WormEntity>> {
    public WormRenderer(EntityRendererProvider.Context context) {
        super(context, new WormModel<>(context.bakeLayer(ModModelLayers.WORM)), 0.1f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WormEntity entity) {
        return new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/worm.png");
    }
}
