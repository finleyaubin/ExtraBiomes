package net.winepicfin.extrabiomes.platform;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * Hooks for the handful of common-module registrations that need a genuinely Forge-patched
 * class to compile: custom Block subclasses overriding Forge-only extension methods (moved to
 * forge/ as ModLogs/StickPileBlock), and vanilla registries whose constructor/registration
 * method Forge's access transformer widens from private (WoodType.register, TreeDecoratorType,
 * TrunkPlacerType) - common's compile classpath resolves plain vanilla Minecraft, so neither is
 * visible there directly. See forge/.../platform/ExtraBiomesExpectPlatformImpl for the real
 * implementations.
 */
public class ExtraBiomesExpectPlatform {
    @ExpectPlatform
    public static Block createLogBlock(BlockBehaviour.Properties properties) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Block createStickPileBlock(BlockBehaviour.Properties properties) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static WoodType registerWoodType(WoodType woodType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(Codec<P> codec) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(Codec<P> codec) {
        throw new AssertionError();
    }

    // Goo's LiquidBlock/BucketItem need the Supplier<? extends Fluid> constructor overloads
    // Forge's patches add to these vanilla classes - not present on plain vanilla, so common
    // can't call `new LiquidBlock(ModFluids.SOURCE_GOO, ...)` directly either.
    @ExpectPlatform
    public static LiquidBlock createGooLiquidBlock(BlockBehaviour.Properties properties) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Item createBucketOfGooItem(Item.Properties properties) {
        throw new AssertionError();
    }

    // FrogHelmetItem itself lives in forge/ - see its class comment for why.
    @ExpectPlatform
    public static Item createFrogHelmetItem(ArmorMaterial material, ArmorItem.Type type, Item.Properties properties) {
        throw new AssertionError();
    }
}
