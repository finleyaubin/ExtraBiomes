package net.winepicfin.extrabiomes.worldgen.biomes;

import com.google.gson.JsonObject;
import net.winepicfin.extrabiomes.testutil.JavaDatapackJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
