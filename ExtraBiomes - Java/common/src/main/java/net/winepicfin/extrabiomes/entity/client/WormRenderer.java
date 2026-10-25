package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.state.WormRenderState;
import net.winepicfin.extrabiomes.entity.custom.WormEntity;
import org.jetbrains.annotations.NotNull;

public class WormRenderer extends MobRenderer<WormEntity, WormRenderState, WormModel<WormRenderState>> {
    public WormRenderer(EntityRendererProvider.Context context) {
        super(context, new WormModel<>(context.bakeLayer(ModModelLayers.WORM)), 0.1f);
    }

    @Override
    public WormRenderState createRenderState() {
        return new WormRenderState();
    }

    @Override
    public void extractRenderState(WormEntity entity, WormRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isArchie = entity.isArchie();
        state.isCiaran = entity.isCiaran();
    }

    @Override
    public @NotNull Identifier getTextureLocation(WormRenderState state) {
        return Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/worm.png");
    }
}
