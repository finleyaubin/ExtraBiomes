package net.winepicfin.extrabiomes.worldgen.biomes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.winepicfin.extrabiomes.testutil.JavaDatapackJson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tallow Hollows stays out of BiomeClimateTuning.BY_BEDROCK_KEY until its Bedrock branch merges, so
// BiomeRegistrationParityTest never sees it; this covers the same ground for it from datagen output.
class TallowHollowsBiomeTest {
    private static final String BIOME_KEY = "tallow_hollows";
    private static final List<String> SIGNATURE_FEATURES = List.of(
            "extrabiomes:tallow_hollows_pillar",
            "extrabiomes:tallow_hollows_forgotten_shelves",
            "extrabiomes:tallow_hollows_wick_bloom",
            "extrabiomes:tallow_hollows_candle_cluster",
            "extrabiomes:tallow_hollows_wax_drip",
            "extrabiomes:tallow_hollows_honeycomb_vein");

    private static JsonObject generatedBiome() {
        return JavaDatapackJson.load("src/generated/resources/data/extrabiomes/worldgen/biome/" + BIOME_KEY + ".json");
    }

    private static List<String> placedFeatures() {
        List<String> features = new ArrayList<>();
        for (JsonElement step : generatedBiome().getAsJsonArray("features")) {
            for (JsonElement feature : step.getAsJsonArray()) {
                features.add(feature.getAsString());
            }
        }
        return features;
    }

    @Test
    void climateMatchesTheTuning() {
        JsonObject biome = generatedBiome();
        assertEquals(BiomeClimateTuning.TALLOW_HOLLOWS.temperature(), biome.get("temperature").getAsFloat());
        assertEquals(BiomeClimateTuning.TALLOW_HOLLOWS.downfall(), biome.get("downfall").getAsFloat());
    }

    @Test
    void carriesEverySignatureFeature() {
        List<String> features = placedFeatures();
        for (String expected : SIGNATURE_FEATURES) {
            assertTrue(features.contains(expected), BIOME_KEY + " is missing " + expected + ": " + features);
        }
    }

    @Test
    void waxDripsReplaceVanillaDripstone() {
        for (String feature : placedFeatures()) {
            assertTrue(!(feature.startsWith("minecraft:") && feature.contains("dripstone")), BIOME_KEY + " still places " + feature);
        }
    }

    @Test
    void treesComeBeforeDefaultFlower() {
        BiomeRegistrationParityTest.assertTreesComeBeforeDefaultFlower(BIOME_KEY);
    }

    @Test
    void neverCombinesDefaultFlowerWithJungleGrass() {
        BiomeRegistrationParityTest.assertNeverCombines(BIOME_KEY, "minecraft:flower_default", "minecraft:patch_grass_jungle");
    }

    @Test
    void neverCombinesDefaultFlowerWithPlainsGrass() {
        BiomeRegistrationParityTest.assertNeverCombines(BIOME_KEY, "minecraft:flower_default", "minecraft:patch_grass_plain");
    }
}
