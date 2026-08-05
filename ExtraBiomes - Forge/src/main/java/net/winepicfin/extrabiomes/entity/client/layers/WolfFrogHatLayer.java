package net.winepicfin.extrabiomes.entity.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class WolfFrogHatLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
    public WolfFrogHatLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull Wolf wolf,
                        float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack headItem = wolf.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.getItem() != ModItems.FROG_HELMET.get() || wolf.isInvisible()) return;

        poseStack.pushPose();
        this.getParentModel().head.translateAndRotate(poseStack);
        poseStack.translate(0.0D, -0.35D, -0.02D);
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        Minecraft.getInstance().getItemRenderer().renderStatic(headItem, ItemDisplayContext.HEAD, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, wolf.level(), 0);
        poseStack.popPose();
    }
}
