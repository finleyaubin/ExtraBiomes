package net.winepicfin.extrabiomes.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.client.state.HoppleshroomRenderState;
import net.winepicfin.extrabiomes.entity.custom.HoppleshroomEntity;
import org.jetbrains.annotations.NotNull;

public class HoppleshroomRenderer extends MobRenderer<HoppleshroomEntity, HoppleshroomRenderState, HoppleshroomModel<HoppleshroomRenderState>> {
    // Index order must stay stable — it maps to the variant int stored on the entity.
    private static final String[] COLOURS = {
            "black", "blue", "brown", "crimson", "cyan", "green",
            "orange", "purple", "red", "warped", "white", "yellow"
    };
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[COLOURS.length];

    static {
        for (int i = 0; i < COLOURS.length; i++) {
            TEXTURES[i] = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,
                    "textures/entity/hopping_spore/" + COLOURS[i] + ".png");
        }
    }

    public HoppleshroomRenderer(EntityRendererProvider.Context context) {
        super(context, new HoppleshroomModel<>(context.bakeLayer(ModModelLayers.HOPPLESHROOM)), 0.3f);
    }

    @Override
    public HoppleshroomRenderState createRenderState() {
        return new HoppleshroomRenderState();
    }

    @Override
    public void extractRenderState(HoppleshroomEntity entity, HoppleshroomRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.verticalSpeed = (float) entity.getDeltaMovement().y;
        state.squish = entity.squish;
        state.variant = Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HoppleshroomRenderState state) {
        return TEXTURES[state.variant];
    }
}
