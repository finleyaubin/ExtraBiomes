package net.winepicfin.extrabiomes.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.util.ModTags;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_TREE_CHARRED = registerKey("add_tree_charred");
    public static final ResourceKey<BiomeModifier> ADD_LUSH_GRASS = registerKey("add_lush_grass");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

    context.register(ADD_LUSH_GRASS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(ModTags.Biomes.tag("lush_mesa")),
            HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LUSH_GRASS_PLACED_KEY)),
            GenerationStep.Decoration.UNDERGROUND_ORES));
}

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
