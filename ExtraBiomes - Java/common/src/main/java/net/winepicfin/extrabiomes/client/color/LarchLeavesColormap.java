package net.winepicfin.extrabiomes.client.color;

import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.io.IOException;

/**
 * textures/colormap/larch_leaves.png: x runs from the coldest biomes (autumn gold) to the warmest
 * (summer green), y picks the shade within that so neighbouring trees differ.
 */
public final class LarchLeavesColormap {
    public static final ResourceLocation RELOAD_LISTENER_ID = new ResourceLocation(ExtraBiomes.MOD_ID, "larch_leaves_colormap");

    private static final ResourceLocation TEXTURE = new ResourceLocation(ExtraBiomes.MOD_ID, "textures/colormap/larch_leaves.png");
    private static final int SIZE = 256;
    private static final int OPAQUE = 0xFF000000;

    private static int[] pixels = new int[SIZE * SIZE];

    public static int colorAt(float warmth, float variation) {
        int x = Math.round(Mth.clamp(warmth, 0.0F, 1.0F) * (SIZE - 1));
        int y = Math.round(Mth.clamp(variation, 0.0F, 1.0F) * (SIZE - 1));
        return pixels[y * SIZE + x] | OPAQUE;
    }

    public static void reload(ResourceManager resourceManager) {
        try {
            pixels = LegacyStuffWrapper.getPixels(resourceManager, TEXTURE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load larch leaves color texture", e);
        }
    }

    private LarchLeavesColormap() {
    }
}
