package net.winepicfin.extrabiomes.entity.client;
// Generated from harpy.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class HarpyModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;

	public HarpyModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));
		PartDefinition wings = waist.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));
		PartDefinition right_wing = wings.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.1745F, 0.0F, -0.2618F));
		PartDefinition support1 = right_wing.addOrReplaceChild("support1", CubeListBuilder.create().mirror().texOffs(30, 34).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 5.0F, 1.0F, 0.3054F, -0.0873F, -2.5744F));
		PartDefinition bone3 = support1.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(39, 38).addBox(-3.0F, -19.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(-5.0F, -20.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.0F, -1.0F, -1.0F, 0.0F, 0.0F, 1.9635F));
		PartDefinition right_spike = support1.addOrReplaceChild("right_spike", CubeListBuilder.create().mirror().texOffs(39, 31).addBox(-1.8F, -1.0F, 0.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(42, 17).addBox(-2.8F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(42, 17).addBox(-3.8F, -3.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(0, 0).addBox(-4.8F, -4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 7.0F, -1.0F, 0.7854F, 0.0F, 0.0F));
		PartDefinition support2 = support1.addOrReplaceChild("support2", CubeListBuilder.create().mirror().texOffs(30, 34).addBox(0.0F, -8.0F, 0.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 7.0F, 0.0F, -0.1745F, -0.0873F, -2.5307F));
		PartDefinition bone = support2.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(39, 38).addBox(2.0F, -20.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(6.0F, -19.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(8.0F, -19.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(4.0F, -20.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(21.0F, 2.0F, -1.0F, 0.0F, 0.0F, -1.5708F));
		PartDefinition support3 = support2.addOrReplaceChild("support3", CubeListBuilder.create().mirror().texOffs(30, 34).addBox(0.0F, -1.0F, 0.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(39, 38).addBox(0.0F, 8.0F, 0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(0.0F, 8.0F, 0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(-2.0F, 5.0F, 0.8F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(-4.0F, 2.0F, 0.8F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -8.0F, 0.0F, 0.0436F, -0.0873F, -2.5307F));
		PartDefinition bone2 = support3.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(39, 38).addBox(0.0F, -10.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(2.0F, -10.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(46, 38).addBox(4.0F, -10.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(39, 38).addBox(-2.0F, -11.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, 2.0F, -1.0F, 0.0F, 0.0F, 1.5708F));
		PartDefinition left_wing = wings.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.1745F, 0.0F, 0.2618F));
		PartDefinition support4 = left_wing.addOrReplaceChild("support4", CubeListBuilder.create().texOffs(30, 34).addBox(0.0F, -2.0F, 0.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 5.0F, 1.0F, 0.3054F, 0.0873F, 2.5744F));
		PartDefinition bone4 = support4.addOrReplaceChild("bone4", CubeListBuilder.create().mirror().texOffs(39, 38).addBox(1.0F, -19.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(3.0F, -20.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(20.0F, -1.0F, -1.0F, 0.0F, 0.0F, -1.9635F));
		PartDefinition left_spike = support4.addOrReplaceChild("left_spike", CubeListBuilder.create().texOffs(39, 31).addBox(-1.2F, -1.0F, 0.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(42, 17).addBox(0.8F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(42, 17).addBox(1.8F, -3.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(3.8F, -4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 7.0F, -1.0F, 0.7854F, 0.0F, 0.0F));
		PartDefinition support5 = support4.addOrReplaceChild("support5", CubeListBuilder.create().texOffs(30, 34).addBox(-2.0F, -8.0F, 0.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 7.0F, 0.0F, -0.1745F, 0.0873F, 2.5307F));
		PartDefinition bone5 = support5.addOrReplaceChild("bone5", CubeListBuilder.create().mirror().texOffs(39, 38).addBox(-4.0F, -20.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(-8.0F, -19.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(-10.0F, -19.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(-6.0F, -20.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-21.0F, 2.0F, -1.0F, 0.0F, 0.0F, 1.5708F));
		PartDefinition support6 = support5.addOrReplaceChild("support6", CubeListBuilder.create().texOffs(30, 34).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)).mirror().texOffs(39, 38).addBox(-2.0F, 8.0F, 0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(-2.0F, 8.0F, 0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(0.0F, 5.0F, 0.8F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(2.0F, 2.0F, 0.8F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -8.0F, 0.0F, 0.0436F, 0.0873F, 2.5307F));
		PartDefinition bone6 = support6.addOrReplaceChild("bone6", CubeListBuilder.create().mirror().texOffs(39, 38).addBox(-2.0F, -10.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(-4.0F, -10.0F, 1.7F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(46, 38).addBox(-6.0F, -10.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false).mirror().texOffs(39, 38).addBox(0.0F, -11.0F, 1.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(10.0F, 2.0F, -1.0F, 0.0F, 0.0F, -1.5708F));
		PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(15, 34).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.5F, 0.0F));
		PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(0, 34).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
		PartDefinition rightLeg = root.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(33, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, -12.0F, 0.0F));
		PartDefinition leftLeg = root.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(25, 17).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
