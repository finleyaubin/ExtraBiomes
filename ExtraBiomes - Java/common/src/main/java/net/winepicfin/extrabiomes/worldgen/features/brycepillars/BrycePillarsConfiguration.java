package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;
import java.util.Optional;

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
 * <p>
 * {@code capMaterial}, when present, replaces the banded material for just the topmost row of
 * each pillar (see {@link BrycePillarsFeature#placePillar}) - e.g. a grass block cap on
 * jungle_pillars' stone spires gives vanilla's already-wired-in {@code addJungleTrees}/
 * {@code addJungleGrass} placed features (see JunglePillars.java) valid ground to land jungle
 * trees/vegetation on top of, the same way real-world jungle-topped karst towers work. Empty for
 * every other biome using this feature, which keeps their pillars exactly as before.
 */
public record BrycePillarsConfiguration(BlockState backgroundMaterial, List<BlockState> streakPalette, int minHeight, int maxHeight, float threshold, int maxRadius, float erosionStrength, Optional<BlockState> capMaterial) implements FeatureConfiguration {
    // threshold raised 0.55 -> 0.85 since abs(simplex) cleared 0.55 (and even 0.75) on far too many columns for sparse, isolated Bryce Canyon-style hoodoos; maxHeight raised 15 -> 48 so the tallest pillars tower above the old vanilla mesa cap.
    public BrycePillarsConfiguration(BlockState backgroundMaterial, List<BlockState> streakPalette) {
        this(backgroundMaterial, streakPalette, Optional.empty());
    }

    public BrycePillarsConfiguration(BlockState backgroundMaterial, List<BlockState> streakPalette, Optional<BlockState> capMaterial) {
        this(backgroundMaterial, streakPalette, 5, 48, 0.97F, 4, 1.5F, capMaterial);
    }

    public static final Codec<BrycePillarsConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("background_material").forGetter(BrycePillarsConfiguration::backgroundMaterial),
            BlockState.CODEC.listOf().optionalFieldOf("streak_palette", List.of()).forGetter(BrycePillarsConfiguration::streakPalette),
            Codec.intRange(0, 96).optionalFieldOf("min_height", 5).forGetter(BrycePillarsConfiguration::minHeight),
            Codec.intRange(0, 96).optionalFieldOf("max_height", 48).forGetter(BrycePillarsConfiguration::maxHeight),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("threshold", 0.97F).forGetter(BrycePillarsConfiguration::threshold),
            Codec.intRange(0, 16).optionalFieldOf("max_radius", 4).forGetter(BrycePillarsConfiguration::maxRadius),
            Codec.floatRange(0.0F, 8.0F).optionalFieldOf("erosion_strength", 1.5F).forGetter(BrycePillarsConfiguration::erosionStrength),
            BlockState.CODEC.optionalFieldOf("cap_material").forGetter(BrycePillarsConfiguration::capMaterial)
    ).apply(instance, BrycePillarsConfiguration::new));
}
