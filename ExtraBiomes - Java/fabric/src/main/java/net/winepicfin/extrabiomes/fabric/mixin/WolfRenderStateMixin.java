package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.world.entity.animal.Wolf;
import net.winepicfin.extrabiomes.fabric.entity.client.state.WolfRenderStateExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WolfRenderState.class)
public class WolfRenderStateMixin implements WolfRenderStateExtension {
    @Unique
    @Nullable
    private Wolf extrabiomes$wolf;

    @Override
    public void extrabiomes$setWolf(@Nullable Wolf wolf) {
        this.extrabiomes$wolf = wolf;
    }

    @Override
    @Nullable
    public Wolf extrabiomes$getWolf() {
        return this.extrabiomes$wolf;
    }
}
