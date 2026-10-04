package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

// Bedrock plants each tulip colour as a 3-wide stripe along X at a fixed z per chunk (red 1, orange 5, pink 9, white 13); walking every column of the chunk reproduces those stripes without writing into neighbouring chunks.
public class NetherlandsTulipFieldFeature extends Feature<NoneFeatureConfiguration> {
    private record Stripe(int centerZ, Block tulip) {
    }

    private static final int STRIPE_RADIUS = 1;
    private static final Stripe[] STRIPES = {
            new Stripe(1, Blocks.RED_TULIP),
            new Stripe(5, Blocks.ORANGE_TULIP),
            new Stripe(9, Blocks.PINK_TULIP),
            new Stripe(13, Blocks.WHITE_TULIP),
    };

    public NetherlandsTulipFieldFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkPos chunkPos = ChunkPos.containing(context.origin());
        boolean placedAny = false;

        for (Stripe stripe : STRIPES) {
            for (int dz = -STRIPE_RADIUS; dz <= STRIPE_RADIUS; dz++) {
                for (int x = 0; x < 16; x++) {
                    int worldX = chunkPos.getMinBlockX() + x;
                    int worldZ = chunkPos.getMinBlockZ() + stripe.centerZ() + dz;
                    BlockPos target = new BlockPos(worldX, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ), worldZ);
                    if (plantTulip(level, target, stripe.tulip())) {
                        placedAny = true;
                    }
                }
            }
        }
        return placedAny;
    }

    private static boolean plantTulip(WorldGenLevel level, BlockPos target, Block tulip) {
        BlockPos ground = target.below();
        if (!level.getBiome(target).is(ModBiomes.THE_NETHERLANDS)
                || !level.getBlockState(target).isAir()
                || !level.getBlockState(ground).is(NetherlandsTulipFeatures.TULIP_REPLACEABLE)) {
            return false;
        }
        level.setBlock(ground, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
        level.setBlock(target, tulip.defaultBlockState(), 2);
        return true;
    }
}
