package net.winepicfin.extrabiomes.fabric.entity.client.state;

import net.minecraft.world.entity.animal.Wolf;
import org.jetbrains.annotations.Nullable;

// Implemented by a mixin onto WolfRenderState (see fabric/.../mixin/WolfRenderStateMixin) so
// WolfFrogHatLayer can recover the Wolf being rendered - the 1.21.2+ render-state split means
// RenderLayer#render only receives the immutable WolfRenderState snapshot, not the entity, but
// GeckoLib's GeoArmorRenderer#prepForRender still needs a live Entity reference.
public interface WolfRenderStateExtension {
    void extrabiomes$setWolf(@Nullable Wolf wolf);

    @Nullable
    Wolf extrabiomes$getWolf();
}
