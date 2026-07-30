package net.winepicfin.extrabiomes.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.JellyfishEntity;
import org.jetbrains.annotations.NotNull;

public class JellyfishRenderer extends MobRenderer<JellyfishEntity, JellyfishModel<JellyfishEntity>> {
    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/jellyfish.png"),
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/jellyfish1.png"),
    };

    public JellyfishRenderer(EntityRendererProvider.Context context) {
        super(context, new JellyfishModel<>(context.bakeLayer(ModModelLayers.JELLYFISH)), 0.4f);
    }

    @Override
    protected void scale(JellyfishEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2f, 1.2f, 1.2f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(JellyfishEntity entity) {
        return TEXTURES[Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1)];
    }
}
