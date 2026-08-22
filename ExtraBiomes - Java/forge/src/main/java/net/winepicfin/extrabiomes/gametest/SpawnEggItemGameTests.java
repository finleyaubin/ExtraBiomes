package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.item.custom.ExtraBiomesSpawnEggItem;
import org.slf4j.Logger;

import java.util.List;

// Regression coverage for the requiredFeatures() NPE ExtraBiomesSpawnEggItem's class javadoc
// documents (vanilla SpawnEggItem reads a private `defaultType` field this class permanently
// leaves null, so before requiredFeatures() was overridden here, every single one of this mod's
// spawn eggs NPE'd the moment CreativeModeTab rebuilt its contents - which happens unconditionally
// at startup for every registered item, not just when a player opens the tab). This can't be a
// plain JUnit test: constructing a real Item subtype outside a running loader needs the registry
// to be in its writable, intrusive-holder-tracking state (Item's own constructor calls
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

    @GameTest(template = "empty")
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
            // The regression: this used to throw NullPointerException (null.requiredFeatures())
            // for every one of these. Calling it here is exactly what
            // CreativeModeTabs$Rebuilder.buildContents does for every registered item.
            item.requiredFeatures();
            // getType(null) is the other path documented on that class - the tag-less fallback
            // vanilla's own crafting/inventory code exercises constantly (e.g. rendering the item
            // in a creative tab, which needs an EntityType to pick the egg's overlay color).
            helper.assertTrue(((ExtraBiomesSpawnEggItem) item).getType(null) != null,
                    item + "#getType(null) returned null");
        }

        LOGGER.info("[SpawnEggItemGameTests] everySpawnEggResolvesRequiredFeaturesWithoutThrowing: passed");
        helper.succeed();
    }
}
