package net.winepicfin.extrabiomes.platform.fabric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FlowingFluid;
import net.winepicfin.extrabiomes.fabric.fluid.ModFluids;
import net.winepicfin.extrabiomes.fabric.mixin.WoodTypeAccessor;

import java.util.function.Supplier;

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

    public static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(MapCodec<P> codec) {
        return net.winepicfin.extrabiomes.fabric.mixin.TreeDecoratorTypeAccessor.invokeNew(codec);
    }

    public static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(MapCodec<P> codec) {
        return net.winepicfin.extrabiomes.fabric.mixin.TrunkPlacerTypeAccessor.invokeNew(codec);
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

    public static Item createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        EntityType<? extends Mob> type = typeSupplier.get();
        if (type == null) {
            throw new IllegalStateException("EntityType for spawn egg was null - registration order issue? ModEntities.register() must be called before ModItems.register()");
        }
        return new SpawnEggItem(type, backgroundColor, highlightColor, properties);
    }

    public static boolean isCreateLoaded() {
        return FabricLoader.getInstance().isModLoaded("create");
    }

    // CreateWindmillCompat itself is excluded from compilation (see fabric/build.gradle) - no
    // 1.20.2 Create-Fabric build exists yet (as of 2026-08-28), so the modCompileOnly Create-Fabric
    // dependency it needs isn't available. isCreateLoaded() above will always report false until
    // Create-Fabric ships a 1.20.2 build and both this method and the build.gradle exclusion are
    // reverted.
    public static void applyWindmillCreateCompat(WorldGenLevel level, BoundingBox box) {
    }
}
