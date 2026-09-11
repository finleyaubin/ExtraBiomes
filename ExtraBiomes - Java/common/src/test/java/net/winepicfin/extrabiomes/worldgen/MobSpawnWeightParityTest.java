package net.winepicfin.extrabiomes.worldgen;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks between Bedrock's spawn_rules "minecraft:weight".default
// (ExtraBiomes - Bedrock/packs/BP/spawn_rules/<name>.json) and the weight argument passed to each
// ModBiomeModifiers.AddSpawnsBiomeModifier registration.
//
// A Bedrock spawn_rules file can have multiple conditions (e.g. one per surface/underwater/biome
// tag); conditionIndex picks the one condition that corresponds to the Java registration being
// checked here. Not every Bedrock condition has a Java-side equivalent - e.g. giant_tortoise's
// underwater-only condition (weight 20) was intentionally left unported - so this only asserts
// what was actually intended to carry over, not every condition in the file.
class MobSpawnWeightParityTest {

    static Stream<Arguments> weights() {
        return Stream.of(
                Arguments.of("giant_tortoise", 0, MobSpawnWeightTuning.GIANT_TORTOISE),
                Arguments.of("piranha", 0, MobSpawnWeightTuning.PIRANHA_JUNGLE),
                Arguments.of("piranha", 2, MobSpawnWeightTuning.PIRANHA_SWAMP),
                Arguments.of("treefrog", 0, MobSpawnWeightTuning.TREEFROG_JUNGLE),
                Arguments.of("treefrog", 0, MobSpawnWeightTuning.TREEFROG_SWAMP),
                Arguments.of("hoppleshroom", 0, MobSpawnWeightTuning.HOPPLESHROOM),
                Arguments.of("jellyfish", 0, MobSpawnWeightTuning.JELLYFISH),
                Arguments.of("jellyfish", 1, MobSpawnWeightTuning.JELLYFISH_BEACH),
                Arguments.of("harpy", 0, MobSpawnWeightTuning.HARPY),
                Arguments.of("worm", 0, MobSpawnWeightTuning.WORM)
        );
    }

    @ParameterizedTest(name = "{0}[{1}] -> {2}")
    @MethodSource("weights")
    void weightMatchesBedrock(String bedrockKey, int conditionIndex, int javaWeight) {
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/spawn_rules/" + bedrockKey + ".json");
        double bedrockWeight = bedrock.getDouble("minecraft:spawn_rules", "conditions",
                String.valueOf(conditionIndex), "minecraft:weight", "default");
        assertEquals(bedrockWeight, javaWeight);
    }
}
