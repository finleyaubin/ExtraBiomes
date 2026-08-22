package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

/**
 * Materials + shape tuning for {@link BrycePillarsFeature}.
 * <p>
 * Per the Minecraft Wiki's description of vanilla's own badlands terracotta banding: "Each world
 * seed generates 192 layers of terracotta for each Y-coordinate to pick from... At each
 * horizontal coordinate, each layer may shift up and down by at most &#177;7 blocks based on
 * noise" - i.e. {@code layers[(noiseValue + Y + 192) % 192]}. {@code backgroundMaterial} and
 * {@code streakPalette} here are the RECIPE for that array, not the array itself -
 * {@link BrycePillarsFeature} builds the actual 192-entry array once per world seed (cached, not
 * re-rolled per pillar or per chunk) and indexes it exactly the way the wiki describes, which is
 * what makes "the same Y = the same colour everywhere in this world" hold - the actual property
 * being asked for - rather than each pillar (or each biome) picking its own independent scheme.
 * <p>
 * The same mechanism now backs the tuff/stone pillar variants too (previously flat single
 * materials) - {@code streakPalette} is just empty for a genuinely flat material like
 * jungle_pillars' stone, which collapses the whole 192-array down to one repeated colour.
 */
public record BrycePillarsConfiguration(BlockState backgroundMaterial, List<BlockState> streakPalette, int minHeight, int maxHeight, float threshold, int maxRadius, float erosionStrength) implements FeatureConfiguration {
    // threshold raised 0.55 -> 0.75 -> 0.85: abs(simplex) clears 0.55 (and even 0.75) on a large
    // share of columns, which is what made these "way too frequent" - real Bryce Canyon-style
    // hoodoos are sparse, isolated spires, not a pillar on most columns. maxHeight raised 15 -> 48
    // (near the codec's range ceiling) so the tallest pillars can tower well above the old vanilla
    // mesa height cap. maxRadius gives each pillar a wide base that tapers to a point at its own
    // height (see BrycePillarsFeature's per-column cone) instead of a uniform 1-block-wide shaft.
    // erosionStrength perturbs that cone's radius with a separate noise field so the outline is a
    // weathered, fluted silhouette instead of a perfect circle at every layer.
    public BrycePillarsConfiguration(BlockState backgroundMaterial, List<BlockState> streakPalette) {
        this(backgroundMaterial, streakPalette, 5, 48, 0.97F, 4, 1.5F);
    }

    public static final Codec<BrycePillarsConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("background_material").forGetter(BrycePillarsConfiguration::backgroundMaterial),
            BlockState.CODEC.listOf().optionalFieldOf("streak_palette", List.of()).forGetter(BrycePillarsConfiguration::streakPalette),
            Codec.intRange(0, 96).optionalFieldOf("min_height", 5).forGetter(BrycePillarsConfiguration::minHeight),
            Codec.intRange(0, 96).optionalFieldOf("max_height", 48).forGetter(BrycePillarsConfiguration::maxHeight),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("threshold", 0.97F).forGetter(BrycePillarsConfiguration::threshold),
            Codec.intRange(0, 16).optionalFieldOf("max_radius", 4).forGetter(BrycePillarsConfiguration::maxRadius),
            Codec.floatRange(0.0F, 8.0F).optionalFieldOf("erosion_strength", 1.5F).forGetter(BrycePillarsConfiguration::erosionStrength)
    ).apply(instance, BrycePillarsConfiguration::new));
}
