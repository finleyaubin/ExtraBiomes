package net.winepicfin.extrabiomes.entity.client;
// Generated from jellyfish.geo.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class JellyfishModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart modelRoot;
	private final ModelPart body;
	private final ModelPart tentacles;
	private final ModelPart tentacle2;
	private final ModelPart upper2;
	private final ModelPart lower2;
	private final ModelPart tentacle1;
	private final ModelPart upper;
	private final ModelPart lower;
	private final ModelPart tentacle4;
	private final ModelPart upper4;
	private final ModelPart lower4;
	private final ModelPart tentacle5;
	private final ModelPart upper5;
	private final ModelPart lower5;
	private final ModelPart tentacle3;
	private final ModelPart upper3;
	private final ModelPart lower3;
	private final ModelPart tentacle6;
	private final ModelPart upper6;
	private final ModelPart lower6;
	private final ModelPart head;

	public JellyfishModel(ModelPart root) {
		this.modelRoot = root;
		this.body = root.getChild("body");
		this.tentacles = this.body.getChild("tentacles");
		this.tentacle2 = this.tentacles.getChild("tentacle2");
		this.upper2 = this.tentacle2.getChild("upper2");
		this.lower2 = this.upper2.getChild("lower2");
		this.tentacle1 = this.tentacles.getChild("tentacle1");
		this.upper = this.tentacle1.getChild("upper");
		this.lower = this.upper.getChild("lower");
		this.tentacle4 = this.tentacles.getChild("tentacle4");
		this.upper4 = this.tentacle4.getChild("upper4");
		this.lower4 = this.upper4.getChild("lower4");
		this.tentacle5 = this.tentacles.getChild("tentacle5");
		this.upper5 = this.tentacle5.getChild("upper5");
		this.lower5 = this.upper5.getChild("lower5");
		this.tentacle3 = this.tentacles.getChild("tentacle3");
		this.upper3 = this.tentacle3.getChild("upper3");
		this.lower3 = this.upper3.getChild("lower3");
		this.tentacle6 = this.tentacle3.getChild("tentacle6");
		this.upper6 = this.tentacle6.getChild("upper6");
		this.lower6 = this.upper6.getChild("lower6");
		this.head = this.body.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition tentacles = body.addOrReplaceChild("tentacles", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition tentacle2 = tentacles.addOrReplaceChild("tentacle2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition upper2 = tentacle2.addOrReplaceChild("upper2", CubeListBuilder.create().texOffs(60, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -10.0F, -2.0F));
		PartDefinition lower2 = upper2.addOrReplaceChild("lower2", CubeListBuilder.create().texOffs(60, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
		PartDefinition tentacle1 = tentacles.addOrReplaceChild("tentacle1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition upper = tentacle1.addOrReplaceChild("upper", CubeListBuilder.create().texOffs(60, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));
		PartDefinition lower = upper.addOrReplaceChild("lower", CubeListBuilder.create().texOffs(60, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
		PartDefinition tentacle4 = tentacles.addOrReplaceChild("tentacle4", CubeListBuilder.create(), PartPose.offset(1.5F, -10.0F, -1.0F));
		PartDefinition upper4 = tentacle4.addOrReplaceChild("upper4", CubeListBuilder.create().texOffs(60, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition lower4 = upper4.addOrReplaceChild("lower4", CubeListBuilder.create().texOffs(60, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
		PartDefinition tentacle5 = tentacles.addOrReplaceChild("tentacle5", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 0.0F));
		PartDefinition upper5 = tentacle5.addOrReplaceChild("upper5", CubeListBuilder.create(), PartPose.offset(-1.0F, -10.0F, 2.0F));
		PartDefinition upper5_r1 = upper5.addOrReplaceChild("upper5_r1", CubeListBuilder.create().texOffs(60, 0).addBox(-1.0F, -10.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition lower5 = upper5.addOrReplaceChild("lower5", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));
		PartDefinition lower5_r2 = lower5.addOrReplaceChild("lower5_r2", CubeListBuilder.create().texOffs(60, 0).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition tentacle3 = tentacles.addOrReplaceChild("tentacle3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition upper3 = tentacle3.addOrReplaceChild("upper3", CubeListBuilder.create().texOffs(60, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -10.0F, 2.0F));
		PartDefinition lower3 = upper3.addOrReplaceChild("lower3", CubeListBuilder.create().texOffs(60, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
		PartDefinition tentacle6 = tentacle3.addOrReplaceChild("tentacle6", CubeListBuilder.create(), PartPose.offset(3.0F, 0.0F, -1.0F));
		PartDefinition upper6 = tentacle6.addOrReplaceChild("upper6", CubeListBuilder.create(), PartPose.offset(-1.0F, -10.0F, 1.0F));
		PartDefinition upper6_r3 = upper6.addOrReplaceChild("upper6_r3", CubeListBuilder.create().texOffs(60, 0).addBox(-1.0F, -10.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, 1.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition lower6 = upper6.addOrReplaceChild("lower6", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));
		PartDefinition lower6_r4 = lower6.addOrReplaceChild("lower6_r4", CubeListBuilder.create().texOffs(60, 0).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 1.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float drift = Mth.sin(ageInTicks * 0.12F) * 0.12F;
		float drift2 = Mth.cos(ageInTicks * 0.12F) * 0.12F;
		this.tentacles.xRot += drift;
		this.tentacles.zRot += drift2;
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
