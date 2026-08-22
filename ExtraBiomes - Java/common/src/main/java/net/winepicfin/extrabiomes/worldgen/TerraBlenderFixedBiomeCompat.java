package net.winepicfin.extrabiomes.worldgen;

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

/**
 * TerraBlender only injects a mod's surface rules (see
 * {@link net.winepicfin.extrabiomes.worldgen.biomes.surface.ModSurfaceRules}) into a level whose
 * biome source is its own {@link MultiNoiseBiomeSource} (see TerraBlender's
 * {@code LevelUtils.shouldApplyToBiomeSource}) - vanilla's "Single Biome" world preset uses a plain
 * {@code FixedBiomeSource} instead, so such levels silently fall back to vanilla's untouched
 * surface rule (no black sand, no other ExtraBiomes ground materials) with no warning or error.
 * <p>
 * This mirrors TerraBlender's own {@code LevelUtils.initializeBiomes}, but sets the noise
 * settings' region type directly instead of bailing out when the biome source isn't a
 * {@link MultiNoiseBiomeSource} - no biome-list/parameter-list initialization is needed here since
 * a fixed biome source doesn't use TerraBlender's region system to pick biomes in the first place,
 * it just needs {@link NoiseGeneratorSettings#surfaceRule()} (mixed into by TerraBlender) to know
 * to serve the namespaced rules instead of vanilla's.
 * <p>
 * Loader-agnostic core: both TerraBlender's {@code IExtendedNoiseGeneratorSettings} mixin
 * interface and {@code RegistryAccess}/{@code LevelStem} are shipped/available identically on
 * Forge and Fabric - only the "run once when the server is about to start" hook differs
 * (Forge's {@code ServerAboutToStartEvent} vs Fabric API's
 * {@code ServerLifecycleEvents.SERVER_STARTING}), so each loader module just calls
 * {@link #applyToFixedBiomeSourceLevels(RegistryAccess)} from its own hook.
 */
public class TerraBlenderFixedBiomeCompat {
    public static void applyToFixedBiomeSourceLevels(RegistryAccess registryAccess) {
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
    }
}
