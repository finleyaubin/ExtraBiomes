package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Materials + shape tuning for {@link BrycePillarsFeature}. {@code hardClayMaterial} and
 * {@code clayMaterial} map directly onto the "hard_clay_material"/"clay_material" fields of
 * Bedrock's "minecraft:mesa" surface builder for every biome with "bryce_pillars": true (see
 * e.g. packs/BP/biomes/cold_mesa_bryce.biome.json) - hardClayMaterial fills the lower ~70% of
 * each pillar (the dense "core"), clayMaterial caps the upper, more weathered section, matching
 * how the old surface builder layered hardened clay under stained hardened clay. Both are
 * {@link BlockStateProvider}s rather than plain BlockStates so a biome whose Bedrock
 * "clay_material" is the multi-colour "stained_hardened_clay" family (no single Java block
 * equivalent) can use a weighted terracotta palette instead of one flat colour, while biomes
 * with a genuinely single material (sand, tuff, stone...) just use
 * {@link BlockStateProvider#simple}.
 */
public record BrycePillarsConfiguration(BlockStateProvider hardClayMaterial, BlockStateProvider clayMaterial, int minHeight, int maxHeight, float threshold, int maxRadius, float erosionStrength) implements FeatureConfiguration {
    // threshold raised 0.55 -> 0.75 -> 0.85: abs(simplex) clears 0.55 (and even 0.75) on a large
    // share of columns, which is what made these "way too frequent" - real Bryce Canyon-style
    // hoodoos are sparse, isolated spires, not a pillar on most columns. maxHeight raised 15 -> 48
    // (near the codec's range ceiling) so the tallest pillars can tower well above the old vanilla
    // mesa height cap. maxRadius gives each pillar a wide base that tapers to a point at its own
    // height (see BrycePillarsFeature's per-column cone) instead of a uniform 1-block-wide shaft.
    // erosionStrength perturbs that cone's radius with a separate noise field so the outline is a
    // weathered, fluted silhouette instead of a perfect circle at every layer.
    public BrycePillarsConfiguration(BlockStateProvider hardClayMaterial, BlockStateProvider clayMaterial) {
        this(hardClayMaterial, clayMaterial, 5, 48, 0.97F, 4, 1.5F);
    }

    public static final Codec<BrycePillarsConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("hard_clay_material").forGetter(BrycePillarsConfiguration::hardClayMaterial),
            BlockStateProvider.CODEC.fieldOf("clay_material").forGetter(BrycePillarsConfiguration::clayMaterial),
            Codec.intRange(0, 96).optionalFieldOf("min_height", 5).forGetter(BrycePillarsConfiguration::minHeight),
            Codec.intRange(0, 96).optionalFieldOf("max_height", 48).forGetter(BrycePillarsConfiguration::maxHeight),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("threshold", 0.97F).forGetter(BrycePillarsConfiguration::threshold),
            Codec.intRange(0, 16).optionalFieldOf("max_radius", 4).forGetter(BrycePillarsConfiguration::maxRadius),
            Codec.floatRange(0.0F, 8.0F).optionalFieldOf("erosion_strength", 1.5F).forGetter(BrycePillarsConfiguration::erosionStrength)
    ).apply(instance, BrycePillarsConfiguration::new));
}
