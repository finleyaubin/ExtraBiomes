package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModBiomes
{
    public static final ResourceKey<Biome> CHARRED_FOREST = register("charred_forest");
    public static final ResourceKey<Biome> COLD_MESA = register("cold_mesa");
    public static final ResourceKey<Biome> COLD_ERODED_MESA = register("cold_eroded_mesa");


    private static ResourceKey<Biome> register(String name)
    {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
    public static void boostrap(BootstapContext<Biome> context) {
        Charred_forest charredForest = new Charred_forest();
        context.register(CHARRED_FOREST,charredForest.Register(context));
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