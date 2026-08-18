package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
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
 *                     {@code new ResourceLocation("extrabiomes", "structurescatter/oasis_puddle")},
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
 * @param centered     when {@code true}, the structure's horizontal footprint is centered on the
 *                     placement origin instead of anchored at the template's local (0,0,0) corner -
 *                     for structures whose "trunk"/focal point sits in the middle of the footprint
 *                     (e.g. a huge mushroom's stem) rather than at a corner. Rotation-safe: the
 *                     centering offset is computed in the template's rotated space so the focal
 *                     point lands on the origin regardless of which {@link Rotation} is picked.
 *                     Ignored when {@code anchor} is present.
 * @param anchor       when present, this exact local (unrotated, pre-transform) point in the
 *                     template is what lands on the placement origin, instead of the local (0,0,0)
 *                     corner or the footprint center - for structures whose focal point is neither
 *                     (e.g. a leaning tree trunk whose base isn't at the bounding box's corner or
 *                     center). Rotation-safe like {@code centered}. Takes priority over
 *                     {@code centered} when both are set.
 * @param minClearFraction the minimum fraction (0.0-1.0) of the structure's rotated bounding box
 *                     that must already be air before placement is allowed; {@code 0.0F} (the
 *                     default for every existing convenience constructor) disables the check
 *                     entirely, preserving this feature's original "place unconditionally"
 *                     behaviour. Opt in for scatter-heavy subsystems (huge mushrooms, packed
 *                     tightly enough that one attempt's cap can otherwise land squarely inside an
 *                     already-placed neighbour's cap/stem) where two placements landing on top of
 *                     each other reads as broken rather than as dense/lush.
 */
public record SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean centered, Optional<BlockPos> anchor, float minClearFraction) implements FeatureConfiguration {

    public SingleStructureConfiguration(ResourceLocation structure) {
        this(structure, Optional.empty(), 0, false, Optional.empty(), 0.0F);
    }

    public SingleStructureConfiguration(ResourceLocation structure, Rotation fixedRotation) {
        this(structure, Optional.of(fixedRotation), 0, false, Optional.empty(), 0.0F);
    }

    public SingleStructureConfiguration(ResourceLocation structure, int groundOffset) {
        this(structure, Optional.empty(), groundOffset, false, Optional.empty(), 0.0F);
    }

    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset) {
        this(structure, rotation, groundOffset, false, Optional.empty(), 0.0F);
    }

    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean centered) {
        this(structure, rotation, groundOffset, centered, Optional.empty(), 0.0F);
    }

    // Mushroom-style use: fixed/random rotation + centered + a required clear-space fraction, no
    // ground offset or explicit anchor point needed.
    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean centered, float minClearFraction) {
        this(structure, rotation, groundOffset, centered, Optional.empty(), minClearFraction);
    }

    public SingleStructureConfiguration(ResourceLocation structure, BlockPos anchor) {
        this(structure, Optional.empty(), 0, false, Optional.of(anchor), 0.0F);
    }

    public static final Codec<SingleStructureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("structure").forGetter(SingleStructureConfiguration::structure),
            Codec.STRING.xmap(Rotation::valueOf, Rotation::name).optionalFieldOf("rotation").forGetter(SingleStructureConfiguration::rotation),
            Codec.INT.optionalFieldOf("ground_offset", 0).forGetter(SingleStructureConfiguration::groundOffset),
            Codec.BOOL.optionalFieldOf("centered", false).forGetter(SingleStructureConfiguration::centered),
            BlockPos.CODEC.optionalFieldOf("anchor").forGetter(SingleStructureConfiguration::anchor),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_clear_fraction", 0.0F).forGetter(SingleStructureConfiguration::minClearFraction)
    ).apply(instance, SingleStructureConfiguration::new));
}
