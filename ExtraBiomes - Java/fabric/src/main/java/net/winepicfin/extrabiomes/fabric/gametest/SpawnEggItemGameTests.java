package net.winepicfin.extrabiomes.fabric.gametest;

import net.minecraft.core.Holder;
import net.minecraft.world.item.SpawnEggItem;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.item.custom.ExtraBiomesSpawnEggItem;
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

    @GameTest(structure = ExtraBiomes.MOD_ID + ":empty")
    public void everySpawnEggResolvesRequiredFeaturesWithoutThrowing(GameTestHelper helper) {
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

            helper.assertTrue(item instanceof ExtraBiomesSpawnEggItem, Component.literal(item + " is not an ExtraBiomesSpawnEggItem"));

            // The old-architecture regression: this used to throw NullPointerException
            // (null.requiredFeatures()) for every one of these, because the old
            // ExtraBiomesSpawnEggItem wrapper permanently left vanilla SpawnEggItem's private
            // `defaultType` field null. Calling it here is exactly what
            // CreativeModeTabs$Rebuilder.buildContents does for every registered item.
            item.requiredFeatures();

            // Placing an egg goes through vanilla's static getType, which only reads ENTITY_DATA.
            EntityType<?> resolvedType = SpawnEggItem.getType(new ItemStack(item));
            helper.assertTrue(resolvedType == expectedType,
                    Component.literal(item + ": SpawnEggItem.getType(new ItemStack) returned " + resolvedType + ", expected " + expectedType));
            helper.assertTrue(SpawnEggItem.byId(expectedType).map(Holder::value).orElse(null) == item,
                    Component.literal("SpawnEggItem.byId(" + expectedType + ") did not resolve back to " + item));

            // Regression coverage for the BY_ID map-collision bug: every one of this mod's spawn
            // eggs used to construct with a null EntityType and collide on that single map slot in
            // vanilla's SpawnEggItem.BY_ID, so looking up any of their entity types returned
            // whichever egg happened to register last. Vanilla's SpawnEggItem constructor no longer
            // accepts an EntityType at all (1.21.10), so BY_ID is never populated for these eggs -
            // MobPickResultMixin instead consults ExtraBiomesSpawnEggItem.byType() for the
            // pick-block path, so that's what's asserted here too.
            helper.assertTrue(ExtraBiomesSpawnEggItem.byType(expectedType) == item,
                    Component.literal("ExtraBiomesSpawnEggItem.byType(" + expectedType + ") did not resolve back to " + item));
        }

        LOGGER.info("[SpawnEggItemGameTests] everySpawnEggResolvesRequiredFeaturesWithoutThrowing: passed");
        helper.succeed();
    }
}
