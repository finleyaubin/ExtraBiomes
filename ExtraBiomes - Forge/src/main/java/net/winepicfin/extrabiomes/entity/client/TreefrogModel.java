package net.winepicfin.extrabiomes.entity.client;
// Generated from treefrog.geo.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class TreefrogModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart modelRoot;
	private final ModelPart treefrog;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart leg1;
	private final ModelPart leglow1;
	private final ModelPart bottom1;
	private final ModelPart foot1;
	private final ModelPart leg2;
	private final ModelPart leglow2;
	private final ModelPart bottom2;
	private final ModelPart foot2;
	private final ModelPart leg4;
	private final ModelPart leglow4;
	private final ModelPart bottom4;
	private final ModelPart foot4;
	private final ModelPart leg3;
	private final ModelPart leglow3;
	private final ModelPart bottom3;
	private final ModelPart foot3;

	public TreefrogModel(ModelPart root) {
		this.modelRoot = root;
		this.treefrog = root.getChild("treefrog");
		this.body = this.treefrog.getChild("body");
		this.head = this.body.getChild("head");
		this.leg1 = this.body.getChild("leg1");
		this.leglow1 = this.leg1.getChild("leglow1");
		this.bottom1 = this.leglow1.getChild("bottom1");
		this.foot1 = this.bottom1.getChild("foot1");
		this.leg2 = this.body.getChild("leg2");
		this.leglow2 = this.leg2.getChild("leglow2");
		this.bottom2 = this.leglow2.getChild("bottom2");
		this.foot2 = this.bottom2.getChild("foot2");
		this.leg4 = this.body.getChild("leg4");
		this.leglow4 = this.leg4.getChild("leglow4");
		this.bottom4 = this.leglow4.getChild("bottom4");
		this.foot4 = this.bottom4.getChild("foot4");
		this.leg3 = this.body.getChild("leg3");
		this.leglow3 = this.leg3.getChild("leglow3");
		this.bottom3 = this.leglow3.getChild("bottom3");
		this.foot3 = this.bottom3.getChild("foot3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition treefrog = partdefinition.addOrReplaceChild("treefrog", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition body = treefrog.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -5.0F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 9).addBox(-1.5F, 1.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)).texOffs(0, 9).addBox(0.0F, -0.8F, -4.6F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-1.0F)).texOffs(18, 0).addBox(-3.0F, -0.8F, -4.6F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-1.0F)), PartPose.offset(0.0F, -6.0F, -3.0F));
		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(16, 15).addBox(0.0F, -0.5F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -2.5F, 3.0F, -0.8913F, 1.2316F, -0.8625F));
		PartDefinition leglow1 = leg1.addOrReplaceChild("leglow1", CubeListBuilder.create().texOffs(8, 15).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -0.5F, 0.0F, 0.0F, 0.1745F, -0.3927F));
		PartDefinition bottom1 = leglow1.addOrReplaceChild("bottom1", CubeListBuilder.create().texOffs(6, 23).addBox(0.0F, 0.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 1.0F, 0.5F, 0.0F, 0.0F, 0.9599F));
		PartDefinition foot1 = bottom1.addOrReplaceChild("foot1", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, -1.0F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, 1.0F, 0.0F, 0.0F, -0.2182F));
		PartDefinition foot1_r1 = foot1.addOrReplaceChild("foot1_r1", CubeListBuilder.create().texOffs(24, 21).addBox(-0.5F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -1.0F, 0.0F, -0.3054F, 0.0F));
		PartDefinition foot1_r2 = foot1.addOrReplaceChild("foot1_r2", CubeListBuilder.create().texOffs(18, 21).addBox(-0.5F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -1.0F, 0.0F, 0.3054F, 0.0F));
		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 15).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -2.5F, 3.0F, -0.8913F, -1.2316F, 0.8625F));
		PartDefinition leglow2 = leg2.addOrReplaceChild("leglow2", CubeListBuilder.create().texOffs(24, 9).addBox(0.0F, -0.5F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, -0.1745F, 0.3927F));
		PartDefinition bottom2 = leglow2.addOrReplaceChild("bottom2", CubeListBuilder.create().texOffs(12, 19).addBox(-2.0F, 0.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.5F, 0.5F, 0.0F, 0.0F, -0.9599F));
		PartDefinition foot2 = bottom2.addOrReplaceChild("foot2", CubeListBuilder.create().texOffs(6, 19).addBox(-2.0F, -1.0F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.2182F));
		PartDefinition foot2_r3 = foot2.addOrReplaceChild("foot2_r3", CubeListBuilder.create().texOffs(0, 19).addBox(-1.5F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -1.0F, 0.0F, 0.3054F, 0.0F));
		PartDefinition foot2_r4 = foot2.addOrReplaceChild("foot2_r4", CubeListBuilder.create().texOffs(24, 17).addBox(-1.5F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -1.0F, 0.0F, -0.3054F, 0.0F));
		PartDefinition leg4 = body.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(18, 17).addBox(-2.0F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -2.5F, -3.0F, -2.1458F, 1.2573F, -2.1688F));
		PartDefinition leglow4 = leg4.addOrReplaceChild("leglow4", CubeListBuilder.create().texOffs(12, 17).addBox(0.2867F, -0.4588F, -0.2269F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.5F, 0.0F, 0.0F, -0.2618F, 0.3927F));
		PartDefinition bottom4 = leglow4.addOrReplaceChild("bottom4", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, 0.0F, 0.5F, -3.1416F, 0.0F, -2.9671F));
		PartDefinition foot4 = bottom4.addOrReplaceChild("foot4", CubeListBuilder.create().texOffs(6, 17).addBox(-0.7374F, -1.9726F, -1.2731F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.3054F));
		PartDefinition foot4_r5 = foot4.addOrReplaceChild("foot4_r5", CubeListBuilder.create().texOffs(0, 17).addBox(-1.5F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2626F, -1.4726F, -0.7731F, 0.0F, 0.3054F, 0.0F));
		PartDefinition foot4_r6 = foot4.addOrReplaceChild("foot4_r6", CubeListBuilder.create().texOffs(24, 15).addBox(-1.5F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2626F, -1.4726F, -0.7731F, 0.0F, -0.3054F, 0.0F));
		PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create().mirror().texOffs(18, 17).addBox(0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, -2.5F, -3.0F, -2.1458F, -1.2573F, 2.1688F));
		PartDefinition leglow3 = leg3.addOrReplaceChild("leglow3", CubeListBuilder.create().mirror().texOffs(12, 17).addBox(-2.2867F, -0.1588F, -0.2269F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 0.2F, 0.0F, 0.0F, 0.2618F, -0.3927F));
		PartDefinition bottom3 = leglow3.addOrReplaceChild("bottom3", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, 0.3F, 0.5F, -3.1416F, 0.0F, 2.9671F));
		PartDefinition foot3 = bottom3.addOrReplaceChild("foot3", CubeListBuilder.create().mirror().texOffs(6, 17).addBox(-1.2626F, -1.9726F, -1.2731F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 1.0F, 1.0F, 0.0F, 0.0F, -0.3054F));
		PartDefinition foot3_r7 = foot3.addOrReplaceChild("foot3_r7", CubeListBuilder.create().mirror().texOffs(0, 17).addBox(-0.5F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.2626F, -1.4726F, -0.7731F, 0.0F, -0.3054F, 0.0F));
		PartDefinition foot3_r8 = foot3.addOrReplaceChild("foot3_r8", CubeListBuilder.create().mirror().texOffs(24, 15).addBox(-0.5F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.2626F, -1.4726F, -0.7731F, 0.0F, 0.3054F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.body.y += Mth.sin(ageInTicks * 0.15F) * 0.3F;
		this.head.yRot += netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot += headPitch * ((float) Math.PI / 180F);
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
