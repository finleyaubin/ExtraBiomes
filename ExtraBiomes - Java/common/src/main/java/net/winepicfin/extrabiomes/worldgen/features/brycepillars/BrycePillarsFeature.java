package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamically reconstructs the "bryce pillar" terrain bumps that Bedrock's "minecraft:mesa"
 * surface builder bakes in per-column whenever "bryce_pillars": true (see e.g.
 * packs/BP/biomes/cold_mesa_bryce.biome.json / desert_bryce / shattered_swamp /
 * shattered_taiga_spikes / jungle_pillars / lush_mesa_bryce). This is the same mechanic vanilla
 * Java used pre-Caves &amp; Cliffs (&lt;=1.17) for the old Mesa/Badlands "Bryce Canyon" variant:
 * a per-column surface-builder step that raised terrain height in a noise-gated pattern and
 * back-filled it with clay/hardened-clay, rather than the modern Eroded Badlands
 * (https://minecraft.wiki/w/Eroded_Badlands), which gets its spires from density-function-shaped
 * erosion instead of a discrete feature.
 * <p>
 * This mod's Forge biomes are grafted onto the vanilla Overworld's existing density functions
 * (see the "'bryce' variant" comments in e.g. ColdMesaBryce.java) rather than shipping their own,
 * so {@code SurfaceRules.bandlands()} (ModSurfaceRules) only reproduces the terracotta colour
 * banding - it can't add the height bumps. This feature fills that gap explicitly, exactly like
 * the pre-1.18 surface builder did.
 * <p>
 * Two independent 2D simplex noise fields are sampled per (x,z) column, mirroring that legacy
 * approach: a coarse "mask" field decides WHERE a pillar exists at all - a column only becomes a
 * pillar if its absolute mask value clears {@link BrycePillarsConfiguration#threshold()} AND it's
 * a local maximum among its 4 cardinal neighbours (a smooth field clears the threshold across a
 * whole contiguous patch, not just one column; the local-maximum check collapses each patch down
 * to a single spire instead of every column in it drawing its own overlapping cone), and a finer
 * "roof" field then modulates each pillar's height for an uneven skyline rather than uniform
 * mesas. Each pillar is a cone - full {@link BrycePillarsConfiguration#maxRadius()} at its base,
 * tapering linearly to a single-block point at its own top - rather than a uniform 1-wide shaft.
 * A third, finer noise field then runs an erosion pass over that cone, perturbing its radius
 * block-by-block so the outline reads as a weathered, fluted hoodoo instead of a smooth cylinder
 * (see the {@code erosion}/{@code erosionScale} maths in {@link #placePillar}). The materials (and the
 * height/radius/rarity/erosion tuning) are all pulled from the per-biome
 * {@link BrycePillarsConfiguration}, so the same shape logic reproduces every one of the Bedrock
 * biomes' distinct clay/hardened-clay combinations.
 */
public class BrycePillarsFeature extends Feature<BrycePillarsConfiguration> {
    // Static, world-seed-agnostic noise fields shared by every biome using this feature - mirrors vanilla's old MesaSurfaceBuilder having one pillarNoise/pillarRoofNoise pair for the whole mesa family.
    private static final PerlinSimplexNoise PILLAR_NOISE = new PerlinSimplexNoise(RandomSource.create(2345L), List.of(0));
    private static final PerlinSimplexNoise PILLAR_ROOF_NOISE = new PerlinSimplexNoise(RandomSource.create(4321L), List.of(0));
    // Finer-grained than PILLAR_NOISE on purpose - the mask/roof fields decide where a pillar exists and how tall it grows, this one just roughs up the resulting cone's surface.
    private static final PerlinSimplexNoise EROSION_NOISE = new PerlinSimplexNoise(RandomSource.create(9876L), List.of(0));
    // The wiki's "noiseValue" that shifts which of the 192 layers a given (x, y, z) reads from, making the bands read as wavy/organic instead of perfectly flat horizontal slabs.
    private static final PerlinSimplexNoise BAND_OFFSET_NOISE = new PerlinSimplexNoise(RandomSource.create(1357L), List.of(0));
    private static final double NOISE_SCALE = 0.25D;
    private static final double EROSION_SCALE = 0.6D;
    private static final double BAND_OFFSET_COORD_SCALE = 0.15D;
    private static final double BAND_OFFSET_MAX = 7.0D;
    private static final double ROOF_INFLUENCE = 0.25D;
    // Built once per (world seed, material recipe) and cached - NOT re-rolled per pillar or per chunk, which is what makes "the same Y = the same colour everywhere" true.
    private static final int BAND_LAYER_COUNT = 192;
    private static final Map<BandCacheKey, List<BlockState>> BAND_CACHE = new ConcurrentHashMap<>();

    public BrycePillarsFeature(Codec<BrycePillarsConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BrycePillarsConfiguration> context) {
        WorldGenLevel level = context.level();
        BrycePillarsConfiguration config = context.config();
        BlockPos origin = context.origin();
        // Materials are chosen deterministically by absolute Y (getBandedMaterial), so this feature needs no placement RandomSource.
        List<BlockState> bands = getOrBuildBands(level.getSeed(), config);
        // Normalize to the chunk's corner regardless of what x/z the placement modifiers picked - this feature scans the whole 16x16 column grid itself rather than placing at one point.
        int chunkX = origin.getX() & ~15;
        int chunkZ = origin.getZ() & ~15;

        boolean placedAny = false;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = chunkX + dx;
                int z = chunkZ + dz;

                double mask = Math.abs(PILLAR_NOISE.getValue(x * NOISE_SCALE, z * NOISE_SCALE, false));
                if (mask <= config.threshold()) continue;

                // A smooth noise field clears the threshold across a whole contiguous patch of columns; requiring this column to be a local maximum among its 4 cardinal neighbours collapses each patch down to a single pillar instead of a chaotic mound.
                if (!isLocalMaximum(x, z, mask)) continue;

                // OCEAN_FLOOR_WG ignores fluids, unlike WORLD_SURFACE_WG which counted lake/pond tops as "surface" and planted pillars floating on water; skipping whenever the two heights differ keeps pillars off water entirely.
                int floorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                if (surfaceY != floorY) continue;

                double strength = (mask - config.threshold()) / (1.0D - config.threshold());
                PillarShape shape = computePillarShape(config, x, z, strength);
                if (shape.height() <= 0) continue;

                int baseY = floorY;
                // Used only for the single bottommost row (in placePillar) so the pillar's footing joins the real ground colour already placed by surface rules, without breaking "same Y = same colour" for every row above.
                BlockState anchorState = level.getBlockState(new BlockPos(x, baseY - 1, z));
                placedAny |= placePillar(level, x, z, baseY, shape.height(), shape.maxRadius(), bands, anchorState, config);
            }
        }
        return placedAny;
    }

    /**
     * Derives this pillar's height and base radius from how strongly its column cleared the mask
     * threshold ({@code strength}), plus the finer "roof" noise field for an uneven skyline rather
     * than uniform mesas.
     */
    private static PillarShape computePillarShape(BrycePillarsConfiguration config, int x, int z, double strength) {
        double roof = PILLAR_ROOF_NOISE.getValue(x * NOISE_SCALE, z * NOISE_SCALE, false);
        int span = config.maxHeight() - config.minHeight();
        int height = config.minHeight() + (int) Math.round(strength * span) + (int) Math.round(roof * span * ROOF_INFLUENCE);
        height = Math.max(config.minHeight(), Math.min(config.maxHeight(), height));
        int maxRadius = Math.max(0, Math.min(config.maxRadius(), (int) Math.round(config.maxRadius() * strength)));
        return new PillarShape(height, maxRadius);
    }

    /**
     * Carves one pillar: a cone tapering linearly from {@code maxRadius} at {@code baseY} to a
     * single-block point at its own top, run through an erosion pass so its outline reads as a
     * weathered, fluted hoodoo instead of a smooth cylinder. Returns whether it placed at least
     * one block.
     */
    private static boolean placePillar(WorldGenLevel level, int x, int z, int baseY, int height, int maxRadius,
                                        List<BlockState> bands, BlockState anchorState, BrycePillarsConfiguration config) {
        boolean placedAny = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = baseY; y < baseY + height; y++) {
            double heightFraction = height > 1 ? (double) (y - baseY) / (height - 1) : 1.0D;
            int radius = Math.round((float) (maxRadius * (1.0D - heightFraction)));
            // Erosion is weighted toward the top (heightFraction) so pillars stand on a solid, mostly-intact base rather than eroding themselves loose at the ground.
            double erosionScale = config.erosionStrength() * (0.4D + 0.6D * heightFraction);
            int maxScan = radius + (int) Math.ceil(erosionScale);
            boolean isBaseRow = y == baseY;
            boolean isTopRow = y == baseY + height - 1;
            BlockState material = isTopRow && config.capMaterial().isPresent()
                    ? config.capMaterial().get()
                    : getBandedMaterial(bands, x, y, z, isBaseRow, anchorState);
            for (int rx = -maxScan; rx <= maxScan; rx++) {
                for (int rz = -maxScan; rz <= maxScan; rz++) {
                    // The pillar's own core column always survives erosion - otherwise a harsh erosion sample at the tapered tip could carve out the one block holding it up, leaving a disconnected floating cap.
                    if (rx != 0 || rz != 0) {
                        double dist = Math.sqrt(rx * rx + rz * rz);
                        double erosion = EROSION_NOISE.getValue((x + rx) * EROSION_SCALE, (z + rz) * EROSION_SCALE, false);
                        if (dist > radius + erosion * erosionScale) continue;
                    }
                    pos.set(x + rx, y, z + rz);
                    level.setBlock(pos, material, 2);
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }

    private record PillarShape(int height, int maxRadius) {
    }

    private static boolean isLocalMaximum(int x, int z, double mask) {
        return mask >= Math.abs(PILLAR_NOISE.getValue((x + 1) * NOISE_SCALE, z * NOISE_SCALE, false))
                && mask >= Math.abs(PILLAR_NOISE.getValue((x - 1) * NOISE_SCALE, z * NOISE_SCALE, false))
                && mask >= Math.abs(PILLAR_NOISE.getValue(x * NOISE_SCALE, (z + 1) * NOISE_SCALE, false))
                && mask >= Math.abs(PILLAR_NOISE.getValue(x * NOISE_SCALE, (z - 1) * NOISE_SCALE, false));
    }

    /**
     * Picks this column's block for world-height {@code y}, following the Minecraft Wiki's own
     * description of vanilla badlands banding: {@code layers[(noiseValue + Y + 192) % 192]}.
     * {@code bands} is the (cached, world-seed-derived) 192-entry array - see
     * {@link #getOrBuildBands}. Because the index depends only on {@code y} (offset by a smooth
     * per-column noise wobble, capped at the wiki's stated &#177;7 blocks) and never on this
     * particular pillar's own base or height, the same world Y always resolves to the same
     * colour everywhere this feature runs - matching how vanilla's real terracotta bands work,
     * not just within one pillar.
     * <p>
     * The single exception is the bottommost row ({@code isBaseRow}): when the real block one
     * below the pillar's base ({@code anchorState}, read in {@link #place}) is a colour that
     * exists in this biome's palette, that exact colour is used directly instead of whatever the
     * formula would have picked - so the pillar's footing joins seamlessly with the real
     * {@code SurfaceRules.bandlands()} terrain it's standing on, without that one row breaking
     * the "same Y = same colour everywhere" property for every row above it.
     */
    private static BlockState getBandedMaterial(List<BlockState> bands, int x, int y, int z, boolean isBaseRow, BlockState anchorState) {
        if (isBaseRow && bands.contains(anchorState)) {
            return anchorState;
        }
        double noiseValue = BAND_OFFSET_NOISE.getValue(x * BAND_OFFSET_COORD_SCALE, z * BAND_OFFSET_COORD_SCALE, false) * BAND_OFFSET_MAX;
        int index = Math.floorMod((int) Math.round(y + noiseValue), bands.size());
        return bands.get(index);
    }

    private static List<BlockState> getOrBuildBands(long seed, BrycePillarsConfiguration config) {
        return BAND_CACHE.computeIfAbsent(new BandCacheKey(seed, config),
                key -> generateBands(RandomSource.create(key.seed() ^ key.config().hashCode()), key.config()));
    }

    /**
     * Builds one world's worth of the 192-layer array from a biome's background/streak recipe.
     * Not a byte-for-byte reimplementation of vanilla's own (private, inaccessible without
     * hooking internal generator classes) badlands array - this is an equivalent in spirit:
     * mostly {@code backgroundMaterial} punctuated by short random-length streaks drawn from
     * {@code streakPalette}, rather than an independent random colour per layer (which would
     * read as speckled noise rather than banding). A biome with an empty streak palette (e.g.
     * jungle_pillars' flat stone) collapses this to one repeated colour, which still runs through
     * the exact same indexing logic as the terracotta biomes.
     */
    private static List<BlockState> generateBands(RandomSource random, BrycePillarsConfiguration config) {
        BlockState[] layers = new BlockState[BAND_LAYER_COUNT];
        Arrays.fill(layers, config.backgroundMaterial());
        List<BlockState> streakPalette = config.streakPalette();
        if (!streakPalette.isEmpty()) {
            int streakCount = 24 + random.nextInt(16);
            for (int i = 0; i < streakCount; i++) {
                int center = random.nextInt(BAND_LAYER_COUNT);
                int radius = random.nextInt(3) + 1;
                BlockState streak = streakPalette.get(random.nextInt(streakPalette.size()));
                for (int layerY = center - radius; layerY <= center + radius; layerY++) {
                    layers[Math.floorMod(layerY, BAND_LAYER_COUNT)] = streak;
                }
            }
        }
        return List.of(layers);
    }

    private record BandCacheKey(long seed, BrycePillarsConfiguration config) {
    }
}
