package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// WoodType.register(WoodType) is private in vanilla, widened via Forge's access transformer on
// that loader - this static invoker mixin is Fabric's equivalent, used by
// platform/fabric/ExtraBiomesExpectPlatformImpl#registerWoodType.
@Mixin(WoodType.class)
public interface WoodTypeAccessor {
    @Invoker("register")
    static WoodType invokeRegister(WoodType type) {
        throw new AssertionError();
    }
}
