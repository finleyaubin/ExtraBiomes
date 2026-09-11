package net.winepicfin.extrabiomes.worldgen.biomes.surface;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * NormalNoise.NoiseParameters backing {@link ModSurfaceRules}'s ports of Bedrock's
 * "minecraft:surface_material_adjustments" biome component - patchy top/mid material overrides
 * layered on top of a biome's base surface_builder materials, each gated by a Perlin noise band.
 * <p>
 * Bedrock samples that noise at world coordinates scaled by a per-adjustment
 * "noise_frequency_scale" (higher = smaller patches); Java's NormalNoise has no equivalent runtime
 * scale knob, so patch size is instead controlled by picking a noise with an appropriately-sized
 * first octave, the same way vanilla shares a handful of differently-scaled noises (Noises.PATCH,
 * Noises.SURFACE, Noises.SWAMP, ...) across many unrelated surface rules. The four below are
 * ordered from smallest to largest patches and reused across every adjustment in ModSurfaceRules
 * by matching each Bedrock noise_frequency_scale to the closest of these, rather than minting a
 * bespoke noise per biome.
 */
public class ModNoiseParameters {
    public static final ResourceKey<NormalNoise.NoiseParameters> SMALL_PATCH = key("small_patch");
    public static final ResourceKey<NormalNoise.NoiseParameters> MEDIUM_PATCH = key("medium_patch");
    public static final ResourceKey<NormalNoise.NoiseParameters> LARGE_PATCH = key("large_patch");
    public static final ResourceKey<NormalNoise.NoiseParameters> REGIONAL_BAND = key("regional_band");

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {
        context.register(SMALL_PATCH, new NormalNoise.NoiseParameters(-5, 1.0));
        context.register(MEDIUM_PATCH, new NormalNoise.NoiseParameters(-4, 1.0));
        context.register(LARGE_PATCH, new NormalNoise.NoiseParameters(-3, 1.0));
        context.register(REGIONAL_BAND, new NormalNoise.NoiseParameters(-2, 1.0));
    }

    private static ResourceKey<NormalNoise.NoiseParameters> key(String name) {
        return ResourceKey.create(Registries.NOISE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
}
