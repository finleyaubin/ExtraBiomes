package net.winepicfin.extrabiomes.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.winepicfin.extrabiomes.testutil.JavaDatapackJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Unlike EntityLootTableParityTest, ExtraBiomes - Bedrock only has a handful of block loot tables
// (packs/BP/loot_tables/blocks/) and the ones that do exist use schemas too different from Java's
// to reuse LootTableParity's comparison logic unmodified - Bedrock's ore tables enumerate one
// match_tool condition per pickaxe tier (iron/gold/diamond/netherite) where Java's single
// alternatives entry applies a fortune-scaled apply_bonus function instead, and Bedrock's slab
// table has no equivalent of Java's block_state_property(type=double) condition at all. Reconciling
// those would need new comparison logic, not just normalizing wrapper-key noise like the entity
// version does - out of scope here. Instead, this is a structural check in the same spirit as
// BiomeRegistrationParityTest: it reads ModBlockLootTableEntries' own generated datagen output
// (`./gradlew :forge:runData` first if stale) and asserts each of these non-trivial (i.e. not a
// bare dropSelf) entries actually wires the loot content the source code intends, catching e.g. a
// wrong Items.* argument or a swapped block/item reference that dropSelf-only coverage can't.
class BlockLootTableGenerationTest {
    private static final String BLOCKS_DIR = "src/generated/resources/data/extrabiomes/loot_tables/blocks/";

    @Test
    void netherDiamondOreDropsDiamondWithFortuneOrOreWithSilkTouch() {
        JsonObject table = JavaDatapackJson.load(BLOCKS_DIR + "nether_diamond_ore.json");
        JsonArray children = table.get("pools").getAsJsonArray().get(0).getAsJsonObject()
                .get("entries").getAsJsonArray().get(0).getAsJsonObject().get("children").getAsJsonArray();

        assertEquals(2, children.size());
        assertEquals("extrabiomes:nether_diamond_ore", children.get(0).getAsJsonObject().get("name").getAsString(),
                "silk touch branch should drop the ore block itself");
        assertEquals("minecraft:diamond", children.get(1).getAsJsonObject().get("name").getAsString(),
                "non-silk-touch branch should drop vanilla diamond (the Items.DIAMOND argument to createOreDrop)");
    }

    @Test
    void mysticSlabDropsDoubleCountWhenBrokenAsDoubleSlab() {
        JsonObject table = JavaDatapackJson.load(BLOCKS_DIR + "mystic_slab.json");
        JsonObject entry = table.get("pools").getAsJsonArray().get(0).getAsJsonObject()
                .get("entries").getAsJsonArray().get(0).getAsJsonObject();

        assertEquals("extrabiomes:mystic_slab", entry.get("name").getAsString());
        JsonObject setCount = entry.get("functions").getAsJsonArray().get(0).getAsJsonObject();
        assertEquals("minecraft:set_count", setCount.get("function").getAsString());
        assertEquals(2.0, setCount.get("count").getAsDouble(), 1e-9,
                "createSlabItemTable should double the drop when the slab block state is \"double\"");
    }

    @Test
    void dropSelfBlockDropsItself() {
        // DENSE_CLOUD is a plain dropSelf.accept(...) entry - the simplest possible case, included
        // as a sanity baseline for the JavaDatapackJson plumbing itself rather than because
        // dropSelf has any branching logic worth guarding.
        JsonObject table = JavaDatapackJson.load(BLOCKS_DIR + "dense_cloud.json");
        String name = table.get("pools").getAsJsonArray().get(0).getAsJsonObject()
                .get("entries").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
        assertEquals("extrabiomes:dense_cloud", name);
    }

    @Test
    void wallHangingSignDropsTheStandingSignItemNotTheWallBlockItself() {
        // MYSTIC_WALL_HANGING_SIGN has no item of its own (placing the standing sign against a
        // wall converts it to this block automatically) - createSingleItemTable is used precisely
        // so breaking the wall variant still gives back the item players actually placed.
        JsonObject table = JavaDatapackJson.load(BLOCKS_DIR + "mystic_wall_hanging_sign.json");
        String name = table.get("pools").getAsJsonArray().get(0).getAsJsonObject()
                .get("entries").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
        assertEquals("extrabiomes:mystic_hanging_sign", name);
    }

    @Test
    void wallSignDropsTheStandingSignItemNotTheWallBlockItself() {
        JsonObject table = JavaDatapackJson.load(BLOCKS_DIR + "mystic_wall_sign.json");
        String name = table.get("pools").getAsJsonArray().get(0).getAsJsonObject()
                .get("entries").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
        assertEquals("extrabiomes:mystic_sign", name);
    }

    // Deliberately NOT asserted here: ModBlocks.MYSTIC_LEAVES/SKY_LEAVES/PALM_LEAVES's
    // createLeavesDrops call (ModBlockLootTableEntries lines ~66/86/106) passes the leaves block
    // itself as the saplingBlock argument instead of ModBlocks.*_SAPLING - confirmed against the
    // generated JSON, all three leaves tables' non-silk-touch alternatives branch names the leaves
    // block a second time instead of the sapling, so none of them can ever drop a sapling. Left
    // unasserted (rather than encoded as an expected-failure) since fixing production code is out
    // of scope for this test pass - see the session's final report for this bug.
}
