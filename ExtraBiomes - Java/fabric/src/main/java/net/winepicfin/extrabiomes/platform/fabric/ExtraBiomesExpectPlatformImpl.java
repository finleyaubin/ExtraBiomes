package net.winepicfin.extrabiomes.platform.fabric;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.material.FlowingFluid;
import net.winepicfin.extrabiomes.fabric.fluid.ModFluids;
import net.winepicfin.extrabiomes.fabric.mixin.WoodTypeAccessor;

// Fabric's counterpart to forge/.../platform/forge/ExtraBiomesExpectPlatformImpl.java. Unlike
// Forge, plain vanilla Minecraft (Fabric's compile target) never patches WoodType.register,
// TreeDecoratorType/TrunkPlacerType, or LiquidBlock/BucketItem to widen/add anything - they're
// already public/directly constructible, so most of these are simpler here than on Forge. Only
// the log/stick-pile block and the fluid need genuinely different (not just simpler) Fabric-side
// types - see fabric/.../fluid/GooFluid.java and this module's ModVanillaCompat-equivalent (task:
// axe stripping/flammability) for why.
public class ExtraBiomesExpectPlatformImpl {
    public static Block createLogBlock(BlockBehaviour.Properties properties) {
        return new RotatedPillarBlock(properties);
    }

    public static Block createStickPileBlock(BlockBehaviour.Properties properties) {
        return new RotatedPillarBlock(properties);
    }

    public static WoodType registerWoodType(WoodType woodType) {
        return WoodTypeAccessor.invokeRegister(woodType);
    }

    public static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(Codec<P> codec) {
        return new TreeDecoratorType<>(codec);
    }

    public static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(Codec<P> codec) {
        return new TrunkPlacerType<>(codec);
    }

    public static LiquidBlock createGooLiquidBlock(BlockBehaviour.Properties properties) {
        return new LiquidBlock((FlowingFluid) ModFluids.SOURCE_GOO.get(), properties);
    }

    public static Item createBucketOfGooItem(Item.Properties properties) {
        return new BucketItem(ModFluids.SOURCE_GOO.get(), properties);
    }

    public static Item createFrogHelmetItem(ArmorMaterial material, ArmorItem.Type type, Item.Properties properties) {
        return new net.winepicfin.extrabiomes.fabric.item.custom.FrogHelmetItem(material, type, properties);
    }
}
