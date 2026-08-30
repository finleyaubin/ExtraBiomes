package net.winepicfin.extrabiomes.entity.client;
// Generated from bait.geo.json by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;

public class BaitModel<T extends BaitProjectileEntity> extends HierarchicalModel<T> {
	private final ModelPart modelRoot;
	private final ModelPart body;
	private final ModelPart worm1;
	private final ModelPart head;
	private final ModelPart head1;
	private final ModelPart head2;
	private final ModelPart head3;
	private final ModelPart body2;
	private final ModelPart worm2;
	private final ModelPart head4;
	private final ModelPart head5;
	private final ModelPart head6;
	private final ModelPart head7;
	private final ModelPart body3;
	private final ModelPart worm3;
	private final ModelPart head8;
	private final ModelPart head9;
	private final ModelPart head10;
	private final ModelPart head11;
	private final ModelPart body4;
	private final ModelPart worm4;
	private final ModelPart head12;
	private final ModelPart head13;
	private final ModelPart head14;
	private final ModelPart head15;
	private final ModelPart body5;
	private final ModelPart worm5;
	private final ModelPart head16;
	private final ModelPart head17;
	private final ModelPart head18;
	private final ModelPart head19;
	private final ModelPart body6;
	private final ModelPart worm6;
	private final ModelPart head20;
	private final ModelPart head21;
	private final ModelPart head22;
	private final ModelPart head23;
	private final ModelPart body7;
	private final ModelPart worm7;
	private final ModelPart head24;
	private final ModelPart head25;
	private final ModelPart head26;
	private final ModelPart head27;
	private final ModelPart body8;
	private final ModelPart worm8;
	private final ModelPart head28;
	private final ModelPart head29;
	private final ModelPart head30;
	private final ModelPart head31;
	private final ModelPart body9;
	private final ModelPart worm9;
	private final ModelPart head32;
	private final ModelPart head33;
	private final ModelPart head34;
	private final ModelPart head35;
	private final ModelPart body10;

