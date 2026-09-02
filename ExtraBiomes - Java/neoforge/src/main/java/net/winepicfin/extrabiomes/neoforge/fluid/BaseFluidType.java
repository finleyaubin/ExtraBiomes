package net.winepicfin.extrabiomes.neoforge.fluid;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Basic implementation of {@link FluidType} that supports specifying still and flowing textures in the constructor.
 *
 * @author Choonster (<a href="https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.19.x/LICENSE.txt">MIT License</a>)
 *
 * Change by: Kaupenjoe and WinEpicFin
 * Added overlayTexture and tintColor as well. Also converts tint color into fog color
 */

// FluidType no longer has an initializeClient(Consumer) hook as of this NeoForge line - client
// extensions for blocks/items/fluid types all register centrally through
// RegisterClientExtensionsEvent now (see ModEventBusClientEvents#registerClientExtensions), so
// this class implements IClientFluidTypeExtensions itself and is handed to that event directly.
public class BaseFluidType extends FluidType implements IClientFluidTypeExtensions {
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
    private final int tintColour;
    private final Vector3f fogColour;

    public BaseFluidType(final ResourceLocation stillTexture, final ResourceLocation flowingTexture, final ResourceLocation overlayTexture, final int tintColor, final Vector3f fogColor, final Properties properties) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColour = tintColor;
        this.fogColour = fogColor;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    @Override
    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    @Override
    public int getTintColor() {
        return tintColour;
    }

    @Override
    public @NotNull Vector4f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
        return new Vector4f(fogColour.x, fogColour.y, fogColour.z, fluidFogColor.w());
    }

    @Override
    public @NotNull FogParameters modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, FogParameters fogParameters) {
        return new FogParameters(0.6f, 3f, fogParameters.shape(), fogParameters.red(), fogParameters.green(), fogParameters.blue(), fogParameters.alpha());
    }
}
