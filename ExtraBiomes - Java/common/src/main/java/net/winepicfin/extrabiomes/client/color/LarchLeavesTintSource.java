package net.winepicfin.extrabiomes.client.color;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/** Tints larch leaves from {@link LarchLeavesColormap} by world position alone, so every biome shows the same mix. */
public class LarchLeavesTintSource implements BlockTintSource {
    /** Autumn gold, for leaves shown without a world around them (inventory, held item). */
    public static final int OUT_OF_WORLD_COLOR = 0xFFEDB42B;

    // Noises use fixed seeds: leaf colour is cosmetic, so it needn't follow the world seed.
    // Broad patches, ~50 blocks across, so whole stands lean gold or green together.
    private static final double GREENNESS_NOISE_FREQUENCY = 0.02;
    private static final SimplexNoise GREENNESS_NOISE = new SimplexNoise(new LegacyRandomSource(0x6EE7L));
    // Centre and spread on the colour map's gold-to-green axis: mostly gold, with the odd greener patch.
    private static final float TYPICAL_GREENNESS = 0.3F;
    private static final float GREENNESS_SPREAD = 0.4F;

    // Roughly one shade change every 12 blocks, sideways and up, so trees differ and shift along their height.
    private static final double SHADE_NOISE_FREQUENCY = 0.08;
    private static final SimplexNoise SHADE_NOISE = new SimplexNoise(new LegacyRandomSource(0x1A4C4L));

    @Override
    public int color(BlockState state) {
        return OUT_OF_WORLD_COLOR;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return LarchLeavesColormap.colorAt(greennessAt(pos), shadeAt(pos));
    }

    private static float greennessAt(BlockPos pos) {
        float noise = GREENNESS_NOISE.get(pos.getX() * GREENNESS_NOISE_FREQUENCY, pos.getZ() * GREENNESS_NOISE_FREQUENCY);
        return Mth.clamp(TYPICAL_GREENNESS + noise * GREENNESS_SPREAD, 0.0F, 1.0F);
    }

    private static float shadeAt(BlockPos pos) {
        float noise = SHADE_NOISE.get(pos.getX() * SHADE_NOISE_FREQUENCY, pos.getY() * SHADE_NOISE_FREQUENCY, pos.getZ() * SHADE_NOISE_FREQUENCY);
        return (noise + 1.0F) / 2.0F;
    }
}
