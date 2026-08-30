package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Supplier;

// Regression coverage for the spawn-egg construction fix in
// ExtraBiomesExpectPlatformImpl#createSpawnEggItem: this mod's spawn eggs used to be
// net.neoforged.neoforge.common.DeferredSpawnEggItem, a lazy-EntityType wrapper whose
// requiredFeatures() NPE'd the moment CreativeModeTab rebuilt its contents (which happens
// unconditionally at startup for every registered item) because vanilla SpawnEggItem's private
// `defaultType` field was never actually populated for that wrapper. createSpawnEggItem now
// eagerly resolves a real EntityType and constructs a plain vanilla SpawnEggItem instead, so this
// test validates the new invariants that fix depends on: a real, correct EntityType backs every
// egg (not a null/lazy placeholder), and each egg is properly registered in vanilla's own
// SpawnEggItem.BY_ID map rather than all 8 eggs colliding on one map slot the way they did when
// every one of them constructed with a null EntityType. This can't be a plain JUnit test:
// constructing a real Item subtype outside a running loader needs the registry to be in its
// writable, intrusive-holder-tracking state (Item's own constructor calls
// BuiltInRegistries.ITEM.createIntrusiveHolder(this)), which only exists during a mod's actual
// RegisterEvent-driven registration - confirmed by attempting it directly in common/src/test,
// where even a bare vanilla `new Item(...)` throws IllegalStateException("This registry can't
// create intrusive holders") once Bootstrap.bootStrap() has run, and Forge's own unfreezing API
// (GameData.unfreezeData()) isn't on common's compile classpath by design (common stays
// loader-agnostic). A GameTest sidesteps this entirely by reusing the mod's own already-registered
// live items instead of constructing new ones.
@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class SpawnEggItemGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    private record SpawnEgg(Item item, Supplier<? extends EntityType<? extends Mob>> expectedType) {
    }

    @GameTest(template = "empty", batch = "extrabiomes")
    public static void everySpawnEggResolvesRequiredFeaturesWithoutThrowing(GameTestHelper helper) {
        LOGGER.info("[SpawnEggItemGameTests] everySpawnEggResolvesRequiredFeaturesWithoutThrowing: starting");
        List<SpawnEgg> spawnEggs = List.of(
                new SpawnEgg(ModItems.PUCKOO_SPAWN_EGG.get(), ModEntities.PUCKOO),
                new SpawnEgg(ModItems.WORM_SPAWN_EGG.get(), ModEntities.WORM),
                new SpawnEgg(ModItems.TREEFROG_SPAWN_EGG.get(), ModEntities.TREEFROG),
                new SpawnEgg(ModItems.HOPPLESHROOM_SPAWN_EGG.get(), ModEntities.HOPPLESHROOM),
                new SpawnEgg(ModItems.GIANT_TORTOISE_SPAWN_EGG.get(), ModEntities.GIANT_TORTOISE),
                new SpawnEgg(ModItems.JELLYFISH_SPAWN_EGG.get(), ModEntities.JELLYFISH),
                new SpawnEgg(ModItems.PIRANHA_SPAWN_EGG.get(), ModEntities.PIRANHA),
                new SpawnEgg(ModItems.HARPY_SPAWN_EGG.get(), ModEntities.HARPY));

        for (SpawnEgg egg : spawnEggs) {
            Item item = egg.item();
            EntityType<? extends Mob> expectedType = egg.expectedType().get();
            LOGGER.info("[SpawnEggItemGameTests] checking {}", item);

            helper.assertTrue(item instanceof SpawnEggItem, item + " is not a SpawnEggItem");
            SpawnEggItem spawnEgg = (SpawnEggItem) item;

            // The old-architecture regression: this used to throw NullPointerException
            // (null.requiredFeatures()) for every one of these, because the wrapper's
            // EntityType was never actually populated. Calling it here is exactly what
            // CreativeModeTabs$Rebuilder.buildContents does for every registered item.
            item.requiredFeatures();

            // ItemStack.EMPTY (not a literal null) is the tag-less fallback vanilla's own
            // crafting/inventory code actually passes (e.g. rendering the item in a creative tab,
            // which needs an EntityType to pick the egg's overlay color). Asserted against the
            // specific EntityType each egg is expected to carry, not just non-null, so a mix-up
            // between two eggs (e.g. two eggs both resolving to the same EntityType) would
            // actually be caught here.
            EntityType<?> resolvedType = spawnEgg.getType(ItemStack.EMPTY);
            helper.assertTrue(resolvedType == expectedType,
                    item + "#getType(ItemStack.EMPTY) returned " + resolvedType + ", expected " + expectedType);

            // Regression coverage for the BY_ID map-collision bug: every one of this mod's spawn
            // eggs used to construct with a null EntityType and collide on that single map slot
            // in vanilla's SpawnEggItem.BY_ID, so looking up any of their entity types returned
            // whichever egg happened to register last. MobPickResultMixin resolves
            // EntityType -> egg item through SpawnEggItem.byId(), so it must actually resolve
            // back to this specific egg.
            helper.assertTrue(SpawnEggItem.byId(expectedType) == item,
                    "SpawnEggItem.byId(" + expectedType + ") did not resolve back to " + item);
        }

        LOGGER.info("[SpawnEggItemGameTests] everySpawnEggResolvesRequiredFeaturesWithoutThrowing: passed");
        helper.succeed();
    }
}
