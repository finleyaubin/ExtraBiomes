package net.winepicfin.extrabiomes.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.function.Supplier;

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
    public static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(MapCodec<P> codec) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(MapCodec<P> codec) {
        throw new AssertionError();
    }

    // Goo's LiquidBlock/BucketItem need the Supplier<? extends Fluid> constructor overloads Forge patches onto these vanilla classes, which aren't present on plain vanilla that common compiles against.
    @ExpectPlatform
    public static LiquidBlock createGooLiquidBlock(BlockBehaviour.Properties properties) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Item createBucketOfGooItem(Item.Properties properties) {
        throw new AssertionError();
    }

    // BlockEntityType's constructor and its own internal register() factory both went private as of
    // 1.21.2 - vanilla's own bootstrap is now the only caller. Each loader widens access the same way
    // it already does for WoodType.register/TreeDecoratorType/TrunkPlacerType above (Forge/NeoForge via
    // access transformer, Fabric via fabric-object-builder-api-v1's FabricBlockEntityTypeBuilder).
    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityType.BlockEntitySupplier<T> factory, Block... validBlocks) {
        throw new AssertionError();
    }

    // FrogHelmetItem itself lives in forge/ - see its class comment for why.
    @ExpectPlatform
    public static Item createFrogHelmetItem(ArmorMaterial material, ArmorType type, Item.Properties properties) {
        throw new AssertionError();
    }

    /**
     * On Forge, backed by {@link net.minecraftforge.common.ForgeSpawnEggItem} instead of the common
     * module's own {@link net.winepicfin.extrabiomes.item.custom.ExtraBiomesSpawnEggItem} - both work
     * around the same DeferredRegister-ordering problem (a Supplier instead of a resolved EntityType),
     * but only ForgeSpawnEggItem's own type->egg lookup is what {@code IForgeEntity#getPickedResult}
     * falls back to when a mob's vanilla getPickResult() misses (see ExtraBiomesSpawnEggItem's own
     * ALL/byType() for the equivalent Fabric-side lookup, consulted by a mixin instead).
     */
    @ExpectPlatform
    public static Item createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        throw new AssertionError();
    }

    /**
     * Whether the Create mod is loaded on this platform (Forge's ModList / Fabric's FabricLoader). Used at
     * windmill worldgen time to pick between the plain start pool and windmill_create.nbt's - see
     * {@link net.winepicfin.extrabiomes.worldgen.structure.windmill.WindmillStructure#findGenerationPoint}.
     * Safe to call unconditionally even when Create isn't on the runtime classpath at all - it's a pure
     * loader-registry lookup, never touches a Create class.
     */
    @ExpectPlatform
    public static boolean isCreateLoaded() {
        throw new AssertionError();
    }

    /**
     * Called from {@link net.winepicfin.extrabiomes.worldgen.structure.windmill.WindmillStructure#afterPlace}
     * once a windmill has finished placing, with the (chunk-restricted) box that call covers. A no-op
     * unless Create is loaded, in which case it scans that box for a placed Windmill Bearing (baked
     * directly into windmill_create.nbt - see WindmillStructures) and queues it to assemble on its own next
     * real tick, matching Create's own right-click trigger. Nothing else in the structure needs an explicit
     * trigger: the windmill bearing is the only contraption-capable block in that build - everything else
     * (shafts, cogwheels, the mechanical bearing, crushing wheels) just receives rotation through the
     * kinetic network once the windmill bearing itself starts turning, the same as it would from a player
     * right-clicking it by hand.
     * <p>
     * Both platform implementations are expected to internally check their own mod-loaded API BEFORE
     * touching any Create class, and to keep the actual Create-importing code in a separate class that
     * check only reaches when Create is present - so that class is never classloaded on a build without
     * Create at all.
     */
    @ExpectPlatform
    public static void applyWindmillCreateCompat(WorldGenLevel level, BoundingBox box) {
        throw new AssertionError();
    }
}
