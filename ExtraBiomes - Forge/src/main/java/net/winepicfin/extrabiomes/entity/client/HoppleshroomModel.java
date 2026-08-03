package net.winepicfin.extrabiomes.entity.client;
// Generated from hoppleshroom.geo.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.winepicfin.extrabiomes.entity.custom.HoppleshroomEntity;

public class HoppleshroomModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart modelRoot;
	private final ModelPart leg;
	private final ModelPart hat;

	public HoppleshroomModel(ModelPart root) {
		this.modelRoot = root;
		this.leg = root.getChild("leg");
		this.hat = root.getChild("hat");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition leg = partdefinition.addOrReplaceChild("leg", CubeListBuilder.create().texOffs(0, 15).addBox(-3.5F, 0.0F, -3.5F, 7.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));
		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -4.0F, -5.5F, 11.0F, 4.0F, 11.0F, new CubeDeformation(0.0F)).texOffs(21, 23).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float vspeed = (float) entity.getDeltaMovement().y;
		float squish = (entity instanceof HoppleshroomEntity hoppleshroom) ? hoppleshroom.squish : 0f;

		// animation.hoppleshroom.jump, plus a landing squash/stretch on top driven by `squish`
		this.leg.xScale = (1f - ((0.08f * (((vspeed < 0f) ? 0f : vspeed))))) + squish * 0.3f;
		this.leg.yScale = (1f + ((0.1f * (((vspeed < 0f) ? 0f : vspeed))))) - squish * 0.5f;
		this.leg.zScale = (1f - ((0.08f * (((vspeed < 0f) ? 0f : vspeed))))) + squish * 0.3f;
		this.hat.y += -((1f + ((0.1f * (((vspeed < 0f) ? 0f : vspeed)))))) + squish * 1.5f;

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		modelRoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.modelRoot;
	}
}
