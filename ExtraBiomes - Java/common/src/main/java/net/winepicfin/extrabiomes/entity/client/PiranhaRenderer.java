package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.PiranhaEntity;
import org.jetbrains.annotations.NotNull;

public class PiranhaRenderer extends MobRenderer<PiranhaEntity, PiranhaModel<PiranhaEntity>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha.png"),
            new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha2.png"),
            new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha3.png"),
    };

    public PiranhaRenderer(EntityRendererProvider.Context context) {
        super(context, new PiranhaModel<>(context.bakeLayer(ModModelLayers.PIRANHA)), 0.2f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PiranhaEntity entity) {
        return TEXTURES[Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1)];
    }

    // Visual counterpart to PiranhaEntity#getDimensions' hitbox scale.
    @Override
    protected void scale(PiranhaEntity entity, PoseStack poseStack, float partialTickTime) {
        super.scale(entity, poseStack, partialTickTime);
        float s = entity.getSizeScale();
        poseStack.scale(s, s, s);
    }

    // Bedrock's animation.piranha.flop rolls the body by variable.zrot when out of water; vanilla
    // CodRenderer does the same thing here rather than in the model, so this mirrors it.
    @Override
    protected void setupRotations(PiranhaEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw,
                                  float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(4.3F * Mth.sin(0.6F * ageInTicks)));
        if (!entity.isInWater()) {
            poseStack.translate(0.1F, 0.1F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
