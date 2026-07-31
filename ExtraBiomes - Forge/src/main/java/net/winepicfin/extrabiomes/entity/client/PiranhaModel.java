package net.winepicfin.extrabiomes.entity.client;
// Generated from piranha.geo.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PiranhaModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart modelRoot;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart leftFin;
	private final ModelPart rightFin;
	private final ModelPart tailfin;
	private final ModelPart waist;

	public PiranhaModel(ModelPart root) {
		this.modelRoot = root;
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.leftFin = this.body.getChild("leftFin");
		this.rightFin = this.body.getChild("rightFin");
		this.tailfin = this.body.getChild("tailfin");
		this.waist = this.body.getChild("waist");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -4.0F, 1.0F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 23).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(1, 0).addBox(-1.0F, -5.0F, 6.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(20, -6).addBox(0.0F, -7.0F, 0.0F, 0.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(22, -1).addBox(0.0F, 0.0F, 3.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(11, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(20, 26).addBox(-0.9992F, -0.0008F, -4.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(22, 22).addBox(-0.9992F, 0.9992F, -3.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition leftFin = body.addOrReplaceChild("leftFin", CubeListBuilder.create().texOffs(24, 4).addBox(1.0F, 0.0F, 3.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.6109F));
		PartDefinition rightFin = body.addOrReplaceChild("rightFin", CubeListBuilder.create().texOffs(24, 1).addBox(-3.0F, 0.0F, 3.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.6109F));
		PartDefinition tailfin = body.addOrReplaceChild("tailfin", CubeListBuilder.create().texOffs(20, 1).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 8.0F));
		PartDefinition waist = body.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float speed = entity.isInWater() ? 0.9F : 1.6F;
		float amount = entity.isInWater() ? 0.28F : 0.5F;
		this.tailfin.yRot = -amount * Mth.sin(speed * 0.4F * ageInTicks);
		this.waist.yRot = 0.5F * amount * Mth.sin(speed * 0.4F * ageInTicks);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F) * 0.5F;
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
