package net.winepicfin.extrabiomes.platform;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.winepicfin.extrabiomes.block.custom.ModLogs;
import net.winepicfin.extrabiomes.block.custom.StickPileBlock;

public class ExtraBiomesExpectPlatformImpl {
    public static Block createLogBlock(BlockBehaviour.Properties properties) {
        return new ModLogs(properties);
    }

    public static Block createStickPileBlock(BlockBehaviour.Properties properties) {
        return new StickPileBlock(properties);
    }

    public static WoodType registerWoodType(WoodType woodType) {
        return WoodType.register(woodType);
    }

    public static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(Codec<P> codec) {
        return new TreeDecoratorType<>(codec);
    }

    public static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(Codec<P> codec) {
        return new TrunkPlacerType<>(codec);
    }
}
