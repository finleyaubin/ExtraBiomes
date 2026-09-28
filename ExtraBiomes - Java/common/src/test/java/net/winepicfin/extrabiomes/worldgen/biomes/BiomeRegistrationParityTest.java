package net.winepicfin.extrabiomes.worldgen.biomes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.winepicfin.extrabiomes.testutil.JavaDatapackJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

// Fast proxy for "does this biome actually generate" without needing a real GameTest world: every
// biome ModBiomes.boostrap() registers gets written out by `./gradlew runData` as
// src/generated/resources/data/extrabiomes/worldgen/biome/<key>.json - datagen runs the full
// registry bootstrap (Register(), feature/placed-feature resolution, TerraBlender region lookups)
// to produce it, so a missing file or a climate mismatch here means that pipeline broke or a
// biome added to BiomeClimateTuning was never wired into ModBiomes.boostrap() at all.
//
// This does NOT prove terrain actually places blocks in a real world (TerraBlender region
// selection, structure placement, etc. aren't exercised) - that needs an actual GameTest that
// generates chunks and samples biomes at runtime, which is a separate, heavier follow-up.
class BiomeRegistrationParityTest {

    static Stream<Map.Entry<String, BiomeClimateTuning.Climate>> biomes() {
        return BiomeClimateTuning.BY_BEDROCK_KEY.entrySet().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("biomes")
    void biomeWasGeneratedWithMatchingClimate(Map.Entry<String, BiomeClimateTuning.Climate> entry) {
        JsonObject generated = JavaDatapackJson.load(
                "src/generated/resources/data/extrabiomes/worldgen/biome/" + entry.getKey() + ".json");
        assertEquals(entry.getValue().temperature(), generated.get("temperature").getAsFloat());
        assertEquals(entry.getValue().downfall(), generated.get("downfall").getAsFloat());
    }

    // Regression for the cross-mod "Feature order cycle found" crash: vanilla always lists a
    // biome's tree feature before minecraft:flower_default within the same decoration step, so an
    // ExtraBiomes biome that reverses that order creates an edge that contradicts every other mod
    // sharing flower_default, which can cycle with edges from unrelated biomes. See LushMesa/
    // LushMesaBryce, which used to call addDefaultFlowers() before addJungleTrees().
    @ParameterizedTest(name = "{0}")
    @MethodSource("biomes")
    void treesComeBeforeDefaultFlowerInEveryStep(Map.Entry<String, BiomeClimateTuning.Climate> entry) {
        JsonObject generated = JavaDatapackJson.load(
                "src/generated/resources/data/extrabiomes/worldgen/biome/" + entry.getKey() + ".json");
        JsonArray steps = generated.getAsJsonArray("features");
        for (JsonElement stepElement : steps) {
            JsonArray step = stepElement.getAsJsonArray();
            int lastTreeIndex = -1;
            int firstFlowerIndex = -1;
            for (int i = 0; i < step.size(); i++) {
                String key = step.get(i).getAsString();
                if (key.contains("trees_") || key.contains("/trees")) {
                    lastTreeIndex = i;
                }
                if (key.equals("minecraft:flower_default") && firstFlowerIndex == -1) {
                    firstFlowerIndex = i;
                }
            }
            assertFalse(firstFlowerIndex != -1 && firstFlowerIndex < lastTreeIndex,
                    entry.getKey() + " lists minecraft:flower_default before a tree feature: " + step);
        }
    }
}
