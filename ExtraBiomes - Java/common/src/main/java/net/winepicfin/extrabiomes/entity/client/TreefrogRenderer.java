package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.TreefrogEntity;
import org.jetbrains.annotations.NotNull;

public class TreefrogRenderer extends MobRenderer<TreefrogEntity, TreefrogModel<TreefrogEntity>> {
    public TreefrogRenderer(EntityRendererProvider.Context context) {
        super(context, new TreefrogModel<>(context.bakeLayer(ModModelLayers.TREEFROG)), 0.25f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TreefrogEntity entity) {
        return new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/treefrog.png");
    }
}
