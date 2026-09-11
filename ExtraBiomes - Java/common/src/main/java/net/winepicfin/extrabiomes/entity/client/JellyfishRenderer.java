package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.state.JellyfishRenderState;
import net.winepicfin.extrabiomes.entity.custom.JellyfishEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JellyfishRenderer extends MobRenderer<JellyfishEntity, JellyfishRenderState, JellyfishModel<JellyfishRenderState>> {
    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/jellyfish.png"),
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/jellyfish1.png"),
    };

    public JellyfishRenderer(EntityRendererProvider.Context context) {
        super(context, new JellyfishModel<>(context.bakeLayer(ModModelLayers.JELLYFISH)), 0.4f);
    }

    @Override
    public JellyfishRenderState createRenderState() {
        return new JellyfishRenderState();
    }

    @Override
    public void extractRenderState(JellyfishEntity entity, JellyfishRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.bodyScaleY = entity.getBodyScaleY();
        state.grayAmount = entity.getGrayAmount();
        state.variant = Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1);
    }

    @Override
    protected void scale(JellyfishRenderState state, PoseStack poseStack) {
        poseStack.scale(1.2f, 1.2f, 1.2f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(JellyfishRenderState state) {
        return TEXTURES[state.variant];
    }

    // The gray-out-of-water desaturation used to happen in JellyfishModel#renderToBuffer, which is now
    // final on the base Model class - the equivalent hook moved to the renderer's overall model tint.
    @Override
    protected int getModelTint(JellyfishRenderState state) {
        float gray = state.grayAmount;
        float r = 1.0f + (0.5f - 1.0f) * gray;
        float g = 1.0f + (0.5f - 1.0f) * gray;
        float b = 1.0f + (0.5f - 1.0f) * gray;
        return ARGB.colorFromFloat(1.0f, r, g, b);
    }

    @Override
    @Nullable
    protected RenderType getRenderType(JellyfishRenderState state, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(getTextureLocation(state));
    }
}
