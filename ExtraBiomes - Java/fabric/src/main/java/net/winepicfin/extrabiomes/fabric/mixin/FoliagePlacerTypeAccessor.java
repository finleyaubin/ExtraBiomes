package net.winepicfin.extrabiomes.fabric.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// FoliagePlacerType's constructor is private in vanilla 1.21.3 (same as TrunkPlacerType's).
@Mixin(FoliagePlacerType.class)
public interface FoliagePlacerTypeAccessor {
    @Invoker("<init>")
    static <P extends FoliagePlacer> FoliagePlacerType<P> invokeNew(MapCodec<P> codec) {
        throw new AssertionError();
    }
}
