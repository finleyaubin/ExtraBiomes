package net.winepicfin.extrabiomes.worldgen.tree.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModTreeDecoratorTypes {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.TREE_DECORATOR_TYPE);
    public static final RegistrySupplier<TreeDecoratorType<CaveVineTreeDecorator>> CAVE_VINE_TREE_DECORATOR = TREE_DECORATOR.register("cave_vine_tree_decorator", () -> new TreeDecoratorType<>(CaveVineTreeDecorator.CODEC));

    public static void register() {
        TREE_DECORATOR.register();
    }
}
