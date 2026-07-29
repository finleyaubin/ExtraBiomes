package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Optional;

/**
 * Configuration for {@link SingleStructureFeature}: places one pre-converted .nbt structure
 * template unconditionally at the feature's origin. This is the Java-Edition equivalent of
 * Bedrock's "minecraft:structure_template_feature" - one reusable config+feature pair meant to be
 * shared by every subsystem that just needs to stamp a single raw structure into the world
 * (oasis puddles, jellycoral clusters, windmills, stone pillars, glacier snow-drifts, taiga spikes, ...).
 *
 * @param structure    the location of the converted structure template, e.g.
 *                     {@code ResourceLocation.fromNamespaceAndPath("extrabiomes", "structurescatter/oasis_puddle")},
 *                     which resolves to {@code data/extrabiomes/structures/structurescatter/oasis_puddle.nbt}.
 * @param rotation     a fixed rotation to always place with, or {@code Optional.empty()} to pick a
 *                     uniformly random rotation per placement. This maps directly onto Bedrock's
 *                     "facing_direction": when Bedrock specifies one fixed direction, pass
 *                     {@code Optional.of(...)} here; when Bedrock leaves it random/unspecified,
 *                     pass {@code Optional.empty()}.
 * @param groundOffset a vertical offset (in blocks) applied to the placement origin immediately
 *                     before the structure is placed. Use a negative value to embed the structure
 *                     into the ground - this is how you express Bedrock distribution expressions
 *                     like {@code "(query.heightmap(variable.worldx, variable.worldz))-4"}: place the
 *                     PlacedFeature on the heightmap (offset 0) and set groundOffset to -4 here.
 */
public record SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset) implements FeatureConfiguration {

    public SingleStructureConfiguration(ResourceLocation structure) {
        this(structure, Optional.empty(), 0);
    }

    public SingleStructureConfiguration(ResourceLocation structure, Rotation fixedRotation) {
        this(structure, Optional.of(fixedRotation), 0);
    }

    public SingleStructureConfiguration(ResourceLocation structure, int groundOffset) {
        this(structure, Optional.empty(), groundOffset);
    }

    public static final Codec<SingleStructureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("structure").forGetter(SingleStructureConfiguration::structure),
            Codec.STRING.xmap(Rotation::valueOf, Rotation::name).optionalFieldOf("rotation").forGetter(SingleStructureConfiguration::rotation),
            Codec.INT.optionalFieldOf("ground_offset", 0).forGetter(SingleStructureConfiguration::groundOffset)
    ).apply(instance, SingleStructureConfiguration::new));
}
