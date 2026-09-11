package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.world.entity.animal.Wolf;
import net.winepicfin.extrabiomes.fabric.entity.client.state.WolfRenderStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Captures the Wolf being rendered onto its WolfRenderState (via WolfRenderStateMixin) so
// WolfFrogHatLayer can recover it - see WolfRenderStateExtension for why this is needed.
@Mixin(WolfRenderer.class)
public abstract class WolfRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/Wolf;Lnet/minecraft/client/renderer/entity/state/WolfRenderState;F)V", at = @At("TAIL"))
    private void extrabiomes$captureWolf(Wolf wolf, WolfRenderState state, float partialTick, CallbackInfo ci) {
        ((WolfRenderStateExtension) state).extrabiomes$setWolf(wolf);
    }
}
