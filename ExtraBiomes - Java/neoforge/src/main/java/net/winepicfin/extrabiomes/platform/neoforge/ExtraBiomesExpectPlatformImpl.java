package net.winepicfin.extrabiomes.platform.neoforge;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.fml.ModList;
import net.winepicfin.extrabiomes.neoforge.block.custom.ModLogs;
import net.winepicfin.extrabiomes.neoforge.block.custom.StickPileBlock;
import net.winepicfin.extrabiomes.neoforge.fluid.ModFluids;
import net.winepicfin.extrabiomes.neoforge.item.custom.FrogHelmetItem;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
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

    // TreeDecoratorType/TrunkPlacerType's constructors are private in vanilla 1.20.6, and NeoForge
    // 20.6.139's own accesstransformer.cfg still targets the old Codec-typed constructor (it wasn't
    // updated for the MapCodec rework), so its public-ification never applies here - reflection is
    // the only way left to construct a custom entry.
    @SuppressWarnings("unchecked")
    public static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(MapCodec<P> codec) {
        try {
            Constructor<TreeDecoratorType> constructor = TreeDecoratorType.class.getDeclaredConstructor(MapCodec.class);
            constructor.setAccessible(true);
            return (TreeDecoratorType<P>) constructor.newInstance(codec);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(MapCodec<P> codec) {
        try {
            Constructor<TrunkPlacerType> constructor = TrunkPlacerType.class.getDeclaredConstructor(MapCodec.class);
            constructor.setAccessible(true);
            return (TrunkPlacerType<P>) constructor.newInstance(codec);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    // Same private-constructor-via-reflection idiom as createTreeDecoratorType/createTrunkPlacerType
    // above - BlockEntityType's constructor (and its own register() factory) went private in 1.21.2
    // with vanilla's bootstrap as the only intended caller, and NeoForge's AT doesn't widen it.
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityType.BlockEntitySupplier<T> factory, Block... validBlocks) {
        try {
            Constructor<BlockEntityType> constructor = BlockEntityType.class.getDeclaredConstructor(BlockEntityType.BlockEntitySupplier.class, Set.class);
            constructor.setAccessible(true);
            Set<Block> blocks = new LinkedHashSet<>(Arrays.asList(validBlocks));
            return (BlockEntityType<T>) constructor.newInstance(factory, blocks);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static LiquidBlock createGooLiquidBlock(BlockBehaviour.Properties properties) {
        return new LiquidBlock(ModFluids.SOURCE_GOO.get(), properties);
    }

    public static Item createBucketOfGooItem(Item.Properties properties) {
        return new BucketItem(ModFluids.SOURCE_GOO.get(), properties);
    }

    public static Item createFrogHelmetItem(ArmorMaterial material, ArmorType type, Item.Properties properties) {
        return new FrogHelmetItem(material, type, properties);
    }

    public static Item createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        EntityType<? extends Mob> type = typeSupplier.get();
        if (type == null) {
            throw new IllegalStateException("EntityType for spawn egg was null - registration order issue? ModEntities.register() must be called before ModItems.register()");
        }
        return new SpawnEggItem(type, backgroundColor, highlightColor, properties);
    }

    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded("create");
    }

    // CreateWindmillCompat itself is excluded from compilation (see neoforge/build.gradle) - no
    // 1.21.3 Create build exists yet (as of 2026-09-01), so the compileOnly Create dependency it
    // needs isn't available. isCreateLoaded() above will always report false until Create ships
    // a 1.21.3 build and both this method and the build.gradle exclusion are reverted.
    public static void applyWindmillCreateCompat(WorldGenLevel level, BoundingBox box) {
    }
}
