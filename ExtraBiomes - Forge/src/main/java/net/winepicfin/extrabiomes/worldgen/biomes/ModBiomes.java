package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;

/**
 * Java (Forge) registrations for every biome that exists in the Bedrock add-on
 * (ExtraBiomes - Bedrock/packs/BP/biomes and packs/RP/biomes).
 *
 * Each ResourceKey below maps 1:1 to a "extrabiomes:<name>" identifier used by the
 * Bedrock biome json files, so the two versions stay in sync.
 */
public class ModBiomes
{
    public static final ResourceKey<Biome> CHARRED_FOREST = register("charred_forest");
    public static final ResourceKey<Biome> COLD_MESA = register("cold_mesa");
    public static final ResourceKey<Biome> COLD_MESA_BRYCE = register("cold_mesa_bryce");
    public static final ResourceKey<Biome> COLD_MESA_PLATEAU = register("cold_mesa_plateau");
    public static final ResourceKey<Biome> DEEP_DARK_FOREST = register("deep_dark_forest");
    public static final ResourceKey<Biome> DEEP_DARK_GREEN = register("deep_dark_green");
    public static final ResourceKey<Biome> DESERT_BRYCE = register("desert_bryce");
    public static final ResourceKey<Biome> FLOATING_JUNGLE = register("floating_jungle");
    public static final ResourceKey<Biome> FUNGLE_JUNGLE = register("fungle_jungle");
    public static final ResourceKey<Biome> FUTURE_DESERT = register("future_desert");
    public static final ResourceKey<Biome> GLACIER = register("glacier");
    public static final ResourceKey<Biome> GRAND_OASIS = register("grand_oasis");
    public static final ResourceKey<Biome> JELLYFISH_FIELDS = register("jellyfish_fields");
    public static final ResourceKey<Biome> JUNGLE_MARSH = register("jungle_marsh");
    public static final ResourceKey<Biome> JUNGLE_PILLARS = register("jungle_pillars");
    public static final ResourceKey<Biome> LUSH_MESA = register("lush_mesa");
    public static final ResourceKey<Biome> LUSH_MESA_BRYCE = register("lush_mesa_bryce");
    public static final ResourceKey<Biome> MOORLANDS = register("moorlands");
    public static final ResourceKey<Biome> MYSTIC_FOREST = register("mystic_forest");
    public static final ResourceKey<Biome> SHATTERED_SWAMP = register("shattered_swamp");
    public static final ResourceKey<Biome> SHATTERED_TAIGA_SPIKES = register("shattered_taiga_spikes");
    public static final ResourceKey<Biome> THE_NETHERLANDS = register("the_netherlands");
    public static final ResourceKey<Biome> THE_NETHERLANDS_MUTATED = register("the_netherlands_mutated");
    public static final ResourceKey<Biome> TAIGA_SPIKES = register("taiga_spikes");
    public static final ResourceKey<Biome> TROPICAL_ISLAND = register("tropical_island");


    private static ResourceKey<Biome> register(String name)
    {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    public static void boostrap(BootstapContext<Biome> context) {
        context.register(CHARRED_FOREST, new CharredForest().Register(context));
        context.register(COLD_MESA, new ColdMesa().Register(context));
        context.register(COLD_MESA_BRYCE, new ColdMesaBryce().Register(context));
        context.register(COLD_MESA_PLATEAU, new ColdMesaPlateau().Register(context));
        context.register(DEEP_DARK_FOREST, new DeepDarkForest().Register(context));
        context.register(DEEP_DARK_GREEN, new DeepDarkGreen().Register(context));
        context.register(DESERT_BRYCE, new DesertBryce().Register(context));
        context.register(FLOATING_JUNGLE, new FloatingJungle().Register(context));
        context.register(FUNGLE_JUNGLE, new FungleJungle().Register(context));
        context.register(FUTURE_DESERT, new FutureDesert().Register(context));
        context.register(GLACIER, new Glacier().Register(context));
        context.register(GRAND_OASIS, new GrandOasis().Register(context));
        context.register(JELLYFISH_FIELDS, new JellyfishFields().Register(context));
        context.register(JUNGLE_MARSH, new JungleMarsh().Register(context));
        context.register(JUNGLE_PILLARS, new JunglePillars().Register(context));
        context.register(LUSH_MESA, new LushMesa().Register(context));
        context.register(LUSH_MESA_BRYCE, new LushMesaBryce().Register(context));
        context.register(MOORLANDS, new Moorlands().Register(context));
        context.register(MYSTIC_FOREST, new MysticForest().Register(context));
        context.register(SHATTERED_SWAMP, new ShatteredSwamp().Register(context));
        context.register(SHATTERED_TAIGA_SPIKES, new ShatteredTiagaSpikes().Register(context));
        context.register(THE_NETHERLANDS, new TheNetherlands().Register(context));
        context.register(THE_NETHERLANDS_MUTATED, new TheNetherlandsMutated().Register(context));
        context.register(TAIGA_SPIKES, new TiagaSpikes().Register(context));
        context.register(TROPICAL_ISLAND, new TropicalIsland().Register(context));
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
        // Bedrock's underground_mushroom/{glow_mushroom,huge_glow_mushroom}_feature.json rules apply to
        // any_of overworld/overworld_generation (i.e. every overworld biome), so they're wired here
        // rather than per-biome.
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, MushroomFeatures.SELECT_MUSHROOM_PLACED_KEY);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, MushroomFeatures.HUGE_GLOW_MUSHROOM_UNDERGROUND_PLACED_KEY);
    }

}
