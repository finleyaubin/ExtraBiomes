package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.HarpyEntity;
import org.jetbrains.annotations.NotNull;

public class HarpyRenderer extends MobRenderer<HarpyEntity, HarpyModel<HarpyEntity>> {
    public HarpyRenderer(EntityRendererProvider.Context context) {
        super(context, new HarpyModel<>(context.bakeLayer(ModModelLayers.HARPY)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HarpyEntity entity) {
        return new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/harpy.png");
    }
}
