package net.winepicfin.extrabiomes.worldgen.biomes;

import java.util.LinkedHashMap;
import java.util.Map;

// skyColor has no Bedrock component to check parity against, so it's centralized here untested; no Minecraft imports so BiomeAppearanceParityTest can iterate BY_BEDROCK_KEY without a bootstrapped registry.
public final class BiomeAppearanceTuning {
    public record Appearance(int waterColor, int foliageColor, int grassColor, int skyColor) {
    }

    public static final Map<String, Appearance> BY_BEDROCK_KEY = new LinkedHashMap<>();

    private static Appearance register(String bedrockKey, int waterColor, int foliageColor, int grassColor, int skyColor) {
        Appearance appearance = new Appearance(waterColor, foliageColor, grassColor, skyColor);
        BY_BEDROCK_KEY.put(bedrockKey, appearance);
        return appearance;
    }

    public static final Appearance CHARRED_FOREST = register("charred_forest", 0x3f76e4, 0x3a2e14, 0x5a3e1e, 0x6EB1FF);
    public static final Appearance COLD_MESA = register("cold_mesa", 0x3d5cdb, 0x60a090, 0x80b0a0, 0x7FA1FF);
    public static final Appearance COLD_MESA_BRYCE = register("cold_mesa_bryce", 0x3d5cdb, 0x5a9a8a, 0x7aaaa0, 0x7FA1FF);
    public static final Appearance COLD_MESA_PLATEAU = register("cold_mesa_plateau", 0x3d5cdb, 0x5898a0, 0x78a8a0, 0x7FA1FF);
    public static final Appearance DEEP_DARK_FOREST = register("deep_dark_forest", 0x3f76e4, 0x1BB210, 0x59c93c, 0x78A7FF);
    public static final Appearance DEEP_DARK_GREEN = register("deep_dark_green", 0x3f76e4, 0x1BB210, 0x59c93c, 0x78A7FF);
    public static final Appearance DESERT_BRYCE = register("desert_bryce", 0x30a8a0, 0xaa8030, 0xc8a040, 0x6EB1FF);
    public static final Appearance FUTURE_DESERT = register("future_desert", 0x0858e0, 0x8898a8, 0xb8c0c8, 0x6EB1FF);
    public static final Appearance GRAND_OASIS = register("grand_oasis", 0x20c8a0, 0x58b028, 0x80c040, 0x6EB1FF);
    public static final Appearance FLOATING_JUNGLE = register("floating_jungle", 0x1B9ED8, 0x30bb0b, 0x59c93c, 0x77A8FF);
    public static final Appearance FUNGLE_JUNGLE = register("fungle_jungle", 0x3f76e4, 0x38a010, 0x50aa30, 0x77A8FF);
    public static final Appearance JUNGLE_MARSH = register("jungle_marsh", 0x2a6830, 0x386020, 0x487030, 0x77A8FF);
    public static final Appearance JUNGLE_PILLARS = register("jungle_pillars", 0x3f76e4, 0x30bb0b, 0x59c93c, 0x77A8FF);
    public static final Appearance MYSTIC_FOREST = register("mystic_forest", 0xFF63FF, 0x9040c8, 0x8050e0, 0x77A8FF);
    public static final Appearance GLACIER = register("glacier", 0x2838c8, 0x60a0b0, 0x80b0c0, 0x7FA1FF);
    public static final Appearance JELLYFISH_FIELDS = register("jellyfish_fields", 0x02B0E5, 0x38b078, 0x58c898, 0x7BA4FF);
    public static final Appearance LUSH_MESA = register("lush_mesa", 0x1B9ED8, 0x58b028, 0x78c040, 0x6EB1FF);
    public static final Appearance LUSH_MESA_BRYCE = register("lush_mesa_bryce", 0x1B9ED8, 0x50a830, 0x70bc40, 0x6EB1FF);
    public static final Appearance MOORLANDS = register("moorlands", 0x2846ea, 0x28962a, 0xbcbc2b, 0x78A7FF);
    public static final Appearance SHATTERED_SWAMP = register("shattered_swamp", 0x3a642c, 0x4b5e14, 0x4e5f42, 0x78A7FF);
    public static final Appearance SHATTERED_TAIGA_SPIKES = register("shattered_taiga_spikes", 0x3a6edb, 0x5a9a78, 0x80b497, 0x7FA1FF);
    public static final Appearance TAIGA_SPIKES = register("taiga_spikes", 0x3a6edb, 0x5a9a78, 0x80b497, 0x7FA1FF);
    public static final Appearance THE_NETHERLANDS = register("the_netherlands", 0x90ADAD, 0xD4912C, 0x7AAB35, 0x78A7FF);
    public static final Appearance THE_NETHERLANDS_MUTATED = register("the_netherlands_mutated", 0x9EB0B0, 0xE3B56D, 0x51AD07, 0x78A7FF);
    public static final Appearance TROPICAL_ISLAND = register("tropical_island", 0x28c8e0, 0x40b020, 0x60c840, 0x7BA4FF);
    // Bedrock 3.1.0-beta-2 shipped no client_biome.json for this biome, so these values were authored alongside the port rather than ported.
    public static final Appearance VOLCANIC_MOSS_TUNDRA = register("volcanic_moss_tundra", 0x2e4a5c, 0x4a7a5a, 0x5a8a6a, 0x7FA1FF);

    private BiomeAppearanceTuning() {
    }
}
