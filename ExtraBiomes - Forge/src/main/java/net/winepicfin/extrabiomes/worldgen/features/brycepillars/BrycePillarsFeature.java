package net.winepicfin.extrabiomes.worldgen.features.brycepillars;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

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
 * (see the {@code erosion}/{@code erosionScale} maths in {@link #place}). The materials (and the
 * height/radius/rarity/erosion tuning) are all pulled from the per-biome
 * {@link BrycePillarsConfiguration}, so the same shape logic reproduces every one of the Bedrock
 * biomes' distinct clay/hardened-clay combinations.
 */
public class BrycePillarsFeature extends Feature<BrycePillarsConfiguration> {
    // Static, world-seed-agnostic noise fields shared by every biome using this feature - mirrors
    // vanilla's old MesaSurfaceBuilder having one pillarNoise/pillarRoofNoise pair for the whole
    // mesa family; only the materials (and optionally height/threshold) vary per biome via config.
    private static final PerlinSimplexNoise PILLAR_NOISE = new PerlinSimplexNoise(RandomSource.create(2345L), List.of(0));
    private static final PerlinSimplexNoise PILLAR_ROOF_NOISE = new PerlinSimplexNoise(RandomSource.create(4321L), List.of(0));
    // Finer-grained than PILLAR_NOISE on purpose - the mask/roof fields decide where a pillar
    // exists and how tall it grows, this one just roughs up the resulting cone's surface.
    private static final PerlinSimplexNoise EROSION_NOISE = new PerlinSimplexNoise(RandomSource.create(9876L), List.of(0));
    private static final double NOISE_SCALE = 0.25D;
    private static final double EROSION_SCALE = 0.6D;
    private static final double ROOF_INFLUENCE = 0.25D;
    private static final double HARD_CLAY_FRACTION = 0.7D;

    public BrycePillarsFeature(Codec<BrycePillarsConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BrycePillarsConfiguration> context) {
        WorldGenLevel level = context.level();
        BrycePillarsConfiguration config = context.config();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        // Normalize to the chunk's corner regardless of what x/z the placement modifiers picked -
        // this feature scans the whole 16x16 column grid itself rather than placing at one point.
        int chunkX = origin.getX() & ~15;
        int chunkZ = origin.getZ() & ~15;

        boolean placedAny = false;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = chunkX + dx;
                int z = chunkZ + dz;

                double mask = Math.abs(PILLAR_NOISE.getValue(x * NOISE_SCALE, z * NOISE_SCALE, false));
                if (mask <= config.threshold()) continue;

                // A smooth noise field clears the threshold across a whole contiguous patch of
                // columns, not just one - without this, every column in that patch would
                // independently draw its own wide cone (below), compounding into one big chaotic
                // mound instead of a single isolated spire. Requiring this column to be a local
                // maximum among its 4 cardinal neighbours collapses each patch down to the one
                // column that actually becomes a pillar, which is also what keeps these scarce
                // rather than "a pillar on every other block" even with a high threshold.
                if (!isLocalMaximum(x, z, mask)) continue;

                // OCEAN_FLOOR_WG ignores fluids (the true solid ground), unlike WORLD_SURFACE_WG
                // which counts the top of a lake/pond as "surface" - using the latter for baseY
                // was planting pillars floating on top of water. Skipping whenever the two heights
                // differ at all (i.e. there's any water/lava above the solid ground here) keeps
                // pillars off water bodies entirely rather than just grounding them underwater.
                int floorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                if (surfaceY != floorY) continue;

                double roof = PILLAR_ROOF_NOISE.getValue(x * NOISE_SCALE, z * NOISE_SCALE, false);
                double strength = (mask - config.threshold()) / (1.0D - config.threshold());
                int span = config.maxHeight() - config.minHeight();
                int height = config.minHeight() + (int) Math.round(strength * span) + (int) Math.round(roof * span * ROOF_INFLUENCE);
                height = Math.max(config.minHeight(), Math.min(config.maxHeight(), height));
                if (height <= 0) continue;

                int baseY = floorY;
                int hardClayTop = baseY + (int) (height * HARD_CLAY_FRACTION);
                int maxRadius = Math.max(0, Math.min(config.maxRadius(), (int) Math.round(config.maxRadius() * strength)));
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                for (int y = baseY; y < baseY + height; y++) {
                    // Linear taper: full maxRadius at the base, shrinking to a single-block point
                    // by the pillar's own top, so each spire is wide-footed rather than a uniform
                    // 1-block-wide shaft.
                    double heightFraction = height > 1 ? (double) (y - baseY) / (height - 1) : 1.0D;
                    int radius = Math.round((float) (maxRadius * (1.0D - heightFraction)));
                    // Erosion pass: perturb the cone's radius per-block with a separate, finer
                    // noise field instead of testing a perfect circle - weathers the outline into
                    // vertical flutes/notches (Bedrock's stained_hardened_clay hoodoos never read
                    // as smooth cylinders). Weighted toward the top (heightFraction) so pillars
                    // still stand on a solid, mostly-intact base rather than eroding themselves
                    // loose at the ground.
                    double erosionScale = config.erosionStrength() * (0.4D + 0.6D * heightFraction);
                    int maxScan = radius + (int) Math.ceil(erosionScale);
                    BlockStateProvider material = y < hardClayTop ? config.hardClayMaterial() : config.clayMaterial();
                    for (int rx = -maxScan; rx <= maxScan; rx++) {
                        for (int rz = -maxScan; rz <= maxScan; rz++) {
                            // The pillar's own core column always survives erosion, no matter how
                            // strong - otherwise a harsh negative erosion sample at radius 0 (the
                            // tapered tip) could carve out the one block holding the tip up,
                            // leaving a disconnected floating cap above a gap.
                            if (rx != 0 || rz != 0) {
                                double dist = Math.sqrt(rx * rx + rz * rz);
                                double erosion = EROSION_NOISE.getValue((x + rx) * EROSION_SCALE, (z + rz) * EROSION_SCALE, false);
                                if (dist > radius + erosion * erosionScale) continue;
                            }
                            pos.set(x + rx, y, z + rz);
                            level.setBlock(pos, material.getState(random, pos), 2);
                            placedAny = true;
                        }
                    }
                }
            }
        }
        return placedAny;
    }

    private static boolean isLocalMaximum(int x, int z, double mask) {
        return mask >= Math.abs(PILLAR_NOISE.getValue((x + 1) * NOISE_SCALE, z * NOISE_SCALE, false))
                && mask >= Math.abs(PILLAR_NOISE.getValue((x - 1) * NOISE_SCALE, z * NOISE_SCALE, false))
                && mask >= Math.abs(PILLAR_NOISE.getValue(x * NOISE_SCALE, (z + 1) * NOISE_SCALE, false))
                && mask >= Math.abs(PILLAR_NOISE.getValue(x * NOISE_SCALE, (z - 1) * NOISE_SCALE, false));
    }
}
