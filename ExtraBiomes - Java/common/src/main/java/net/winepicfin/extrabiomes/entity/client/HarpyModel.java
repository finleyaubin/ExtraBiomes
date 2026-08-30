package net.winepicfin.extrabiomes.entity.client;
// Generated from harpy.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class HarpyModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart modelRoot;
	private final ModelPart root;
	private final ModelPart waist;
	private final ModelPart wings;
	private final ModelPart right_wing;
	private final ModelPart support1;
	private final ModelPart bone3;
	private final ModelPart right_spike;
	private final ModelPart support2;
	private final ModelPart bone;
	private final ModelPart support3;
	private final ModelPart bone2;
	private final ModelPart left_wing;
	private final ModelPart support4;
	private final ModelPart bone4;
	private final ModelPart left_spike;
	private final ModelPart support5;
	private final ModelPart bone5;
	private final ModelPart support6;
	private final ModelPart bone6;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public HarpyModel(ModelPart root) {
		this.modelRoot = root;
		this.root = root.getChild("root");
		this.waist = this.root.getChild("waist");
		this.wings = this.waist.getChild("wings");
		this.right_wing = this.wings.getChild("right_wing");
		this.support1 = this.right_wing.getChild("support1");
		this.bone3 = this.support1.getChild("bone3");
		this.right_spike = this.support1.getChild("right_spike");
		this.support2 = this.support1.getChild("support2");
		this.bone = this.support2.getChild("bone");
		this.support3 = this.support2.getChild("support3");
		this.bone2 = this.support3.getChild("bone2");
		this.left_wing = this.wings.getChild("left_wing");
		this.support4 = this.left_wing.getChild("support4");
		this.bone4 = this.support4.getChild("bone4");
		this.left_spike = this.support4.getChild("left_spike");
		this.support5 = this.support4.getChild("support5");
		this.bone5 = this.support5.getChild("bone5");
		this.support6 = this.support5.getChild("support6");
		this.bone6 = this.support6.getChild("bone6");
		this.body = this.waist.getChild("body");
		this.head = this.body.getChild("head");
		this.leftArm = this.body.getChild("leftArm");
		this.rightArm = this.body.getChild("rightArm");
		this.rightLeg = this.root.getChild("rightLeg");
		this.leftLeg = this.root.getChild("leftLeg");
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
		// animation.harpy.fly
		this.left_wing.yRot += ((((Mth.sin(((limbSwing * 15f)) * 0.017453292f) * 20f)) + 1.5f)) * 0.017453292f;
		this.right_wing.yRot += ((((((-1f) * Mth.sin(((limbSwing * 15f)) * 0.017453292f)) * 20f)) + 1.5f)) * 0.017453292f;
		this.support5.yRot += ((((Mth.sin(((limbSwing * 15f)) * 0.017453292f) * 20f)) + 1.5f)) * 0.017453292f;
		this.support2.yRot += ((((((-1f) * Mth.sin(((limbSwing * 15f)) * 0.017453292f)) * 20f)) + 1.5f)) * 0.017453292f;

		// animation.harpy.tilt
		this.root.xRot += ((((limbSwingAmount / 1f)) * 45f)) * 0.017453292f;

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		modelRoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return this.modelRoot;
	}
}
