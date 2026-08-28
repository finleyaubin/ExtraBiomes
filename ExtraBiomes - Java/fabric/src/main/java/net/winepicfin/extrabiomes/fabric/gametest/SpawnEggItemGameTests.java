package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.item.custom.ExtraBiomesSpawnEggItem;
import org.slf4j.Logger;

import java.util.List;

// Fabric equivalent of forge/gametest/SpawnEggItemGameTests.java - see that class for why this
// has to be a GameTest reusing the mod's own live registered items rather than a plain JUnit test
// constructing a fresh one (Item's constructor needs the registry in its writable
// intrusive-holder-tracking state, which only exists during a real loader's own registration
// pass). ExtraBiomesSpawnEggItem is common code, so the bug it fixes (and this regression test)
// applies identically on Fabric - nothing here is Forge-specific, this is just a second real
// runtime to catch a regression in either loader's own item registration wiring.
public class SpawnEggItemGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void everySpawnEggResolvesRequiredFeaturesWithoutThrowing(GameTestHelper helper) {
        LOGGER.info("[SpawnEggItemGameTests] everySpawnEggResolvesRequiredFeaturesWithoutThrowing: starting");
        List<Item> spawnEggs = List.of(
                ModItems.PUCKOO_SPAWN_EGG.get(),
                ModItems.WORM_SPAWN_EGG.get(),
                ModItems.TREEFROG_SPAWN_EGG.get(),
                ModItems.HOPPLESHROOM_SPAWN_EGG.get(),
                ModItems.GIANT_TORTOISE_SPAWN_EGG.get(),
                ModItems.JELLYFISH_SPAWN_EGG.get(),
                ModItems.PIRANHA_SPAWN_EGG.get(),
                ModItems.HARPY_SPAWN_EGG.get());

        for (Item item : spawnEggs) {
            LOGGER.info("[SpawnEggItemGameTests] checking {}", item);
            helper.assertTrue(item instanceof ExtraBiomesSpawnEggItem, item + " is not an ExtraBiomesSpawnEggItem");
            item.requiredFeatures();
            EntityType<?> type = ((ExtraBiomesSpawnEggItem) item).getType(null);
            helper.assertTrue(type != null, item + "#getType(null) returned null");
            // Regression coverage for the pick-block fix: MobPickResultMixin resolves entity type ->
            // egg item through ExtraBiomesSpawnEggItem.byType(), so it must actually find this egg.
            helper.assertTrue(ExtraBiomesSpawnEggItem.byType(type) == item,
                    "ExtraBiomesSpawnEggItem.byType(" + type + ") did not resolve back to " + item);
        }

        LOGGER.info("[SpawnEggItemGameTests] everySpawnEggResolvesRequiredFeaturesWithoutThrowing: passed");
        helper.succeed();
    }
}
