package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Fabric equivalent of forge/src/main/resources/META-INF/accesstransformer.cfg's WolfModel.head
// widening - Fabric has no AT mechanism, an accessor mixin is the direct replacement. Used by
// fabric/.../entity/client/layers/WolfFrogHatLayer to position the frog helmet on the wolf's head.
@Mixin(WolfModel.class)
public interface WolfModelAccessor {
    @Accessor("head")
    ModelPart extrabiomes$getHead();
}
