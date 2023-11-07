package net.winepicfin.extrabiomes.entity.client;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;


public class PuckooModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart Puckoo;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart leg0;
	private final ModelPart leg1;


	public PuckooModel(ModelPart root) {
		this.Puckoo = root.getChild("Puckoo");
		this.neck = Puckoo.getChild("neck");
		this.head = Puckoo.getChild("neck").getChild("head");
		this.leg0=Puckoo.getChild("leg0");
		this.leg1=Puckoo.getChild("leg1");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Puckoo = partdefinition.addOrReplaceChild("Puckoo", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -2.75F, -5.0F, 7.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.5F, -1.0F));

		PartDefinition leg0 = Puckoo.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(32, 13).addBox(-1.5F, 0.75F, -2.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(32, 32).addBox(-1.51F, -1.25F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 2.5F, 0.0F));

		PartDefinition leg1 = Puckoo.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(27, 26).addBox(-1.49F, -1.25F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(25, 0).addBox(-1.5F, 0.75F, -2.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 2.5F, 0.0F));

		PartDefinition Tail = Puckoo.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(21, 27).addBox(0.0F, -4.25F, -1.0F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 5.0F));

		PartDefinition wing0 = Puckoo.addOrReplaceChild("wing0", CubeListBuilder.create(), PartPose.offset(3.0F, -1.0F, -2.0F));

		PartDefinition cube_r1 = wing0.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 32).addBox(0.5F, 1.0F, 0.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.25F, -1.0F, -0.0873F, 0.0873F, -0.0436F));

		PartDefinition wing1 = Puckoo.addOrReplaceChild("wing1", CubeListBuilder.create(), PartPose.offset(-4.0F, -1.0F, -2.0F));

		PartDefinition cube_r2 = wing1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(0.5F, 1.0F, 0.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.25F, -1.0F, -0.0873F, -0.0873F, 0.0436F));

		PartDefinition Saddle = Puckoo.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(30, 49).addBox(-3.5F, -2.75F, -5.0F, 7.0F, 5.0F, 10.0F, new CubeDeformation(0.101F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition neck = Puckoo.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, -5.0F));

		PartDefinition cube_r3 = neck.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 24).addBox(-1.5F, -1.5F, -2.25F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, 0.0F, -0.7418F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(41, 43).addBox(-2.0F, -4.25F, -2.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(10, 27).addBox(-0.25F, -7.25F, -3.0F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition upperjaw = head.addOrReplaceChild("upperjaw", CubeListBuilder.create().texOffs(4, 43).addBox(-2.0F, -2.25F, -3.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -2.0F));

		PartDefinition Lowerjaw = head.addOrReplaceChild("Lowerjaw", CubeListBuilder.create().texOffs(26, 43).addBox(-2.0F, -0.25F, -3.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -2.0F));

		PartDefinition mossy_pebble = Lowerjaw.addOrReplaceChild("mossy_pebble", CubeListBuilder.create().texOffs(50, 16).addBox(-1.0F, -0.5F, -3.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.25F, 0.25F));

		PartDefinition Pebble = Lowerjaw.addOrReplaceChild("Pebble", CubeListBuilder.create().texOffs(50, 5).addBox(-1.5F, -0.5F, -3.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.25F, 0.25F));

		PartDefinition whiskers = Lowerjaw.addOrReplaceChild("whiskers", CubeListBuilder.create().texOffs(1, 53).addBox(2.26F, 0.75F, -1.0F, 0.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(1, 53).addBox(-2.26F, 0.75F, -1.0F, 0.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw,headPitch,ageInTicks);
		this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
	}
	private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
		pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
		pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

		this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Puckoo.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return Puckoo;
	}
}