package net.winepicfin.extrabiomes.entity.client;
// Generated from worm.geo.json by tools/geo2java.py

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class WormModel<T extends LivingEntityRenderState> extends EntityModel<T> {
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart head1;
	private final ModelPart head2;
	private final ModelPart head3;
	private final ModelPart body2;
	private final ModelPart Archie;

	public WormModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.head1 = this.head.getChild("head1");
		this.head2 = this.head1.getChild("head2");
		this.head3 = this.head2.getChild("head3");
		this.body2 = this.head3.getChild("body2");
		this.Archie = this.body2.getChild("Archie");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 9).addBox(-1.04F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(7, 0).addBox(-1.03F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head2 = head1.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(5, 6).addBox(-1.02F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head3 = head2.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(0, 6).addBox(-1.01F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body2 = head3.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition Archie = body2.addOrReplaceChild("Archie", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 104.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 80.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 84.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 88.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 92.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 96.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 100.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 76.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 72.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 68.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 64.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 60.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 56.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 52.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 8.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 12.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 16.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 44.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 40.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 36.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 32.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 28.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 24.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-0.5F, -0.5F, 48.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -0.5F, 8.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(T state) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float limbSwing = state.walkAnimationPos;
		// "Archie" easter egg: the long extra tail only shows when the worm is renamed to Archie.
		this.Archie.visible = state.customName != null && "Archie".equals(state.customName.getString());
		// animation.worm.move
		this.head1.xRot += ((Math.abs(Mth.sin(((limbSwing * 50f)) * 0.017453292f)) * 45f)) * 0.017453292f;
		this.head2.xRot += ((Math.abs(Mth.sin(((limbSwing * 50f)) * 0.017453292f)) * (-45f))) * 0.017453292f;
		this.head3.xRot += ((Math.abs(Mth.sin(((limbSwing * 50f)) * 0.017453292f)) * (-45f))) * 0.017453292f;
		this.body2.xRot += ((Math.abs(Mth.sin(((limbSwing * 50f)) * 0.017453292f)) * 45f)) * 0.017453292f;
		this.body.z += (((((Mth.sin(((limbSwing * 32f)) * 0.017453292f) * 2f)) <= 0f) ? 0f : ((Mth.sin(((limbSwing * 64f)) * 0.017453292f) * 2f))));

	}
}
