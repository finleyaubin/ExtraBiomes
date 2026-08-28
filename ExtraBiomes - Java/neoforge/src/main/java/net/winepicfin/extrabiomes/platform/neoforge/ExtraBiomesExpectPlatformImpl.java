package net.winepicfin.extrabiomes.platform.neoforge;

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
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.fml.ModList;
import net.winepicfin.extrabiomes.neoforge.block.custom.ModLogs;
import net.winepicfin.extrabiomes.neoforge.block.custom.StickPileBlock;
import net.winepicfin.extrabiomes.neoforge.fluid.ModFluids;
import net.winepicfin.extrabiomes.neoforge.item.custom.FrogHelmetItem;

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

    public static Item createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        return new DeferredSpawnEggItem(typeSupplier, backgroundColor, highlightColor, properties);
    }

    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded("create");
    }

    public static void applyWindmillCreateCompat(WorldGenLevel level, BoundingBox box) {
    }
}
