package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.SharedConstants;
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
        // Bedrock's query.vertical_speed is blocks per second, and the client only hears the server's velocity every few ticks, so measure it from the motion actually shown.
        state.verticalSpeed = (float) (entity.getY() - entity.yo) * SharedConstants.TICKS_PER_SECOND;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TreefrogRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/treefrog.png");
    }
}
