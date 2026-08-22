package net.winepicfin.extrabiomes.worldgen.biomes;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against every ExtraBiomes - Bedrock/packs/RP/biomes/<key>.client_biome.json
// "minecraft:water_appearance" / "minecraft:foliage_appearance" / "minecraft:grass_appearance"
// components, read live rather than copy-pasted. Iterates BiomeAppearanceTuning.BY_BEDROCK_KEY so
// adding a biome there automatically gets covered here - no per-biome test to remember to write.
// Mirrors BiomeClimateParityTest.
class BiomeAppearanceParityTest {

    static Stream<Map.Entry<String, BiomeAppearanceTuning.Appearance>> biomes() {
        return BiomeAppearanceTuning.BY_BEDROCK_KEY.entrySet().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("biomes")
    void waterColorMatchesBedrock(Map.Entry<String, BiomeAppearanceTuning.Appearance> entry) {
        BedrockEntityJson bedrock = BedrockEntityJson.load("RP/biomes/" + entry.getKey() + ".client_biome.json");
        assertEquals(bedrock.getColor("minecraft:client_biome", "components", "minecraft:water_appearance", "surface_color"),
                entry.getValue().waterColor());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("biomes")
    void foliageColorMatchesBedrock(Map.Entry<String, BiomeAppearanceTuning.Appearance> entry) {
        BedrockEntityJson bedrock = BedrockEntityJson.load("RP/biomes/" + entry.getKey() + ".client_biome.json");
        assertEquals(bedrock.getColor("minecraft:client_biome", "components", "minecraft:foliage_appearance", "color"),
                entry.getValue().foliageColor());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("biomes")
    void grassColorMatchesBedrock(Map.Entry<String, BiomeAppearanceTuning.Appearance> entry) {
        BedrockEntityJson bedrock = BedrockEntityJson.load("RP/biomes/" + entry.getKey() + ".client_biome.json");
        assertEquals(bedrock.getColor("minecraft:client_biome", "components", "minecraft:grass_appearance", "color"),
                entry.getValue().grassColor());
    }
}
