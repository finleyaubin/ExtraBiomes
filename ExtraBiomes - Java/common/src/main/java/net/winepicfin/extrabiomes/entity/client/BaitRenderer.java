package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;
import org.jetbrains.annotations.NotNull;

// Ported from Bedrock's controller.render.bait: swaps between 10 damage-stage textures as piranhas chip the bait away.
public class BaitRenderer extends EntityRenderer<BaitProjectileEntity> {
    private static final ResourceLocation[] TEXTURES = {
            texture("bait10"), texture("bait20"), texture("bait30"), texture("bait40"), texture("bait50"),
            texture("bait60"), texture("bait70"), texture("bait80"), texture("bait90"), texture("bait90"),
    };

    private final BaitModel<BaitProjectileEntity> model;

    public BaitRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BaitModel<>(context.bakeLayer(ModModelLayers.BAIT));
        this.shadowRadius = 0.15F;
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(ExtraBiomes.MOD_ID, "textures/entity/" + name + ".png");
    }

    @Override
    public void render(BaitProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                        MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        // The model's root part uses the humanoid PartPose.offset(0, 24, 0) convention, which LivingEntityRenderer normally un-flips; this renderer has no such base class, so the flip has to happen here.
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.25D, 0.0D);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(getTextureLocation(entity)));
        this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTicks, 0.0F, 0.0F);
        // Not a LivingEntity, so the usual automatic red hurt tint doesn't apply - drive it off BaitProjectileEntity's own hurtTime instead.
        int overlay = OverlayTexture.pack(OverlayTexture.NO_WHITE_U, entity.getHurtTime() > 0);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BaitProjectileEntity entity) {
        int index = Mth.clamp(entity.getHealth() * TEXTURES.length / entity.getMaxHealth(), 0, TEXTURES.length - 1);
        return TEXTURES[index];
    }
}
