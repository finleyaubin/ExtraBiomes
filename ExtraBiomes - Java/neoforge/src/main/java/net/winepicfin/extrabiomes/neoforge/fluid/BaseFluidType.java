package net.winepicfin.extrabiomes.neoforge.fluid;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int tintColour;
    private final Vector3f fogColour;

    public BaseFluidType(final Identifier stillTexture, final Identifier flowingTexture, final Identifier overlayTexture, final int tintColor, final Vector3f fogColor, final Properties properties) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColour = tintColor;
        this.fogColour = fogColor;
    }

    @Override
    public Identifier getStillTexture() {
        return stillTexture;
    }

    @Override
    public Identifier getFlowingTexture() {
        return flowingTexture;
    }

    @Override
    public Identifier getOverlayTexture() {
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

    // FogParameters (start/end/shape/color record) is gone as of the 1.21.6 fog rework - FogData is a
    // mutable holder the vanilla environment already populated, so this just tightens its distances.
    @Override
    public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
        fogData.environmentalStart = 0.6f;
        fogData.environmentalEnd = 3f;
    }
}
