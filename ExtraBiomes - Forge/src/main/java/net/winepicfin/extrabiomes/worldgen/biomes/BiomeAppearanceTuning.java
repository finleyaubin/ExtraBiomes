package net.winepicfin.extrabiomes.worldgen.biomes;

import java.util.LinkedHashMap;
import java.util.Map;

// Bedrock client appearance values (ExtraBiomes - Bedrock/packs/RP/biomes/<key>.client_biome.json,
// "minecraft:water_appearance"/"minecraft:foliage_appearance"/"minecraft:grass_appearance"
// components) for every ported biome. Deliberately has no Minecraft imports, and BY_BEDROCK_KEY
// lets BiomeAppearanceParityTest iterate every biome without needing a bootstrapped registry to
// construct a real Biome (each Register() method here needs one). Mirrors BiomeClimateTuning.
public final class BiomeAppearanceTuning {
    public record Appearance(int waterColor, int foliageColor, int grassColor) {
    }

    public static final Map<String, Appearance> BY_BEDROCK_KEY = new LinkedHashMap<>();

    private static Appearance register(String bedrockKey, int waterColor, int foliageColor, int grassColor) {
        Appearance appearance = new Appearance(waterColor, foliageColor, grassColor);
        BY_BEDROCK_KEY.put(bedrockKey, appearance);
        return appearance;
    }

    public static final Appearance CHARRED_FOREST = register("charred_forest", 0x4c5e38, 0x3a2e14, 0x5a3e1e);
    public static final Appearance COLD_MESA = register("cold_mesa", 0x3d5cdb, 0x60a090, 0x80b0a0);
    public static final Appearance COLD_MESA_BRYCE = register("cold_mesa_bryce", 0x3d5cdb, 0x5a9a8a, 0x7aaaa0);
    public static final Appearance COLD_MESA_PLATEAU = register("cold_mesa_plateau", 0x3d5cdb, 0x5898a0, 0x78a8a0);
    public static final Appearance DEEP_DARK_FOREST = register("deep_dark_forest", 0x3f76e4, 0x1BB210, 0x59c93c);
    public static final Appearance DEEP_DARK_GREEN = register("deep_dark_green", 0x3f76e4, 0x1BB210, 0x59c93c);
    public static final Appearance DESERT_BRYCE = register("desert_bryce", 0x30a8a0, 0xaa8030, 0xc8a040);
    public static final Appearance FLOATING_JUNGLE = register("floating_jungle", 0x1B9ED8, 0x30bb0b, 0x59c93c);
    public static final Appearance FUNGLE_JUNGLE = register("fungle_jungle", 0x3f76e4, 0x38a010, 0x50aa30);
    public static final Appearance FUTURE_DESERT = register("future_desert", 0x0858e0, 0x8898a8, 0xb8c0c8);
    public static final Appearance GLACIER = register("glacier", 0x2838c8, 0x60a0b0, 0x80b0c0);
    public static final Appearance GRAND_OASIS = register("grand_oasis", 0x20c8a0, 0x58b028, 0x80c040);
    public static final Appearance JELLYFISH_FIELDS = register("jellyfish_fields", 0x02B0E5, 0x38b078, 0x58c898);
    public static final Appearance JUNGLE_MARSH = register("jungle_marsh", 0x2a6830, 0x386020, 0x487030);
    public static final Appearance JUNGLE_PILLARS = register("jungle_pillars", 0x3f76e4, 0x30bb0b, 0x59c93c);
    public static final Appearance LUSH_MESA = register("lush_mesa", 0x1B9ED8, 0x58b028, 0x78c040);
    public static final Appearance LUSH_MESA_BRYCE = register("lush_mesa_bryce", 0x1B9ED8, 0x50a830, 0x70bc40);
    public static final Appearance MOORLANDS = register("moorlands", 0x2846ea, 0x28962a, 0xbcbc2b);
    public static final Appearance MYSTIC_FOREST = register("mystic_forest", 0xFF63FF, 0x9040c8, 0x8050e0);
    public static final Appearance SHATTERED_SWAMP = register("shattered_swamp", 0x3a642c, 0x4b5e14, 0x4e5f42);
    public static final Appearance SHATTERED_TAIGA_SPIKES = register("shattered_taiga_spikes", 0x3a6edb, 0x5a9a78, 0x80b497);
    public static final Appearance THE_NETHERLANDS = register("the_netherlands", 0x90ADAD, 0xD4912C, 0x7AAB35);
    public static final Appearance THE_NETHERLANDS_MUTATED = register("the_netherlands_mutated", 0x9EB0B0, 0xE3B56D, 0x51AD07);
    public static final Appearance TAIGA_SPIKES = register("taiga_spikes", 0x3a6edb, 0x5a9a78, 0x80b497);
    public static final Appearance TROPICAL_ISLAND = register("tropical_island", 0x28c8e0, 0x40b020, 0x60c840);

    private BiomeAppearanceTuning() {
    }
}
