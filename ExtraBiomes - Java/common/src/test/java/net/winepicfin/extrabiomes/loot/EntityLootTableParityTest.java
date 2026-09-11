package net.winepicfin.extrabiomes.loot;

import com.google.gson.JsonObject;
import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import net.winepicfin.extrabiomes.testutil.JavaDatapackJson;
import net.winepicfin.extrabiomes.testutil.LootTableParity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// Parity checks between ExtraBiomes - Bedrock/packs/BP/loot_tables/entities/<name>.json and the
// Java datapack loot table generated at
// src/generated/resources/data/extrabiomes/loot_table/entities/<name>.json (run `./gradlew
// runData` first if this directory is stale/missing). Both sides are plain JSON, so this needs no
// Java class at all - just the two files.
//
// Bedrock also defines boat loot tables (mystic_boat.json etc.) with no Java-side equivalent to
// compare against (Forge boats don't have a custom loot table), so those aren't included here.
class EntityLootTableParityTest {

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"giant_tortoise", "harpy", "piranha", "puckoo", "treefrog", "worm"})
    void lootTableMatchesBedrock(String entityName) {
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/loot_tables/entities/" + entityName + ".json");
        JsonObject java = JavaDatapackJson.load(
                "src/generated/resources/data/extrabiomes/loot_table/entities/" + entityName + ".json");
        LootTableParity.assertMatches(bedrock.root(), java);
    }
}
