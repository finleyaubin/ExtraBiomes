package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.PiranhaEntity;
import org.jetbrains.annotations.NotNull;

public class PiranhaRenderer extends MobRenderer<PiranhaEntity, PiranhaModel<PiranhaEntity>> {
    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha.png"),
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha2.png"),
            ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "textures/entity/fish/piranha3.png"),
    };

    public PiranhaRenderer(EntityRendererProvider.Context context) {
        super(context, new PiranhaModel<>(context.bakeLayer(ModModelLayers.PIRANHA)), 0.2f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PiranhaEntity entity) {
        return TEXTURES[Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1)];
    }
}
