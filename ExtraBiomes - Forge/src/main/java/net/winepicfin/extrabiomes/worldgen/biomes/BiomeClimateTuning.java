package net.winepicfin.extrabiomes.worldgen.biomes;

import java.util.LinkedHashMap;
import java.util.Map;

// Bedrock climate values (ExtraBiomes - Bedrock/packs/BP/biomes/<key>.biome.json,
// "minecraft:climate" component) for every ported biome. Deliberately has no Minecraft imports,
// and BY_BEDROCK_KEY lets BiomeClimateParityTest iterate every biome without needing a
// bootstrapped registry to construct a real Biome (each Register() method here needs one).
public final class BiomeClimateTuning {
    public record Climate(float temperature, float downfall) {
    }

    public static final Map<String, Climate> BY_BEDROCK_KEY = new LinkedHashMap<>();

    private static Climate register(String bedrockKey, float temperature, float downfall) {
        Climate climate = new Climate(temperature, downfall);
        BY_BEDROCK_KEY.put(bedrockKey, climate);
        return climate;
    }

    public static final Climate CHARRED_FOREST = register("charred_forest", 2.0f, 0.5f);
    public static final Climate COLD_MESA = register("cold_mesa", 0.0f, 1.0f);
    public static final Climate COLD_MESA_BRYCE = register("cold_mesa_bryce", 0.0f, 1.0f);
    public static final Climate COLD_MESA_PLATEAU = register("cold_mesa_plateau", 0.0f, 1.0f);
    public static final Climate DEEP_DARK_FOREST = register("deep_dark_forest", 0.3f, 0.8f);
    public static final Climate DEEP_DARK_GREEN = register("deep_dark_green", 0.8f, 0.4f);
    public static final Climate DESERT_BRYCE = register("desert_bryce", 2.0f, 0.0f);
    public static final Climate FLOATING_JUNGLE = register("floating_jungle", 0.95f, 0.9f);
    public static final Climate FUNGLE_JUNGLE = register("fungle_jungle", 0.95f, 0.9f);
    public static final Climate FUTURE_DESERT = register("future_desert", 2.0f, 0.0f);
    public static final Climate GLACIER = register("glacier", 0.0f, 1.0f);
    public static final Climate GRAND_OASIS = register("grand_oasis", 2.0f, 0.0f);
    public static final Climate JELLYFISH_FIELDS = register("jellyfish_fields", 0.5f, 0.5f);
    public static final Climate JUNGLE_MARSH = register("jungle_marsh", 0.95f, 0.9f);
    public static final Climate JUNGLE_PILLARS = register("jungle_pillars", 0.95f, 0.9f);
    public static final Climate LUSH_MESA = register("lush_mesa", 2.0f, 0.7f);
    public static final Climate LUSH_MESA_BRYCE = register("lush_mesa_bryce", 2.0f, 0.7f);
    public static final Climate MOORLANDS = register("moorlands", 0.5f, 0.5f);
    public static final Climate MYSTIC_FOREST = register("mystic_forest", 0.95f, 0.9f);
    public static final Climate SHATTERED_SWAMP = register("shattered_swamp", 0.8f, 0.5f);
    public static final Climate SHATTERED_TIAGA_SPIKES = register("shattered_tiaga_spikes", 0.0f, 1.0f);
    public static final Climate THE_NETHERLANDS = register("the_netherlands", 0.5f, 0.5f);
    public static final Climate THE_NETHERLANDS_MUTATED = register("the_netherlands_mutated", 0.5f, 0.5f);
    public static final Climate TIAGA_SPIKES = register("tiaga_spikes", 0.0f, 1.0f);
    public static final Climate TROPICAL_ISLAND = register("tropical_island", 1.0f, 0.9f);

    private BiomeClimateTuning() {
    }
}
