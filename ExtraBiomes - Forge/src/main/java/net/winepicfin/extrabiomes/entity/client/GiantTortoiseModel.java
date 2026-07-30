package net.winepicfin.extrabiomes.entity.client;
// Generated from giant_tortoise.geo.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class GiantTortoiseModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;

	public GiantTortoiseModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));
		PartDefinition body2 = body.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(69, 33).addBox(-4.5F, 3.0F, -10.0F, 9.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(94, 0).addBox(-6.0F, 6.0F, 0.0F, 13.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(30, 1).addBox(-5.5F, 3.0F, -9.0F, 11.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -10.0F, 1.5708F, 0.0F, 0.0F));
		PartDefinition spikes = body2.addOrReplaceChild("spikes", CubeListBuilder.create().texOffs(6, 37).addBox(-10.0F, -17.5F, -7.5F, 19.0F, 20.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 20.5F, 1.5F));
		PartDefinition spike1 = spikes.addOrReplaceChild("spike1", CubeListBuilder.create().texOffs(120, 60).addBox(-0.5F, -2.2929F, 0.4213F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition spike2 = spikes.addOrReplaceChild("spike2", CubeListBuilder.create().texOffs(120, 60).addBox(-0.5F, -1.327F, 1.7836F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.2618F, 0.0F, 0.0F));
		PartDefinition spike3 = spikes.addOrReplaceChild("spike3", CubeListBuilder.create().texOffs(120, 60).addBox(-0.5F, -1.0F, 2.3F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));
		PartDefinition spike4 = spikes.addOrReplaceChild("spike4", CubeListBuilder.create().texOffs(120, 60).addBox(-0.5F, -3.3093F, 1.7483F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 1.0F, 0.2618F, 0.0F, 0.0F));
		PartDefinition spike5 = spikes.addOrReplaceChild("spike5", CubeListBuilder.create().texOffs(120, 60).addBox(-0.5F, -4.7648F, 2.3611F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 2.0F, 0.4363F, 0.0F, 0.0F));
		PartDefinition spike6 = spikes.addOrReplaceChild("spike6", CubeListBuilder.create().texOffs(120, 60).addBox(-0.5F, 2.4663F, 2.2353F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.5F, -2.0F, 0.9599F, 0.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -7.0F));
		PartDefinition leg0 = body.addOrReplaceChild("leg0", CubeListBuilder.create().mirror().texOffs(106, 32).addBox(-6.0F, 5.0F, -3.0F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(108, 39).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -1.0F, 8.0F));
		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(108, 39).addBox(0.0F, -1.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).mirror().texOffs(106, 32).addBox(0.0F, 5.0F, -3.0F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -1.0F, 8.0F));
		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(108, 39).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).mirror().texOffs(106, 32).addBox(-6.0F, 5.0F, -3.0F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, -1.0F, -3.0F));
		PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(108, 39).addBox(0.0F, -1.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).mirror().texOffs(106, 32).addBox(0.0F, 5.0F, -3.0F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -1.0F, -3.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
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
