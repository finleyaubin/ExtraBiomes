package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;

public class FungleJungle {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityTypes.MOOSHROOM, 4, 8));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addJungleTrees(biomeBuilder);
        BiomeDefaultFeatures.addJungleGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder, true);
        // Bedrock's 'spawns_without_patrols' tag is handled via BiomeTags.WITHOUT_PATROL_SPAWNS, see ModBiomeTagProvider.

        // mushroom_surface_mycelium_floor and mushroom_island_surface_huge_mushroom are delivered by the ADD_MUSHROOM_FIELDS_*
        // modifiers via ModTags.Biomes.GETS_MUSHROOM_ISLAND_FEATURES, not baked in here: see that tag's javadoc for why a
        // baked-in copy makes a feature order cycle with mods that append vegetal features.
        // boulder: weighted boulder selection (with pebble scatter), local modification step
        // boulder: weighted stick-pile selection, vegetal decoration step

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.FUNGLE_JUNGLE.downfall())
                .temperature(BiomeClimateTuning.FUNGLE_JUNGLE.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x113290)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, BiomeAppearanceTuning.FUNGLE_JUNGLE.skyColor())
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xC0D8FF)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.CAN_PILLAGER_PATROL_SPAWN, false)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.FUNGLE_JUNGLE.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.FUNGLE_JUNGLE.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.FUNGLE_JUNGLE.grassColor()).build())
                .build();
    }
}
