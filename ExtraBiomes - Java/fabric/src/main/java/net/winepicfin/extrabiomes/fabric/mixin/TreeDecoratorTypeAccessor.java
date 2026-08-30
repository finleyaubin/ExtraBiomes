package net.winepicfin.extrabiomes.fabric.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// TreeDecoratorType's constructor is private in vanilla 1.20.6 (it also switched from Codec to
// MapCodec) - Forge's access transformer widens it, this static invoker mixin is Fabric's
// equivalent, used by platform/fabric/ExtraBiomesExpectPlatformImpl#createTreeDecoratorType.
@Mixin(TreeDecoratorType.class)
public interface TreeDecoratorTypeAccessor {
    @Invoker("<init>")
    static <P extends TreeDecorator> TreeDecoratorType<P> invokeNew(MapCodec<P> codec) {
        throw new AssertionError();
    }
}
