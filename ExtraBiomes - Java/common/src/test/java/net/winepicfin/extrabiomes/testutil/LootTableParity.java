package net.winepicfin.extrabiomes.testutil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Compares a Bedrock loot table (packs/BP/loot_tables/...) against the equivalent Java datapack
// loot table (generated via `runData` under
// src/generated/resources/data/extrabiomes/loot_tables/...). The two schemas wrap the same drop
// data differently - Java prefixes function/condition/type ids with "minecraft:" that Bedrock
// omits, adds wrapper keys Bedrock doesn't have (type, bonus_rolls, random_sequence, add, and a
// default weight of 1 that Bedrock always writes explicitly but Java's datagen omits), and
// sometimes collapses a uniform(min, max) count into a bare number when min == max. This walks
// only the keys Bedrock actually has, normalizing those differences, rather than doing a strict
// JSON diff (which would just fail on the wrapper noise).
public final class LootTableParity {
    private static final String MINECRAFT_PREFIX = "minecraft:";

    // Items Java renamed (1.20.5's item-component rewrite) that Bedrock still calls by the old
    // name - a real, permanent naming divergence between editions, not a bug on either side.
    private static final Map<String, String> JAVA_ITEM_RENAMES = Map.of("scute", "turtle_scute");

    public static void assertMatches(JsonObject bedrockRoot, JsonObject javaRoot) {
        compare(bedrockRoot.get("pools"), javaRoot.get("pools"), "pools");
    }

    private static void compare(JsonElement bedrock, JsonElement java, String path) {
        if (bedrock == null || bedrock.isJsonNull()) {
            return;
        }
        if (java == null || java.isJsonNull()) {
            fail("Java loot table is missing " + path);
        }
        if (bedrock.isJsonArray()) {
            JsonArray b = bedrock.getAsJsonArray();
            JsonArray j = java.getAsJsonArray();
            assertEquals(b.size(), j.size(), "entry count mismatch at " + path);
            for (int i = 0; i < b.size(); i++) {
                compare(b.get(i), j.get(i), path + "[" + i + "]");
            }
        } else if (bedrock.isJsonObject()) {
            JsonObject b = bedrock.getAsJsonObject();
            JsonObject j = java.getAsJsonObject();
            for (String key : b.keySet()) {
                JsonElement bVal = b.get(key);
                JsonElement jVal = j.has(key) ? j.get(key) : null;
                if (jVal == null && "weight".equals(key) && bVal.getAsDouble() == 1.0) {
                    continue; // Java's datagen omits the default weight of 1
                }
                compare(normalizeCount(key, bVal), normalizeCount(key, jVal), path + "." + key);
            }
        } else {
            JsonPrimitive b = bedrock.getAsJsonPrimitive();
            JsonPrimitive j = java.getAsJsonPrimitive();
            if (b.isString()) {
                String bId = stripMinecraftPrefix(b.getAsString());
                assertEquals(JAVA_ITEM_RENAMES.getOrDefault(bId, bId), stripMinecraftPrefix(j.getAsString()), path);
            } else {
                assertEquals(b.getAsDouble(), j.getAsDouble(), 1e-6, path);
            }
        }
    }

    // Both sides may express a fixed count as either {"min": n, "max": n} or a bare number n.
    private static JsonElement normalizeCount(String key, JsonElement value) {
        if (!"count".equals(key) || value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            return value;
        }
        JsonObject asRange = new JsonObject();
        asRange.addProperty("min", value.getAsDouble());
        asRange.addProperty("max", value.getAsDouble());
        return asRange;
    }

    private static String stripMinecraftPrefix(String value) {
        return value.startsWith(MINECRAFT_PREFIX) ? value.substring(MINECRAFT_PREFIX.length()) : value;
    }

    private LootTableParity() {
    }
}
