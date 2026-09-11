package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.state.TreefrogRenderState;
import net.winepicfin.extrabiomes.entity.custom.TreefrogEntity;
import org.jetbrains.annotations.NotNull;

public class TreefrogRenderer extends MobRenderer<TreefrogEntity, TreefrogRenderState, TreefrogModel<TreefrogRenderState>> {
    public TreefrogRenderer(EntityRendererProvider.Context context) {
        super(context, new TreefrogModel<>(context.bakeLayer(ModModelLayers.TREEFROG)), 0.25f);
    }

    @Override
    public TreefrogRenderState createRenderState() {
        return new TreefrogRenderState();
    }

    @Override
    public void extractRenderState(TreefrogEntity entity, TreefrogRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.verticalSpeed = (float) entity.getDeltaMovement().y;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TreefrogRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/treefrog.png");
    }
}
