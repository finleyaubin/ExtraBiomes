package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.entity.custom.projectile.RazorFeatherProjectileEntity;

// Reimplements ThrownItemRenderer's render (which only billboards, no roll) with an added spin, matching Bedrock's animation.razor_feather.throw.
public class RazorFeatherRenderer<T extends RazorFeatherProjectileEntity> extends EntityRenderer<T, ThrownItemRenderState> {
    private static final float SPIN_DEGREES_PER_TICK = 45.0F;

    private final ItemRenderer itemRenderer;

    public RazorFeatherRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    @Override
    public void extractRenderState(T entity, ThrownItemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        ItemStack stack = entity.getItem();
        state.item = stack.copy();
        state.itemModel = stack.isEmpty() ? null : this.itemRenderer.getModel(stack, entity.level(), null, entity.getId());
    }

    @Override
    public void render(ThrownItemRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.ageInTicks * SPIN_DEGREES_PER_TICK));
        if (state.itemModel != null) {
            this.itemRenderer.render(state.item, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight,
                    OverlayTexture.NO_OVERLAY, state.itemModel);
        }
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }
}
