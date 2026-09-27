package net.winepicfin.extrabiomes.neoforge.entity.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.neoforge.item.custom.FrogHelmetItem;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

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

    public static final ContextKey<Wolf> WOLF = new ContextKey<>(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "wolf"));

    private FrogHelmetRenderer renderer;
    private HumanoidModel<?> baseModel;

    public WolfFrogHatLayer(RenderLayerParent<WolfRenderState, WolfModel> parent) {
        super(parent);
    }

    // Vanilla's render states no longer carry the source entity - ModEventBusClientEvents stashes the
    // Wolf under WOLF via RegisterRenderStateModifiersEvent, and GeoArmorRenderer needs a GeoRenderState
    // humanoid state (GeckoLib's mixin makes every EntityRenderState one) that we fill ourselves.
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int packedLight, @NotNull WolfRenderState renderState,
                        float netHeadYaw, float headPitch) {
        Wolf wolf = renderState.getRenderData(WOLF);
        if (wolf == null)
            return;

        ItemStack headItem = wolf.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.getItem() != ModItems.FROG_HELMET.get() || wolf.isInvisible()) return;

        if (this.renderer == null)
            this.renderer = new FrogHelmetRenderer<>();
        if (this.baseModel == null)
            this.baseModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER));

        HumanoidRenderState humanoidState = new HumanoidRenderState();
        // GeckoLib reads light from the vanilla lightCoords field, not its PACKED_LIGHT ticket
        humanoidState.lightCoords = packedLight;
        GeoRenderState geoState = (GeoRenderState) humanoidState;
        this.renderer.fillRenderState((FrogHelmetItem) headItem.getItem(), new GeoArmorRenderer.RenderData(headItem, EquipmentSlot.HEAD, wolf, this.baseModel),
                geoState, renderState.partialTick);

        poseStack.pushPose();
        try {
            ((ModelPart) HEAD_FIELD.get(this.getParentModel())).translateAndRotate(poseStack);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        poseStack.translate(0.05D, 0.15D, -0.02D);
        poseStack.scale(1F, 1F, 1F);
        poseStack.mulPose(Axis.XP.rotationDegrees(0.0F));
        // cameraState only reaches GeoRenderLayers/pre-post-render hooks, none of which this
        // renderer uses, so an empty one is fine here - RenderLayer#submit isn't handed a real one.
        this.renderer.submitRenderTasks(geoState, poseStack, submitNodeCollector, new CameraRenderState(), null);
        poseStack.popPose();
    }
}
