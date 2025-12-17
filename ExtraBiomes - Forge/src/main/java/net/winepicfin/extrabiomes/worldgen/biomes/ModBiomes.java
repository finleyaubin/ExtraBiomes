package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModBiomes
{
    public static final ResourceKey<Biome> CHARRED_FOREST = register("charred_forest");
    public static final ResourceKey<Biome> COLD_MESA = register("cold_mesa");
    public static final ResourceKey<Biome> COLD_ERODED_MESA = register("cold_eroded_mesa");
    public static final ResourceKey<Biome>  MYSTIC_FOREST = register("mystic_forest");
    public static final ResourceKey<Biome>  LUSH_MESA = register("lush_mesa");
    public static final ResourceKey<Biome> TIAGA_SPIKES = register("tiaga_spikes");


    private static ResourceKey<Biome> register(String name)
    {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
    public static void boostrap(BootstapContext<Biome> context) {
        CharredForest charredForest = new CharredForest();
        MysticForest mysticForest = new MysticForest();
        ColdMesa coldMesa = new ColdMesa();
        LushMesa lushMesa = new LushMesa();
        TiagaSpikes tiagaSpikes = new TiagaSpikes();
        context.register(CHARRED_FOREST,charredForest.Register(context));
        context.register(MYSTIC_FOREST,mysticForest.Register(context));
        context.register(COLD_MESA,coldMesa.Register(context));
        context.register(COLD_ERODED_MESA,coldMesa.Register(context));
        context.register(LUSH_MESA,lushMesa.Register(context));
        context.register(TIAGA_SPIKES,tiagaSpikes.Register(context));

    }
    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }

}