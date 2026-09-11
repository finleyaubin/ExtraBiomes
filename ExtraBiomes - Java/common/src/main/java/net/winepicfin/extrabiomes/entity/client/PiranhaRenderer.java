package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.state.PiranhaRenderState;
import net.winepicfin.extrabiomes.entity.custom.PiranhaEntity;
import org.jetbrains.annotations.NotNull;

public class PiranhaRenderer extends MobRenderer<PiranhaEntity, PiranhaRenderState, PiranhaModel<PiranhaRenderState>> {
    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha.png"),
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha2.png"),
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha3.png"),
    };

    public PiranhaRenderer(EntityRendererProvider.Context context) {
        super(context, new PiranhaModel<>(context.bakeLayer(ModModelLayers.PIRANHA)), 0.2f);
    }

    @Override
    public PiranhaRenderState createRenderState() {
        return new PiranhaRenderState();
    }

    @Override
    public void extractRenderState(PiranhaEntity entity, PiranhaRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isBiting = entity.isBiting();
        state.variant = Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PiranhaRenderState state) {
        return TEXTURES[state.variant];
    }

    // Visual counterpart to PiranhaEntity#getDimensions' hitbox scale. LivingEntity#getScale() (and
    // this state's `scale` field) is now driven by Attributes.SCALE, which PiranhaEntity keeps in sync
    // with getSizeScale() - the base LivingEntityRenderer#scale is otherwise a no-op as of 1.21.2.
    @Override
    protected void scale(PiranhaRenderState state, PoseStack poseStack) {
        float s = state.scale;
        poseStack.scale(s, s, s);
    }

    // Mirrors vanilla CodRenderer: rolls the body out of water in the renderer rather than the model, matching Bedrock's animation.piranha.flop.
    @Override
    protected void setupRotations(PiranhaRenderState state, PoseStack poseStack, float bodyRot, float scale) {
        super.setupRotations(state, poseStack, bodyRot, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(4.3F * Mth.sin(0.6F * state.ageInTicks)));
        if (!state.isInWater) {
            poseStack.translate(0.1F, 0.1F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
