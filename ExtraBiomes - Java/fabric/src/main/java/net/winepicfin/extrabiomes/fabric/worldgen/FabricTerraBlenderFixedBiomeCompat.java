package net.winepicfin.extrabiomes.fabric.worldgen;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import terrablender.DimensionTypeTags;
import terrablender.api.RegionType;
import terrablender.worldgen.IExtendedNoiseGeneratorSettings;

import java.util.Map;

// Fabric equivalent of forge/.../worldgen/TerraBlenderFixedBiomeCompat.java - see that class's
// javadoc for why this is needed (Single Biome worlds use a FixedBiomeSource, which TerraBlender
// doesn't patch surface rules for on its own). TerraBlender's IExtendedNoiseGeneratorSettings
// mixin interface is loader-agnostic (shipped by both terrablender-forge and terrablender-fabric),
// only the "run once when the server is about to start" hook differs -
// ServerLifecycleEvents.SERVER_STARTING is Fabric API's equivalent of Forge's
// ServerAboutToStartEvent. Note: unlike Forge's LOWEST-priority subscription, Fabric API's event
// has no priority levels, so ordering relative to other mods' listeners isn't guaranteed here.
public class FabricTerraBlenderFixedBiomeCompat {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            RegistryAccess registryAccess = server.registryAccess();
            Registry<LevelStem> levelStemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

            for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : levelStemRegistry.entrySet()) {
                LevelStem stem = entry.getValue();
                ChunkGenerator generator = stem.generator();
                if (!(generator instanceof NoiseBasedChunkGenerator noiseGenerator)) continue;
                if (generator.getBiomeSource() instanceof MultiNoiseBiomeSource) continue;

                Holder<DimensionType> dimensionType = stem.type();
                RegionType regionType;
                if (dimensionType.is(DimensionTypeTags.NETHER_REGIONS)) {
                    regionType = RegionType.NETHER;
                } else if (dimensionType.is(DimensionTypeTags.OVERWORLD_REGIONS)) {
                    regionType = RegionType.OVERWORLD;
                } else {
                    continue;
                }

                NoiseGeneratorSettings settings = noiseGenerator.generatorSettings().value();
                ((IExtendedNoiseGeneratorSettings) (Object) settings).setRegionType(regionType);
            }
        });
    }
}
