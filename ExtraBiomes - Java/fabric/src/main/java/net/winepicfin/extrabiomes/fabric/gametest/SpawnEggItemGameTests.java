package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Supplier;

// Fabric equivalent of neoforge/gametest/SpawnEggItemGameTests.java - see that class for why this
// has to be a GameTest reusing the mod's own live registered items rather than a plain JUnit test
// constructing a fresh one (Item's constructor needs the registry in its writable
// intrusive-holder-tracking state, which only exists during a real loader's own registration
// pass). ExtraBiomesExpectPlatformImpl#createSpawnEggItem is loader-specific but structurally
// identical on both platforms, so the bug it fixes (and this regression test) applies identically
// here - nothing below is Fabric-specific, this is just a second real runtime to catch a
// regression in either loader's own item registration wiring.
public class SpawnEggItemGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    private record SpawnEgg(Item item, Supplier<? extends EntityType<? extends Mob>> expectedType) {
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
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
            // (null.requiredFeatures()) for every one of these, because the old
            // ExtraBiomesSpawnEggItem wrapper permanently left vanilla SpawnEggItem's private
            // `defaultType` field null. Calling it here is exactly what
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
            // whichever egg happened to register last. MobPickResultMixin already switched to
            // vanilla's SpawnEggItem.byId() for the pick-block path in the same fix that removed
            // ExtraBiomesSpawnEggItem.byType(), so testing byId() directly here covers that
            // pick-block path too, without needing the old byType()-based check this test used to
            // have.
            helper.assertTrue(SpawnEggItem.byId(expectedType) == item,
                    "SpawnEggItem.byId(" + expectedType + ") did not resolve back to " + item);
        }

        LOGGER.info("[SpawnEggItemGameTests] everySpawnEggResolvesRequiredFeaturesWithoutThrowing: passed");
        helper.succeed();
    }
}
