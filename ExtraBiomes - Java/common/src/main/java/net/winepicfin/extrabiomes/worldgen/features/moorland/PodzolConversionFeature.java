package net.winepicfin.extrabiomes.worldgen.features.moorland;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

// Bedrock's moorland_after_surface_podzol_feature: 15-160 podzol placements per chunk, with the count following a smooth noise over 80-block regions so podzol comes in broad sparse and dense areas.
public class PodzolConversionFeature extends Feature<NoneFeatureConfiguration> {
    private static final double REGION_SIZE = 80.0;
    private static final int MIN_PLACEMENTS = 15;
    private static final int MAX_PLACEMENTS = 160;
    private static final double PLACEMENTS_PER_NOISE_UNIT = 120.0;
    private static final double NOISE_OFFSET = 0.3;

    public PodzolConversionFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        int placements = placementsFor(level.getSeed(), chunkPos);
        boolean placedAny = false;

        for (int i = 0; i < placements; i++) {
            int x = chunkPos.getMinBlockX() + random.nextInt(16);
            int z = chunkPos.getMinBlockZ() + random.nextInt(16);
            BlockPos ground = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1, z);
            if (level.getBiome(ground).is(ModBiomes.MOORLANDS) && convertToPodzol(level, ground)) {
                placedAny = true;
            }
        }
        return placedAny;
    }

    private static int placementsFor(long seed, ChunkPos chunkPos) {
        double regionX = chunkPos.getMiddleBlockX() / REGION_SIZE;
        double regionZ = chunkPos.getMiddleBlockZ() / REGION_SIZE;
        int cellX = Mth.floor(regionX);
        int cellZ = Mth.floor(regionZ);
        double fractionX = regionX - cellX;
        double fractionZ = regionZ - cellZ;
        double noise = Mth.lerp2(fractionX, fractionZ,
                cornerNoise(seed, cellX, cellZ), cornerNoise(seed, cellX + 1, cellZ),
                cornerNoise(seed, cellX, cellZ + 1), cornerNoise(seed, cellX + 1, cellZ + 1));
        int placements = (int) Math.ceil((noise + NOISE_OFFSET) * PLACEMENTS_PER_NOISE_UNIT);
        return Mth.clamp(placements, MIN_PLACEMENTS, MAX_PLACEMENTS);
    }

    private static double cornerNoise(long seed, int cellX, int cellZ) {
        long cellSeed = seed ^ (cellX * 341873128712L) ^ (cellZ * 132897987541L);
        return RandomSource.create(cellSeed).nextDouble() * 2.0 - 1.0;
    }

    private static boolean convertToPodzol(WorldGenLevel level, BlockPos ground) {
        BlockState state = level.getBlockState(ground);
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)) {
            level.setBlock(ground, Blocks.PODZOL.defaultBlockState(), 2);
            return true;
        }
        return false;
    }
}
