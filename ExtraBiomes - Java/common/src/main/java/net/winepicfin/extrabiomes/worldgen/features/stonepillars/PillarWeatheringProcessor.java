package net.winepicfin.extrabiomes.worldgen.features.stonepillars;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * bDubs-style weathering pass for {@link StonePillarsFeature}: re-skins a fraction of the
 * template's plain {@code minecraft:stone} into vanilla weathered-stone variants (no new
 * textures) along a dark-at-base -&gt; light-at-crest gradient, and, once the structure is
 * actually in the world, grows vines on exposed side faces and grass/moss (with the occasional
 * sapling/bamboo/azalea/fern where a ledge is wide enough) on exposed tops. Both the reskin and
 * the vegetation use the same coherent-noise style already established by
 * {@link net.winepicfin.extrabiomes.worldgen.features.brycepillars.BrycePillarsFeature} (a static
 * seeded {@link PerlinSimplexNoise} field, sampled per-column) so variation clusters into patches
 * instead of a speckled per-block roll.
 * <p>
 * Runtime-only, like {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.PreserveBedrockProcessor} -
 * never serialized, only ever constructed once and passed to {@link StructureTemplate#placeInWorld}.
 * See that class's javadoc for why {@link #getType()} returns {@link StructureProcessorType#NOP}
 * rather than throwing or registering a real type of its own.
 */
public final class PillarWeatheringProcessor extends StructureProcessor {
    public static final PillarWeatheringProcessor INSTANCE = new PillarWeatheringProcessor();

    private static final PerlinSimplexNoise WEATHER_NOISE = new PerlinSimplexNoise(RandomSource.create(5551L), List.of(0));
    private static final PerlinSimplexNoise VEGETATION_NOISE = new PerlinSimplexNoise(RandomSource.create(9113L), List.of(0));
    private static final PerlinSimplexNoise SOIL_NOISE = new PerlinSimplexNoise(RandomSource.create(7331L), List.of(0));
    private static final double WEATHER_SCALE_XZ = 0.07D;
    private static final double WEATHER_SCALE_Y = 0.10D;
    // No template here is anywhere near this tall (tallest pillar variant is 106) - used only to
    // normalize blockInfo's template-local Y (unrotated, so it's a stable height fraction
    // regardless of which random rotation this placement picked) into a 0..1 "how close to the
    // crest is this block" gradient weight. Shorter variants simply never reach 1.0, which still
    // reads as a gradient within their own height.
    private static final double ASSUMED_MAX_TEMPLATE_HEIGHT = 100.0D;
    // Strong enough to reliably pull the very base into the deepslate/tuff buckets and the very
    // crest into diorite (see pickVariant), while the noise-driven patchiness (still roughly half
    // the final value) keeps the transition looking like weathering rather than a ruler-straight
    // band.
    private static final double GRADIENT_WEIGHT = 0.5D;

    private static final Set<Block> STONE_FAMILY = Set.of(Blocks.STONE, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE,
            Blocks.ANDESITE, Blocks.DIORITE, Blocks.CRACKED_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS,
            Blocks.DEEPSLATE, Blocks.TUFF);

    private static final double VEGETATION_SCALE = 0.05D;
    private static final float VINE_CHANCE_LOW = 0.30F;
    private static final float VINE_CHANCE_HIGH = 0.12F;
    private static final float TOP_GROWTH_CHANCE = 0.30F;

    private static final Map<Direction, BooleanProperty> VINE_FACE_PROPERTIES = new EnumMap<>(Map.of(
            Direction.NORTH, VineBlock.NORTH, Direction.SOUTH, VineBlock.SOUTH,
            Direction.EAST, VineBlock.EAST, Direction.WEST, VineBlock.WEST));

    private PillarWeatheringProcessor() {
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos,
                                                               StructureTemplate.StructureBlockInfo blockInfo,
                                                               StructureTemplate.StructureBlockInfo relativeBlockInfo,
                                                               StructurePlaceSettings settings) {
        if (!relativeBlockInfo.state().is(Blocks.STONE)) {
            return relativeBlockInfo;
        }
        BlockPos worldPos = relativeBlockInfo.pos();
        // A blend of an XZ sample and a Y-leaning sample - a single 2D field alone would only ever produce perfectly vertical bands, since PerlinSimplexNoise has no native 3D overload.
        double xz = WEATHER_NOISE.getValue(worldPos.getX() * WEATHER_SCALE_XZ, worldPos.getZ() * WEATHER_SCALE_XZ, false);
        double y = WEATHER_NOISE.getValue(worldPos.getY() * WEATHER_SCALE_Y, (worldPos.getX() - worldPos.getZ()) * WEATHER_SCALE_Y * 0.5D, false);
        double n = xz * 0.65D + y * 0.35D;

        // blockInfo.pos() is still template-local (pre-rotation), so its Y is unaffected by which random Rotation this placement picked - exactly the "fraction up the structure" this gradient needs.
        double heightFraction = Math.max(0.0D, Math.min(1.0D, blockInfo.pos().getY() / ASSUMED_MAX_TEMPLATE_HEIGHT));
        double gradientShift = (heightFraction - 0.5D) * 2.0D * GRADIENT_WEIGHT;

        BlockState variant = pickVariant(n + gradientShift);
        return variant == null ? relativeBlockInfo : new StructureTemplate.StructureBlockInfo(worldPos, variant, relativeBlockInfo.nbt());
    }

    /** Ordered darkest (deepslate/tuff, base) -&gt; lightest (diorite, crest); the middle dead zone resolves to {@code null} (unchanged stone) so the gradient still leaves bare-stone patches rather than fully recoloring the pillar. */
    @Nullable
    private static BlockState pickVariant(double n) {
        if (n < -0.85D) return Blocks.DEEPSLATE.defaultBlockState();
        if (n < -0.65D) return Blocks.TUFF.defaultBlockState();
        if (n < -0.45D) return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        if (n < -0.25D) return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        if (n < -0.05D) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        if (n < 0.05D) return null;
        if (n < 0.25D) return Blocks.COBBLESTONE.defaultBlockState();
        if (n < 0.45D) return Blocks.ANDESITE.defaultBlockState();
        if (n < 0.65D) return Blocks.DIORITE.defaultBlockState();
        return null;
    }

    /**
     * Called once, right after the structure is actually placed - scans its real bounding box for
     * stone-family faces exposed to real air/cave air. Side faces grow vines (only where there's
     * genuine open space beyond the face, not a one-block notch, so vines read as hanging off an
     * exterior wall rather than embedded in the rock); open tops get a grass/moss patch, with a
     * sapling/bamboo/azalea/fern on top where the ledge is wide enough to plausibly hold one.
     * Everything is gated by {@link #VEGETATION_NOISE} so growth clusters into patches rather than
     * an even coat, biased toward the pillar's lower reaches for an "overgrown base" look.
     * <p>
     * Candidate positions are collected from the structure's state right after placement, before
     * any of this method's own writes - so a later write (e.g. converting a ledge to grass_block)
     * can't make an earlier adjacency check (e.g. "is this neighbour still stone-family") see its
     * own prior edits.
     */
    public static void growVegetation(WorldGenLevel level, BoundingBox box, RandomSource random) {
        int sy = box.maxY() - box.minY() + 1;

        Set<BlockPos> stonePositions = new HashSet<>();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (STONE_FAMILY.contains(level.getBlockState(p).getBlock())) {
                        stonePositions.add(p);
                    }
                }
            }
        }

        for (BlockPos p : stonePositions) {
            Map<Direction, Boolean> faces = null;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos n = p.relative(dir);
                if (!isOpen(level, n) || !isOpen(level, n.relative(dir)) || !isExteriorFace(level, n, dir)) continue;
                double density = vegetationDensity(n);
                float chance = ((n.getY() - box.minY()) < sy * 0.45D ? VINE_CHANCE_LOW : VINE_CHANCE_HIGH) * (float) density;
                if (random.nextFloat() >= chance) continue;
                if (faces == null) faces = new EnumMap<>(Direction.class);
                faces.put(dir.getOpposite(), Boolean.TRUE);
            }
            if (faces != null) {
                placeVine(level, p, faces.keySet());
            }

            BlockPos above = p.above();
            if (!isOpen(level, above)) continue;
            double density = vegetationDensity(above);
            if (random.nextFloat() >= TOP_GROWTH_CHANCE * (float) density) continue;

            boolean mossy = SOIL_NOISE.getValue(p.getX() * VEGETATION_SCALE, p.getZ() * VEGETATION_SCALE, false) < 0.0D;
            level.setBlock(p, (mossy ? Blocks.MOSS_BLOCK : Blocks.GRASS_BLOCK).defaultBlockState(), Block.UPDATE_CLIENTS);

            float topRoll = random.nextFloat();
            boolean wideLedge = isWideLedge(stonePositions, level, p);
            if (wideLedge && topRoll < 0.12F) {
                level.setBlock(above, Blocks.JUNGLE_SAPLING.defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if (wideLedge && topRoll < 0.22F) {
                level.setBlock(above, Blocks.BAMBOO.defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if (topRoll < 0.34F) {
                level.setBlock(above, (mossy ? Blocks.FLOWERING_AZALEA : Blocks.AZALEA).defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if (topRoll < 0.55F) {
                level.setBlock(above, (mossy ? Blocks.FERN : Blocks.GRASS).defaultBlockState(), Block.UPDATE_CLIENTS);
            }
            // else: bare grass/moss patch, no topper.
        }
    }

    /**
     * True when {@code n} reads as genuinely open exterior space rather than a notch/alcove carved
     * into the rock's own silhouette (this structure's shape has plenty of those - a 1-2 block deep
     * pocket open on only one side still passes the straight-ahead clearance check, but visually
     * reads as a vine stuffed inside solid rock rather than hanging off a wall). Requires at least
     * one of the two directions perpendicular to the approach direction to also be open, so a cell
     * boxed in by stone on every side but the one it was reached from is rejected.
     */
    private static boolean isExteriorFace(WorldGenLevel level, BlockPos n, Direction approachDir) {
        Direction towardStone = approachDir.getOpposite();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (d == towardStone || d == approachDir) continue;
            if (isOpen(level, n.relative(d))) {
                return true;
            }
        }
        return false;
    }

    /** True when every cardinal neighbour at the same Y is also a recorded stone-family position with open air above it - a real ledge, not a single exposed nub too narrow for a tree/bamboo to plausibly fit. */
    private static boolean isWideLedge(Set<BlockPos> stonePositions, WorldGenLevel level, BlockPos p) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos n = p.relative(dir);
            if (!stonePositions.contains(n) || !isOpen(level, n.above())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isOpen(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir();
    }

    /** 0..1 clustering weight - only positions in a noise field's upper range grow anything, so vegetation reads as patches rather than an even coat. */
    private static double vegetationDensity(BlockPos pos) {
        double n = VEGETATION_NOISE.getValue(pos.getX() * VEGETATION_SCALE, pos.getZ() * VEGETATION_SCALE, false);
        return Math.max(0.0D, (n + 1.0D) / 2.0D);
    }

    private static void placeVine(WorldGenLevel level, BlockPos vinePos, Set<Direction> faces) {
        BlockState state = Blocks.VINE.defaultBlockState();
        for (Direction face : faces) {
            BooleanProperty property = VINE_FACE_PROPERTIES.get(face);
            if (property != null) {
                state = state.setValue(property, Boolean.TRUE);
            }
        }
        level.setBlock(vinePos, state, Block.UPDATE_CLIENTS);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.NOP;
    }
}
