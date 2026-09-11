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
// src/generated/resources/data/extrabiomes/loot_table/...). The two schemas wrap the same drop
// data differently - Java prefixes function/condition/type ids with "minecraft:" that Bedrock
// omits, adds wrapper keys Bedrock doesn't have (type, bonus_rolls, random_sequence, add, and a
// default weight of 1 that Bedrock always writes explicitly but Java's datagen omits), and
// sometimes collapses a uniform(min, max) count into a bare number when min == max. This walks
// only the keys Bedrock actually has, normalizing those differences, rather than doing a strict
// JSON diff (which would just fail on the wrapper noise).
public final class LootTableParity {
    private static final String MINECRAFT_PREFIX = "minecraft:";

    // Ids Java renamed (items in 1.20.5's item-component rewrite, loot function/condition ids in
    // 1.21's enchantment-registry rework) that Bedrock still calls by the old name - a real,
    // permanent naming divergence between editions, not a bug on either side.
    private static final Map<String, String> JAVA_ITEM_RENAMES = Map.of(
            "scute", "turtle_scute",
            "looting_enchant", "enchanted_count_increase",
            "random_chance_with_looting", "random_chance_with_enchanted_bonus");

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
            JsonObject b = normalizeLootingCondition(bedrock.getAsJsonObject());
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

    // Bedrock's random_chance_with_looting condition ({"chance": c, "looting_multiplier": m}) maps
    // to Java 1.21's random_chance_with_enchanted_bonus, which restructures the same two numbers
    // into unenchanted_chance=c and a linear enchanted_chance curve where base=c+m (the level-1
    // value) and per_level_above_first=m - same formula, different shape. Reshape Bedrock's object
    // to Java's so the generic key-walk below can compare them directly.
    private static JsonObject normalizeLootingCondition(JsonObject bedrock) {
        if (!bedrock.has("chance") || !bedrock.has("looting_multiplier")) {
            return bedrock;
        }
        double chance = bedrock.get("chance").getAsDouble();
        double multiplier = bedrock.get("looting_multiplier").getAsDouble();
        JsonObject reshaped = bedrock.deepCopy();
        reshaped.remove("chance");
        reshaped.remove("looting_multiplier");
        reshaped.addProperty("unenchanted_chance", chance);
        JsonObject enchantedChance = new JsonObject();
        enchantedChance.addProperty("type", "linear");
        enchantedChance.addProperty("base", chance + multiplier);
        enchantedChance.addProperty("per_level_above_first", multiplier);
        reshaped.add("enchanted_chance", enchantedChance);
        return reshaped;
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
