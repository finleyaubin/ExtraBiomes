package net.winepicfin.extrabiomes.worldgen.features.glacier;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Set;

// Vanilla's forest rock only rests on dirt or stone, so it would bury itself under Glacier's ice and snow.
public class ErraticFeature extends Feature<NoneFeatureConfiguration> {
    private static final Set<net.minecraft.world.level.block.Block> RESTING_BLOCKS =
            Set.of(Blocks.SNOW_BLOCK, Blocks.PACKED_ICE, Blocks.ICE, Blocks.BLUE_ICE, Blocks.STONE, Blocks.GRAVEL);
    private static final BlockState ROCK = Blocks.ANDESITE.defaultBlockState();

    public ErraticFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        while (origin.getY() > level.getMinY() + 3 && !RESTING_BLOCKS.contains(level.getBlockState(origin.below()).getBlock())) {
            origin = origin.below();
        }
        if (origin.getY() <= level.getMinY() + 3) {
            return false;
        }

        for (int blob = 0; blob < 3; blob++) {
            int x = random.nextInt(2);
            int y = random.nextInt(2);
            int z = random.nextInt(2);
            float radius = (x + y + z) * 0.333F + 0.5F;
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-x, -y, -z), origin.offset(x, y, z))) {
                if (pos.distSqr(origin) <= radius * radius) {
                    level.setBlock(pos, ROCK, 4);
                }
            }
            origin = origin.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }
        return true;
    }
}
