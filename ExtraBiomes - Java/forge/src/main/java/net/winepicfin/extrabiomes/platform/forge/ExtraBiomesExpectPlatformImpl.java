package net.winepicfin.extrabiomes.platform.forge;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.ModList;
import net.winepicfin.extrabiomes.forge.block.custom.ModLogs;
import net.winepicfin.extrabiomes.forge.block.custom.StickPileBlock;
import net.winepicfin.extrabiomes.forge.fluid.ModFluids;
import net.winepicfin.extrabiomes.forge.item.custom.FrogHelmetItem;

import java.util.function.Supplier;

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

    public static LiquidBlock createGooLiquidBlock(BlockBehaviour.Properties properties) {
        return new LiquidBlock(ModFluids.SOURCE_GOO, properties);
    }

    public static Item createBucketOfGooItem(Item.Properties properties) {
        return new BucketItem(ModFluids.SOURCE_GOO, properties);
    }

    public static Item createFrogHelmetItem(ArmorMaterial material, ArmorItem.Type type, Item.Properties properties) {
        return new FrogHelmetItem(material, type, properties);
    }

    // ForgeSpawnEggItem is Forge's own purpose-built replacement for exactly this Supplier-based
    // registration pattern - unlike the common module's ExtraBiomesSpawnEggItem, it registers itself
    // into ForgeSpawnEggItem's own type->egg lookup at FMLCommonSetupEvent, which is what
    // IForgeEntity#getPickedResult (the real pick-block entry point) falls back to whenever a mob's
    // vanilla getPickResult() misses. Fixes pick-block on every custom mob for free, no per-entity code.
    public static Item createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        return new ForgeSpawnEggItem(typeSupplier, backgroundColor, highlightColor, properties);
    }

    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded("create");
    }

    // CreateWindmillCompat itself is excluded from compilation (see forge/build.gradle) - no
    // 1.20.2 Create build exists yet (as of 2026-08-28), so the compileOnly Create dependency it
    // needs isn't available. isCreateLoaded() above will always report false until Create ships
    // a 1.20.2 build and both this method and the build.gradle exclusion are reverted.
    public static void applyWindmillCreateCompat(WorldGenLevel level, BoundingBox box) {
    }
}
