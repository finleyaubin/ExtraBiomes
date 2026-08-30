package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;
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
 * @param requireGroundedFloor when {@code true}, every column of the structure's footprint (not
 *                     just the placement origin) must have solid, non-air ground directly beneath
 *                     its lowest row before placement is allowed - Bedrock's own
 *                     {@code constraints.grounded}, which a single-column {@code HeightmapPlacement}
 *                     doesn't reproduce on its own: that only checks the origin column, so a wide
 *                     structure placed near a ledge, slope, or single-block gap can still have part
 *                     of its footprint hang over open air. {@code false} (the default for every
 *                     existing convenience constructor) disables the check, preserving this
 *                     feature's original behaviour.
 * @param requiredFloorBlocks when non-empty (and {@code requireGroundedFloor} is set), restricts the
 *                     floor to these specific blocks (e.g. sand) instead of merely non-air.
 * @param minSubmergedFraction the minimum fraction (0.0-1.0) of the structure's rotated bounding
 *                     box that must already be water (a water block, or the water side of any
 *                     waterlogged block - checked via fluid state, not block state) before
 *                     placement is allowed; {@code 0.0F} (the default for every existing
 *                     convenience constructor) disables the check entirely. Opt in for subsystems
 *                     whose template no longer bundles its own explicit water fill (relying
 *                     instead on the surrounding ocean plus its own waterlogged blocks) and so
 *                     needs a placement-time guarantee that it's actually landing underwater
 *                     rather than in air or on dry land.
 * @param embedInStone when {@code true}, {@code groundOffset} is measured from the shallowest
 *                     stone-like block under the structure's whole rotated footprint (skipping
 *                     through {@link net.minecraft.tags.BlockTags#DIRT} blocks such as grass/dirt/
 *                     podzol at each column) instead of from the origin column's
 *                     {@code WORLD_SURFACE_WG} heightmap value. A plain heightmap-relative
 *                     {@code groundOffset} only guarantees depth under one column, so a template
 *                     wider than one block can float over a dip elsewhere in its footprint, or - if
 *                     the biome's dirt layer is deeper than {@code |groundOffset|} - never actually
 *                     reach real stone at all and sit on top of the dirt instead. Taking the
 *                     shallowest stone depth across every footprint column fixes both: the anchor
 *                     always lands in stone, and it lands deep enough that no column of the
 *                     footprint is left hanging over a lower neighbour. {@code false} (the default
 *                     for every existing convenience constructor) preserves this feature's original
 *                     surface-relative behaviour.
 */
public record SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean centered, Optional<BlockPos> anchor, float minClearFraction, boolean requireGroundedFloor, List<Block> requiredFloorBlocks, float minSubmergedFraction, boolean embedInStone) implements FeatureConfiguration {

    public SingleStructureConfiguration(ResourceLocation structure) {
        this(structure, Optional.empty(), 0, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false);
    }

    public SingleStructureConfiguration(ResourceLocation structure, Rotation fixedRotation) {
        this(structure, Optional.of(fixedRotation), 0, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false);
    }

    public SingleStructureConfiguration(ResourceLocation structure, int groundOffset) {
        this(structure, Optional.empty(), groundOffset, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false);
    }

    // Jellycoral-style use: random rotation + a required submerged (water/waterlogged) fraction,
    // for templates that no longer bundle their own explicit water fill.
    public SingleStructureConfiguration(ResourceLocation structure, int groundOffset, float minSubmergedFraction) {
        this(structure, Optional.empty(), groundOffset, false, Optional.empty(), 0.0F, false, List.of(), minSubmergedFraction, false);
    }

    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset) {
        this(structure, rotation, groundOffset, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false);
    }

    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean centered) {
        this(structure, rotation, groundOffset, centered, Optional.empty(), 0.0F, false, List.of(), 0.0F, false);
    }

    // Mushroom-style use: fixed/random rotation + centered + a required clear-space fraction.
    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean centered, float minClearFraction) {
        this(structure, rotation, groundOffset, centered, Optional.empty(), minClearFraction, false, List.of(), 0.0F, false);
    }

    // Stick-pile-style use: fixed/random rotation + a required clear-space fraction + a required solid floor.
    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, float minClearFraction, boolean requireGroundedFloor) {
        this(structure, rotation, groundOffset, false, Optional.empty(), minClearFraction, requireGroundedFloor, List.of(), 0.0F, false);
    }

    // Oasis-puddle-style use: required solid floor restricted to a specific set of blocks.
    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean requireGroundedFloor, List<Block> requiredFloorBlocks) {
        this(structure, rotation, groundOffset, false, Optional.empty(), 0.0F, requireGroundedFloor, requiredFloorBlocks, 0.0F, false);
    }

    // Stone-pillar-style use: required solid floor restricted to a specific set of blocks, sunk into real stone rather than anchored to the dirt/grass surface.
    public SingleStructureConfiguration(ResourceLocation structure, Optional<Rotation> rotation, int groundOffset, boolean requireGroundedFloor, List<Block> requiredFloorBlocks, boolean embedInStone) {
        this(structure, rotation, groundOffset, false, Optional.empty(), 0.0F, requireGroundedFloor, requiredFloorBlocks, 0.0F, embedInStone);
    }

    public SingleStructureConfiguration(ResourceLocation structure, BlockPos anchor) {
        this(structure, Optional.empty(), 0, false, Optional.of(anchor), 0.0F, false, List.of(), 0.0F, false);
    }

    public static final Codec<SingleStructureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("structure").forGetter(SingleStructureConfiguration::structure),
            Codec.STRING.xmap(Rotation::valueOf, Rotation::name).optionalFieldOf("rotation").forGetter(SingleStructureConfiguration::rotation),
            Codec.INT.optionalFieldOf("ground_offset", 0).forGetter(SingleStructureConfiguration::groundOffset),
            Codec.BOOL.optionalFieldOf("centered", false).forGetter(SingleStructureConfiguration::centered),
            BlockPos.CODEC.optionalFieldOf("anchor").forGetter(SingleStructureConfiguration::anchor),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_clear_fraction", 0.0F).forGetter(SingleStructureConfiguration::minClearFraction),
            Codec.BOOL.optionalFieldOf("require_grounded_floor", false).forGetter(SingleStructureConfiguration::requireGroundedFloor),
            Codec.list(BuiltInRegistries.BLOCK.byNameCodec()).optionalFieldOf("required_floor_blocks", List.of()).forGetter(SingleStructureConfiguration::requiredFloorBlocks),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_submerged_fraction", 0.0F).forGetter(SingleStructureConfiguration::minSubmergedFraction),
            Codec.BOOL.optionalFieldOf("embed_in_stone", false).forGetter(SingleStructureConfiguration::embedInStone)
    ).apply(instance, SingleStructureConfiguration::new));
}
