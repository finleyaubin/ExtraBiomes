package net.winepicfin.extrabiomes.fabric.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// TrunkPlacerType's constructor is private in vanilla 1.20.6 (it also switched from Codec to
// MapCodec) - Forge's access transformer widens it, this static invoker mixin is Fabric's
// equivalent, used by platform/fabric/ExtraBiomesExpectPlatformImpl#createTrunkPlacerType.
@Mixin(TrunkPlacerType.class)
public interface TrunkPlacerTypeAccessor {
    @Invoker("<init>")
    static <P extends TrunkPlacer> TrunkPlacerType<P> invokeNew(MapCodec<P> codec) {
        throw new AssertionError();
    }
}
