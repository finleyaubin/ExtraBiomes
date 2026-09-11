package net.winepicfin.extrabiomes.worldgen.biomes;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against every ExtraBiomes - Bedrock/packs/BP/biomes/<key>.biome.json
// "minecraft:climate" component, read live rather than copy-pasted. Iterates
// BiomeClimateTuning.BY_BEDROCK_KEY so adding a biome there automatically gets covered here - no
// per-biome test to remember to write.
class BiomeClimateParityTest {

    static Stream<Map.Entry<String, BiomeClimateTuning.Climate>> biomes() {
        return BiomeClimateTuning.BY_BEDROCK_KEY.entrySet().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("biomes")
    void temperatureMatchesBedrock(Map.Entry<String, BiomeClimateTuning.Climate> entry) {
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/biomes/" + entry.getKey() + ".biome.json");
        assertEquals(bedrock.getFloat("minecraft:biome", "components", "minecraft:climate", "temperature"),
                entry.getValue().temperature());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("biomes")
    void downfallMatchesBedrock(Map.Entry<String, BiomeClimateTuning.Climate> entry) {
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/biomes/" + entry.getKey() + ".biome.json");
        assertEquals(bedrock.getFloat("minecraft:biome", "components", "minecraft:climate", "downfall"),
                entry.getValue().downfall());
    }
}
