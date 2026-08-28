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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reconstructs the "bryce pillar" rock formations Bedrock's "minecraft:mesa" surface builder adds
 * whenever "bryce_pillars": true (cold_mesa_bryce / desert_bryce / shattered_swamp /
 * shattered_taiga_spikes / jungle_pillars). The real reference (see the shattered_swamp screenshot
 * in this repo's README) is not a field of isolated round hoodoo cones - it's a handful of tall,
 * wide, flat-sided rock FINS with a jagged, saw-tooth crest, sparsely spaced with large open ground
 * between them. So instead of a per-column radius/height (a cone), each formation here is a single
 * elongated rectangle in the XZ plane - an origin point, a random orientation, a length, and a
 * thickness - extruded upward with its top edge height varying along its own length via a ridged
 * noise profile (steep, uneven peaks and valleys, tapering to nothing at both ends) rather than
 * tapering to a single point. That's what produces a "wall with broken teeth" silhouette instead of
 * a spike. Formations are placed sparsely: a column only becomes an origin if it clears
 * {@link BrycePillarsConfiguration#threshold()} on a coarse mask field AND is the maximum among
 * neighbours {@link #ORIGIN_SPACING_RADIUS} blocks away (not just adjacent columns), which is what
 * keeps formations from crowding - the previous per-column approach cleared entire chunks nearly
 * solid because every column got its own chance to spawn something. The mask field's sample
 * coordinates are still pushed around by a coarse warp pair (see {@link #maskAt}) so origins cluster
 * into loose groups rather than a perfectly even lattice. The materials (and height/thickness/
 * rarity/edge-roughness tuning) come from the per-biome {@link BrycePillarsConfiguration}, and the
 * 192-layer terracotta-style Y banding ({@link #getBandedMaterial}) is unchanged from before.
 */
public class BrycePillarsFeature extends Feature<BrycePillarsConfiguration> {
    // Decides WHERE a fin's origin column is. Coarser-feeling than its NOISE_SCALE alone suggests, because ORIGIN_SPACING_RADIUS enforces real separation between accepted origins on top of the raw threshold.
    private static final PerlinSimplexNoise FIN_ORIGIN_NOISE = new PerlinSimplexNoise(RandomSource.create(2345L), List.of(0));
    private static final PerlinSimplexNoise PILLAR_ROOF_NOISE = new PerlinSimplexNoise(RandomSource.create(4321L), List.of(0));
    // Reused for the fin's edge roughness (see placeFin) - same "weathered, not a razor-straight wall" role erosion played on the old cone's radius.
    private static final PerlinSimplexNoise EROSION_NOISE = new PerlinSimplexNoise(RandomSource.create(9876L), List.of(0));
    private static final PerlinSimplexNoise BAND_OFFSET_NOISE = new PerlinSimplexNoise(RandomSource.create(1357L), List.of(0));
    private static final PerlinSimplexNoise WARP_NOISE_X = new PerlinSimplexNoise(RandomSource.create(2468L), List.of(0));
    private static final PerlinSimplexNoise WARP_NOISE_Z = new PerlinSimplexNoise(RandomSource.create(8642L), List.of(0));
    // Sampled at several coordinate offsets (see place()) to pick one fin's orientation, length, and the per-fin seed RIDGE_NOISE uses - stable because each is keyed only on the fin's own origin (x, z).
    private static final PerlinSimplexNoise FIN_NOISE = new PerlinSimplexNoise(RandomSource.create(1122L), List.of(0));
    // Drives the jagged "broken teeth" crest profile along one fin's length - see computeFinColumnHeight.
    private static final PerlinSimplexNoise RIDGE_NOISE = new PerlinSimplexNoise(RandomSource.create(7913L), List.of(0));
    private static final double NOISE_SCALE = 0.25D;
    private static final double EROSION_SCALE = 0.35D;
    private static final double EROSION_SMOOTH_SCALE = 0.12D;
    private static final double EROSION_FINE_WEIGHT = 0.6D;
    private static final double EROSION_SMOOTH_WEIGHT = 0.4D;
    private static final double BAND_OFFSET_COORD_SCALE = 0.15D;
    private static final double BAND_OFFSET_MAX = 7.0D;
    private static final double ROOF_INFLUENCE = 0.15D;
    private static final double REGIONAL_HEIGHT_INFLUENCE = 0.3D;
    private static final double WARP_COORD_SCALE = 0.05D;
    private static final double WARP_STRENGTH = 12.0D;
    private static final double FIN_COORD_SCALE = 0.05D;
    // How far away (in blocks) a candidate origin must beat every other candidate before it's accepted - the actual density control now, since it directly bounds how close two fins' origins can be regardless of how permissive the mask threshold is.
    private static final int ORIGIN_SPACING_RADIUS = 14;
    private static final int FIN_LENGTH_MIN = 8;
    private static final int FIN_LENGTH_MAX = 18;
    // One full ridge/valley roughly every 1/RIDGE_SCALE blocks along a fin's length - tuned so an 8-18 block fin gets several distinct "teeth" rather than one smooth hump.
    private static final double RIDGE_SCALE = 0.22D;
    // Keeps a jagged valley from ever fully hitting 0 baseline height (before the end-taper envelope) - a real "broken tooth" crest still has some wall left between teeth, not a total gap.
    private static final double RIDGE_BASELINE = 0.45D;
    private static final int MIN_TAPER_HEIGHT = 2;
    // Fraction of the biome's own erosionStrength used to wobble a fin's side edges - kept well under 1.0 so the wall stays readable as a wall rather than dissolving into the old cone's radial noise look.
    private static final double EDGE_ROUGHNESS_FACTOR = 0.5D;
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

        // Scoped to this one place() call and shared by every maskAt/regional-maximum/regionalHeight lookup below - warpAt is a pure function of (x, z), so a coordinate queried more than once (e.g. by two different origin checks, or by isRegionalMaximum looking outward from two nearby columns) only ever costs one noise evaluation.
        Map<Long, double[]> warpCache = new HashMap<>();

        boolean placedAny = false;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = chunkX + dx;
                int z = chunkZ + dz;

                double mask = maskAt(FIN_ORIGIN_NOISE, warpCache, x, z);
                if (mask <= config.threshold()) continue;
                if (!isRegionalMaximum(FIN_ORIGIN_NOISE, warpCache, x, z, mask, ORIGIN_SPACING_RADIUS)) continue;

                // OCEAN_FLOOR_WG ignores fluids, unlike WORLD_SURFACE_WG which counted lake/pond tops as "surface" and planted formations floating on water; skipping whenever the two heights differ keeps origins off water entirely (the fin's own column-by-column fill in placeFin repeats this check for every column it spans, not just the origin).
                int floorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                if (surfaceY != floorY) continue;

                double strength = (mask - config.threshold()) / (1.0D - config.threshold());
                // warpAt(warpCache, x, z)[0] is exactly the WARP_NOISE_X sample regionalHeight needs (pre * WARP_STRENGTH) - dividing it back out reuses the cached warp instead of resampling the same noise field a second time.
                double regionalHeight = warpAt(warpCache, x, z)[0] / WARP_STRENGTH;
                double roof = PILLAR_ROOF_NOISE.getValue(x * NOISE_SCALE, z * NOISE_SCALE, false);
                int crestHeight = computeCrestHeight(config, strength, roof, regionalHeight);
                if (crestHeight <= 0) continue;

                // Angle, length, and the ridge-profile seed are each their own FIN_NOISE sample at a well-separated coordinate offset (same trick BULGE_NOISE used previously) - independent-feeling per fin, but still a stable, deterministic function of the fin's own origin.
                double angle = FIN_NOISE.getValue((x + 9000) * FIN_COORD_SCALE, (z + 9000) * FIN_COORD_SCALE, false) * Math.PI;
                double lengthT = (FIN_NOISE.getValue((x + 1000) * FIN_COORD_SCALE, (z + 1000) * FIN_COORD_SCALE, false) + 1.0D) / 2.0D;
                int length = (int) Math.round(FIN_LENGTH_MIN + lengthT * (FIN_LENGTH_MAX - FIN_LENGTH_MIN));
                int halfThickness = Math.max(1, config.maxRadius() / 2);
                double ridgeSeed = FIN_NOISE.getValue((x + 5000) * FIN_COORD_SCALE, (z + 5000) * FIN_COORD_SCALE, false) * 1000.0D;

                placedAny |= placeFin(level, x, z, angle, length, halfThickness, crestHeight, ridgeSeed, bands, config);
            }
        }
        return placedAny;
    }

    /**
     * The fin's average crest height, from how strongly its origin column cleared the mask
     * threshold ({@code strength}), a finer "roof" wobble, and a coarse regional bias
     * ({@code regionalHeight}, the caller's already-cached warp sample) so fins in the same
     * warp-driven region trend toward a shared height band rather than each rolling independently.
     */
    private static int computeCrestHeight(BrycePillarsConfiguration config, double strength, double roof, double regionalHeight) {
        int span = config.maxHeight() - config.minHeight();
        int height = config.minHeight() + (int) Math.round(strength * span)
                + (int) Math.round(roof * span * ROOF_INFLUENCE)
                + (int) Math.round(regionalHeight * span * REGIONAL_HEIGHT_INFLUENCE);
        return Math.max(config.minHeight(), Math.min(config.maxHeight(), height));
    }

    /**
     * Fills one fin: a rectangle {@code length} blocks long (in the {@code angle} direction) and
     * {@code 2 * halfThickness} blocks wide, its edges wobbled by {@code erosionAt} for a weathered
     * rather than razor-straight side profile, with each column's height set by
     * {@link #computeFinColumnHeight} - a jagged ridge line that tapers to nothing at both ends
     * instead of a single point at one centre. Every column re-checks its own ground height and the
     * water skip, since a fin can span very different terrain along its length. Returns whether it
     * placed at least one block.
     */
    private static boolean placeFin(WorldGenLevel level, int originX, int originZ, double angle, int length, int halfThickness,
                                     int crestHeight, double ridgeSeed, List<BlockState> bands, BrycePillarsConfiguration config) {
        double dirAlongX = Math.cos(angle);
        double dirAlongZ = Math.sin(angle);
        double dirAcrossX = -dirAlongZ;
        double dirAcrossZ = dirAlongX;

        int reach = length + halfThickness + 2;
        boolean placedAny = false;
        // erosionAt(worldX, worldZ) is a pure function of the world column, but the rotated-rectangle scan below can revisit a column's edge check more than once for a wide/short fin - cached per fin the same way the old cone cached it per pillar.
        Map<Long, Double> edgeCache = new HashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                double along = dx * dirAlongX + dz * dirAlongZ;
                if (along < 0.0D || along > length) continue;
                double across = dx * dirAcrossX + dz * dirAcrossZ;

                int worldX = originX + dx;
                int worldZ = originZ + dz;
                double edgeWobble = edgeCache.computeIfAbsent(packKey(worldX, worldZ), key -> erosionAt(worldX, worldZ))
                        * config.erosionStrength() * EDGE_ROUGHNESS_FACTOR;
                if (Math.abs(across) > halfThickness + edgeWobble) continue;

                // OCEAN_FLOOR_WG/WORLD_SURFACE_WG water skip, per column - see place()'s own copy of this check for why.
                int floorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, worldX, worldZ);
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ);
                if (surfaceY != floorY) continue;

                int topHeight = computeFinColumnHeight(along, length, crestHeight, ridgeSeed);
                if (topHeight <= 0) continue;

                int baseY = floorY;
                BlockState anchorState = level.getBlockState(new BlockPos(worldX, baseY - 1, worldZ));
                for (int y = baseY; y < baseY + topHeight; y++) {
                    boolean isBaseRow = y == baseY;
                    boolean isTopRow = y == baseY + topHeight - 1;
                    BlockState material = isTopRow && config.capMaterial().isPresent()
                            ? config.capMaterial().get()
                            : getBandedMaterial(bands, worldX, y, worldZ, isBaseRow, anchorState);
                    pos.set(worldX, y, worldZ);
                    level.setBlock(pos, material, 2);
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }

    /**
     * This column's height within its fin: an envelope that rises from {@link #MIN_TAPER_HEIGHT} at
     * both ends of the fin up to {@code crestHeight} at the middle ({@code sin} of the position
     * along the fin's length), multiplied by a ridged noise factor ({@code 1 - |noise|}, which
     * produces sharp peaks and deep, narrow valleys rather than smooth rolling bumps) so the top
     * edge reads as broken/jagged teeth instead of one smooth rounded hump. {@code ridgeSeed} is
     * this fin's own stable seed (see {@code place()}), used as the noise field's second coordinate
     * so different fins get different, unrelated tooth patterns instead of all reusing the same one.
     */
    private static int computeFinColumnHeight(double along, int length, int crestHeight, double ridgeSeed) {
        double t = length > 0 ? Math.min(1.0D, Math.max(0.0D, along / length)) : 0.5D;
        double envelope = Math.sin(t * Math.PI);
        double envelopeHeight = MIN_TAPER_HEIGHT + envelope * Math.max(0, crestHeight - MIN_TAPER_HEIGHT);
        double ridged = 1.0D - Math.abs(RIDGE_NOISE.getValue(along * RIDGE_SCALE, ridgeSeed, false));
        double jaggedFactor = RIDGE_BASELINE + (1.0D - RIDGE_BASELINE) * ridged;
        return Math.max(0, (int) Math.round(envelopeHeight * jaggedFactor));
    }

    private static boolean isRegionalMaximum(PerlinSimplexNoise field, Map<Long, double[]> warpCache, int x, int z, double mask, int radius) {
        return mask >= maskAt(field, warpCache, x + radius, z)
                && mask >= maskAt(field, warpCache, x - radius, z)
                && mask >= maskAt(field, warpCache, x, z + radius)
                && mask >= maskAt(field, warpCache, x, z - radius);
    }

    /**
     * The mask value driving both the threshold check and the regional-maximum comparison, with the
     * sample coordinates first pushed around by {@link #warpAt}. Those two warp fields are much
     * coarser (see {@link #WARP_COORD_SCALE}) than {@link #FIN_ORIGIN_NOISE} itself, so the
     * displacement drifts slowly across the world instead of jittering column to column - the effect
     * is that {@link #FIN_ORIGIN_NOISE}'s otherwise-regular lattice of local maxima gets stretched
     * and compressed region by region, producing loose groups of fins in some areas and long open
     * gaps in others rather than one somewhere-nearby every few dozen blocks everywhere.
     */
    private static double maskAt(PerlinSimplexNoise field, Map<Long, double[]> warpCache, int x, int z) {
        double[] warp = warpAt(warpCache, x, z);
        return Math.abs(field.getValue((x + warp[0]) * NOISE_SCALE, (z + warp[1]) * NOISE_SCALE, false));
    }

    /**
     * The warp displacement for one (x, z) column, cached in {@code warpCache} - a pure function of
     * (x, z) alone, so every caller within the same {@link #place} invocation that ends up asking
     * for the same coordinate shares one computation instead of resampling
     * {@link #WARP_NOISE_X}/{@link #WARP_NOISE_Z} from scratch every time.
     */
    private static double[] warpAt(Map<Long, double[]> warpCache, int x, int z) {
        return warpCache.computeIfAbsent(packKey(x, z), key -> {
            double warpX = WARP_NOISE_X.getValue(x * WARP_COORD_SCALE, z * WARP_COORD_SCALE, false) * WARP_STRENGTH;
            double warpZ = WARP_NOISE_Z.getValue(x * WARP_COORD_SCALE, z * WARP_COORD_SCALE, false) * WARP_STRENGTH;
            return new double[]{warpX, warpZ};
        });
    }

    private static long packKey(int x, int z) {
        return (((long) x) << 32) | (z & 0xFFFFFFFFL);
    }

    /**
     * The edge-roughness offset for one (x, z) sample, blended from two octaves of
     * {@link #EROSION_NOISE} at different scales rather than one raw high-frequency sample - a
     * single fine field jumps around from block to block (reads as grainy/noisy), so mixing in a
     * slow-varying coarse sample pulls neighbouring blocks' offsets toward a shared local trend
     * while the fine sample still supplies the smaller-scale roughness that keeps the edge from
     * going perfectly straight.
     */
    private static double erosionAt(int x, int z) {
        double fine = EROSION_NOISE.getValue(x * EROSION_SCALE, z * EROSION_SCALE, false);
        double smooth = EROSION_NOISE.getValue(x * EROSION_SMOOTH_SCALE, z * EROSION_SMOOTH_SCALE, false);
        return fine * EROSION_FINE_WEIGHT + smooth * EROSION_SMOOTH_WEIGHT;
    }

    /**
     * Picks this column's block for world-height {@code y}, following the Minecraft Wiki's own
     * description of vanilla badlands banding: {@code layers[(noiseValue + Y + 192) % 192]}.
     * {@code bands} is the (cached, world-seed-derived) 192-entry array - see
     * {@link #getOrBuildBands}. Because the index depends only on {@code y} (offset by a smooth
     * per-column noise wobble, capped at the wiki's stated &#177;7 blocks) and never on this
     * particular formation's own base or height, the same world Y always resolves to the same
     * colour everywhere this feature runs - matching how vanilla's real terracotta bands work,
     * not just within one formation.
     * <p>
     * The single exception is the bottommost row ({@code isBaseRow}): when the real block one
     * below the column's base ({@code anchorState}) is a colour that exists in this biome's
     * palette, that exact colour is used directly instead of whatever the formula would have
     * picked - so the footing joins seamlessly with the real {@code SurfaceRules.bandlands()}
     * terrain it's standing on, without that one row breaking the "same Y = same colour
     * everywhere" property for every row above it.
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
