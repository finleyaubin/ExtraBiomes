package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

// Vanilla's own BoatRenderer builds a Boat.Type -> (texture, model) map at construction time, keyed
// off the closed vanilla enum - unusable for a custom EntityType. This bakes one fixed texture/model
// pair instead (this mod never sets Boat.Type away from its default), otherwise a straight copy of
// vanilla's render() body.
public class ModBoatRenderer extends EntityRenderer<Boat> {
    private final ResourceLocation texture;
    private final ListModel<Boat> model;

    @SuppressWarnings("unchecked")
    public ModBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, ResourceLocation texture, boolean chestBoat) {
        super(context);
        this.shadowRadius = 0.8F;
        this.texture = texture;
        ModelPart part = context.bakeLayer(layer);
        this.model = (ListModel<Boat>) (chestBoat ? new ChestBoatModel(part) : new BoatModel(part));
    }

    @Override
    public ResourceLocation getTextureLocation(Boat boat) {
        return texture;
    }

    @Override
    public void render(Boat boat, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int light) {
        pose.pushPose();
        pose.translate(0.0F, 0.375F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        float hurtTime = boat.getHurtTime() - partialTick;
        float damage = Math.max(boat.getDamage() - partialTick, 0.0F);
        if (hurtTime > 0.0F) {
            pose.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * boat.getHurtDir()));
        }

        float bubbleAngle = boat.getBubbleAngle(partialTick);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            pose.mulPose(new org.joml.Quaternionf().setAngleAxis(bubbleAngle * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
        }

        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F));
        model.setupAnim(boat, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);
        var vertexConsumer = buffer.getBuffer(model.renderType(texture));
        model.renderToBuffer(pose, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!boat.isUnderWater() && model instanceof WaterPatchModel waterPatchModel) {
            var waterConsumer = buffer.getBuffer(RenderType.waterMask());
            waterPatchModel.waterPatch().render(pose, waterConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        pose.popPose();
        super.render(boat, yaw, partialTick, pose, buffer, light);
    }
}
