package net.winepicfin.extrabiomes.platform;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
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

    // Goo's LiquidBlock/BucketItem need the Supplier<? extends Fluid> constructor overloads Forge patches onto these vanilla classes, which aren't present on plain vanilla that common compiles against.
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
