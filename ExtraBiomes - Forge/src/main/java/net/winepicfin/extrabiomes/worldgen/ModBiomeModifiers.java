package net.winepicfin.extrabiomes.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.util.ModTags;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_TREE_CHARRED = registerKey("add_tree_charred");
    public static final ResourceKey<BiomeModifier> ADD_LUSH_GRASS = registerKey("add_lush_grass");
    public static final ResourceKey<BiomeModifier> ADD_GLOW_VINES = registerKey("add_glow_vines");
    public static final ResourceKey<BiomeModifier> ADD_UNDERGROUND_JUNGLE_VEGETATION = registerKey("add_underground_jungle_vegetation");
    public static final ResourceKey<BiomeModifier> ADD_UNDERGROUND_JUNGLE_CAVE_VINES = registerKey("add_underground_jungle_cave_vines");
    public static final ResourceKey<BiomeModifier> ADD_MUSHROOM_FIELDS_HUGE_MUSHROOMS = registerKey("add_mushroom_fields_huge_mushrooms");
    public static final ResourceKey<BiomeModifier> ADD_DARK_FOREST_HUGE_MUSHROOMS = registerKey("add_dark_forest_huge_mushrooms");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

    context.register(ADD_LUSH_GRASS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(ModTags.Biomes.LUSH_MESA),
            HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LUSH_GRASS_PLACED_KEY)),
            GenerationStep.Decoration.TOP_LAYER_MODIFICATION));

    // Same underground_jungle recipe used for FloatingJungle/FungleJungle/etc (see
    // UndergroundJungleFeatures' class javadoc "Biome wiring" section), applied to every vanilla
    // jungle biome (jungle, sparse_jungle, bamboo_jungle) instead of one of this mod's own biomes.
    context.register(ADD_UNDERGROUND_JUNGLE_VEGETATION, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_JUNGLE),
            HolderSet.direct(
                    placedFeatures.getOrThrow(UndergroundJungleFeatures.GRASS_FLOOR_PLACED_KEY),
                    placedFeatures.getOrThrow(UndergroundJungleFeatures.GRASS_FLOOR_UPPER_PLACED_KEY)),
            GenerationStep.Decoration.VEGETAL_DECORATION));
    context.register(ADD_UNDERGROUND_JUNGLE_CAVE_VINES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_JUNGLE),
            HolderSet.direct(placedFeatures.getOrThrow(UndergroundJungleFeatures.CAVE_VINE_PLACED_KEY)),
            GenerationStep.Decoration.UNDERGROUND_DECORATION));

    // Custom huge mushroom variants added to vanilla's own mushroom-themed biomes, mirroring how
    // FungleJungle/DeepDarkForest already use these same placed features for this mod's biomes.
    context.register(ADD_MUSHROOM_FIELDS_HUGE_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            HolderSet.direct(biomes.getOrThrow(Biomes.MUSHROOM_FIELDS)),
            HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY)),
            GenerationStep.Decoration.VEGETAL_DECORATION));
    context.register(ADD_DARK_FOREST_HUGE_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST)),
            HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY)),
            GenerationStep.Decoration.VEGETAL_DECORATION));
}

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
