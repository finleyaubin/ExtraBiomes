package net.winepicfin.extrabiomes.entity.client;
// Generated from jellyfish.geo.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class JellyfishModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;

	public JellyfishModel(ModelPart root) {
		this.root = root;
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
