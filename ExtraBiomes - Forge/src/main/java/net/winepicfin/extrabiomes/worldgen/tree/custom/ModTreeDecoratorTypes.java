package net.winepicfin.extrabiomes.worldgen.tree.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModTreeDecoratorTypes {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, ExtraBiomes.MOD_ID);
    public static final RegistryObject<TreeDecoratorType<CaveVineTreeDecorator>> CAVE_VINE_TREE_DECORATOR = TREE_DECORATOR.register("cave_vine_tree_decorator", () -> new TreeDecoratorType<>(CaveVineTreeDecorator.CODEC));

    public static void register(IEventBus eventBus){
        TREE_DECORATOR.register(eventBus);
    }
}
