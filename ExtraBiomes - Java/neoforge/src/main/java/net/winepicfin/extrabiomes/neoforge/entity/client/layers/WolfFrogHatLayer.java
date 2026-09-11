package net.winepicfin.extrabiomes.neoforge.entity.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderState;

import java.lang.reflect.Field;

public class WolfFrogHatLayer extends RenderLayer<WolfRenderState, WolfModel> {
    // WolfModel#head isn't widened by accesstransformer.cfg on the neoforge module's merged jar
    // (same unresolved AT issue as ModVanillaCompat/ModSpawnCaps) - a plain read, so
    // Field#setAccessible(true) is enough here, no Unsafe needed.
    private static final Field HEAD_FIELD;

    static {
        try {
            HEAD_FIELD = WolfModel.class.getDeclaredField("head");
            HEAD_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private FrogHelmetRenderer renderer;
    private HumanoidModel<?> baseModel;

    public WolfFrogHatLayer(RenderLayerParent<WolfRenderState, WolfModel> parent) {
        super(parent);
    }

    // Vanilla's render states no longer carry the source Entity - GeckoLib's EntityRenderStateMixin
    // (applied to every EntityRenderState) ducks one back on via GeoEntityRenderState, which
    // GeoArmorRenderer#prepForRender still needs.
    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull WolfRenderState renderState,
                        float netHeadYaw, float headPitch) {
        if (!(renderState instanceof GeoEntityRenderState geoRenderState) || !(geoRenderState.geckolib$getEntity() instanceof Wolf wolf))
            return;

        ItemStack headItem = wolf.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.getItem() != ModItems.FROG_HELMET.get() || wolf.isInvisible()) return;

        if (this.renderer == null)
            this.renderer = new FrogHelmetRenderer();
        // GeoArmorRenderer.prepForRender bails out early (leaving its internal entity
        // reference null, which crashes renderToBuffer) unless it's given a non-null
        // base HumanoidModel, even though that model is never actually used for a wolf.
        if (this.baseModel == null)
            this.baseModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER));

        poseStack.pushPose();
        try {
            ((ModelPart) HEAD_FIELD.get(this.getParentModel())).translateAndRotate(poseStack);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        poseStack.translate(0.05D, -0.6D, -0.02D);
        poseStack.scale(1F, 1F, 1F);
        poseStack.mulPose(Axis.XP.rotationDegrees(0.0F));
        this.renderer.prepForRender(wolf, headItem, EquipmentSlot.HEAD, this.baseModel, buffer, geoRenderState.geckolib$getPartialTick(), netHeadYaw, headPitch);
        this.renderer.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY,
                ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
        poseStack.popPose();
    }
}