	public BaitModel(ModelPart root) {
		this.modelRoot = root;
		this.body = root.getChild("body");
		this.worm1 = this.body.getChild("worm1");
		this.head = this.worm1.getChild("head");
		this.head1 = this.head.getChild("head1");
		this.head2 = this.head1.getChild("head2");
		this.head3 = this.head2.getChild("head3");
		this.body2 = this.head3.getChild("body2");
		this.worm2 = this.body.getChild("worm2");
		this.head4 = this.worm2.getChild("head4");
		this.head5 = this.head4.getChild("head5");
		this.head6 = this.head5.getChild("head6");
		this.head7 = this.head6.getChild("head7");
		this.body3 = this.head7.getChild("body3");
		this.worm3 = this.body.getChild("worm3");
		this.head8 = this.worm3.getChild("head8");
		this.head9 = this.head8.getChild("head9");
		this.head10 = this.head9.getChild("head10");
		this.head11 = this.head10.getChild("head11");
		this.body4 = this.head11.getChild("body4");
		this.worm4 = this.body.getChild("worm4");
		this.head12 = this.worm4.getChild("head12");
		this.head13 = this.head12.getChild("head13");
		this.head14 = this.head13.getChild("head14");
		this.head15 = this.head14.getChild("head15");
		this.body5 = this.head15.getChild("body5");
		this.worm5 = this.body.getChild("worm5");
		this.head16 = this.worm5.getChild("head16");
		this.head17 = this.head16.getChild("head17");
		this.head18 = this.head17.getChild("head18");
		this.head19 = this.head18.getChild("head19");
		this.body6 = this.head19.getChild("body6");
		this.worm6 = this.body.getChild("worm6");
		this.head20 = this.worm6.getChild("head20");
		this.head21 = this.head20.getChild("head21");
		this.head22 = this.head21.getChild("head22");
		this.head23 = this.head22.getChild("head23");
		this.body7 = this.head23.getChild("body7");
		this.worm7 = this.body.getChild("worm7");
		this.head24 = this.worm7.getChild("head24");
		this.head25 = this.head24.getChild("head25");
		this.head26 = this.head25.getChild("head26");
		this.head27 = this.head26.getChild("head27");
		this.body8 = this.head27.getChild("body8");
		this.worm8 = this.body.getChild("worm8");
		this.head28 = this.worm8.getChild("head28");
		this.head29 = this.head28.getChild("head29");
		this.head30 = this.head29.getChild("head30");
		this.head31 = this.head30.getChild("head31");
		this.body9 = this.head31.getChild("body9");
		this.worm9 = this.body.getChild("worm9");
		this.head32 = this.worm9.getChild("head32");
		this.head33 = this.head32.getChild("head33");
		this.head34 = this.head33.getChild("head34");
		this.head35 = this.head34.getChild("head35");
		this.body10 = this.head35.getChild("body10");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition worm1 = body.addOrReplaceChild("worm1", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -4.0F, 2.0F, 0.0F, -0.7854F, 0.0F));
		PartDefinition head = worm1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 3).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 0.0F));
		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(10, 29).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head2 = head1.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(5, 29).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head3 = head2.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(9, 31).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body2 = head3.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(14, 12).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm2 = body.addOrReplaceChild("worm2", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0F, -5.0F, 0.0F, 0.0F, 0.1309F, 0.0F));
		PartDefinition head4 = worm2.addOrReplaceChild("head4", CubeListBuilder.create().texOffs(28, 28).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, -4.0F));
		PartDefinition head5 = head4.addOrReplaceChild("head5", CubeListBuilder.create().texOffs(28, 23).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));
		PartDefinition head6 = head5.addOrReplaceChild("head6", CubeListBuilder.create().texOffs(20, 28).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head7 = head6.addOrReplaceChild("head7", CubeListBuilder.create().texOffs(28, 17).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body3 = head7.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(7, 14).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm3 = body.addOrReplaceChild("worm3", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, -5.0F, 0.0F, 0.0F, -0.2182F, 0.0F));
		PartDefinition head8 = worm3.addOrReplaceChild("head8", CubeListBuilder.create().texOffs(0, 28).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head9 = head8.addOrReplaceChild("head9", CubeListBuilder.create().texOffs(27, 20).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head10 = head9.addOrReplaceChild("head10", CubeListBuilder.create().texOffs(15, 27).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head11 = head10.addOrReplaceChild("head11", CubeListBuilder.create().texOffs(24, 26).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body4 = head11.addOrReplaceChild("body4", CubeListBuilder.create().texOffs(14, 6).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm4 = body.addOrReplaceChild("worm4", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -6.0F, 0.0F, 0.0F, 0.6109F, 0.0F));
		PartDefinition head12 = worm4.addOrReplaceChild("head12", CubeListBuilder.create().texOffs(26, 11).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head13 = head12.addOrReplaceChild("head13", CubeListBuilder.create().texOffs(10, 26).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head14 = head13.addOrReplaceChild("head14", CubeListBuilder.create().texOffs(26, 5).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head15 = head14.addOrReplaceChild("head15", CubeListBuilder.create().texOffs(5, 26).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body5 = head15.addOrReplaceChild("body5", CubeListBuilder.create().texOffs(14, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm5 = body.addOrReplaceChild("worm5", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -7.0F, -1.0F, 0.0F, -3.0805F, 0.0F));
		PartDefinition head16 = worm5.addOrReplaceChild("head16", CubeListBuilder.create().texOffs(19, 25).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head17 = head16.addOrReplaceChild("head17", CubeListBuilder.create().texOffs(25, 14).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head18 = head17.addOrReplaceChild("head18", CubeListBuilder.create().texOffs(25, 8).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head19 = head18.addOrReplaceChild("head19", CubeListBuilder.create().texOffs(25, 2).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body6 = head19.addOrReplaceChild("body6", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm6 = body.addOrReplaceChild("worm6", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -7.0F, 0.0F, 0.0F, -0.2182F, 0.1745F));
		PartDefinition head20 = worm6.addOrReplaceChild("head20", CubeListBuilder.create().texOffs(0, 25).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head21 = head20.addOrReplaceChild("head21", CubeListBuilder.create().texOffs(23, 23).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head22 = head21.addOrReplaceChild("head22", CubeListBuilder.create().texOffs(23, 18).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head23 = head22.addOrReplaceChild("head23", CubeListBuilder.create().texOffs(15, 23).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body7 = head23.addOrReplaceChild("body7", CubeListBuilder.create().texOffs(7, 8).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm7 = body.addOrReplaceChild("worm7", CubeListBuilder.create(), PartPose.offset(2.0F, -8.0F, 0.0F));
		PartDefinition head24 = worm7.addOrReplaceChild("head24", CubeListBuilder.create().texOffs(10, 23).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head25 = head24.addOrReplaceChild("head25", CubeListBuilder.create().texOffs(5, 23).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head26 = head25.addOrReplaceChild("head26", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head27 = head26.addOrReplaceChild("head27", CubeListBuilder.create().texOffs(13, 31).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body8 = head27.addOrReplaceChild("body8", CubeListBuilder.create().texOffs(7, 2).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm8 = body.addOrReplaceChild("worm8", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -10.0F, 0.0F, 0.0F, -0.6545F, 0.1745F));
		PartDefinition head28 = worm8.addOrReplaceChild("head28", CubeListBuilder.create().texOffs(21, 12).addBox(-1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head29 = head28.addOrReplaceChild("head29", CubeListBuilder.create().texOffs(21, 6).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));
		PartDefinition head30 = head29.addOrReplaceChild("head30", CubeListBuilder.create().texOffs(21, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head31 = head30.addOrReplaceChild("head31", CubeListBuilder.create().texOffs(14, 20).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body9 = head31.addOrReplaceChild("body9", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));
		PartDefinition worm9 = body.addOrReplaceChild("worm9", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -3.0F, 0.0F, 0.0F, 0.4363F, 0.0F));
		PartDefinition head32 = worm9.addOrReplaceChild("head32", CubeListBuilder.create().texOffs(9, 20).addBox(-1.0F, -7.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition head33 = head32.addOrReplaceChild("head33", CubeListBuilder.create().texOffs(4, 20).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -4.0F));
		PartDefinition head34 = head33.addOrReplaceChild("head34", CubeListBuilder.create().texOffs(5, 31).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition head35 = head34.addOrReplaceChild("head35", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));
		PartDefinition body10 = head35.addOrReplaceChild("body10", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		// Worms fall off one at a time as the bait is bitten down, instead of just fading the shared texture.
		int visibleWorms = Mth.clamp(Mth.ceil(9f * entity.getHealth() / (float) entity.getMaxHealth()), 0, 9);
		this.worm1.visible = visibleWorms >= 1;
		this.worm2.visible = visibleWorms >= 2;
		this.worm3.visible = visibleWorms >= 3;
		this.worm4.visible = visibleWorms >= 4;
		this.worm5.visible = visibleWorms >= 5;
		this.worm6.visible = visibleWorms >= 6;
		this.worm7.visible = visibleWorms >= 7;
		this.worm8.visible = visibleWorms >= 8;
		this.worm9.visible = visibleWorms >= 9;

		// animation.bait.wrigle: all worm segment chains flex in an alternating +/-45 degree pattern off abs(sin(life_time)); slowed from the Bedrock source's *50 for a calmer wiggle.
		// Each worm gets a phase offset so the 9 chains don't all flex in lockstep.
		float lifeTime = ageInTicks / 20f;
		applyWiggle(lifeTime, 0, this.head1, this.head2, this.head3, this.body2);
		applyWiggle(lifeTime, 1, this.head5, this.head6, this.head7, this.body3);
		applyWiggle(lifeTime, 2, this.head9, this.head10, this.head11, this.body4);
		applyWiggle(lifeTime, 3, this.head13, this.head14, this.head15, this.body5);
		applyWiggle(lifeTime, 4, this.head17, this.head18, this.head19, this.body6);
		applyWiggle(lifeTime, 5, this.head21, this.head22, this.head23, this.body7);
		applyWiggle(lifeTime, 6, this.head25, this.head26, this.head27, this.body8);
		applyWiggle(lifeTime, 7, this.head29, this.head30, this.head31, this.body9);
		applyWiggle(lifeTime, 8, this.head33, this.head34, this.head35, this.body10);

		// Not a LivingEntity, so no automatic hurt shake - fake it with a quick decaying wobble on the whole body.
		if (entity.getHurtTime() > 0) {
			float shake = Mth.sin(entity.getHurtTime() * 3.0F) * (entity.getHurtTime() / 10f) * 0.15F;
			this.body.zRot += shake;
		}
	}

	private static void applyWiggle(float lifeTime, int wormIndex, ModelPart first, ModelPart second, ModelPart third, ModelPart fourth) {
		float phase = wormIndex * 0.6981317F;
		float wiggle = Math.abs(Mth.sin(lifeTime + phase)) * 45f * 0.017453292f;
		first.xRot += wiggle;
		second.xRot -= wiggle;
		third.xRot -= wiggle;
		fourth.xRot += wiggle;
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
