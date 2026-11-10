package net.winepicfin.extrabiomes.client.color;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

/** Tints larch leaves from {@link LarchLeavesColormap}: biome temperature picks gold or green, position picks the shade. */
public class LarchLeavesTintSource implements BlockTintSource {
    /** Autumn gold, for leaves shown without a world around them (inventory, held item). */
    public static final int OUT_OF_WORLD_COLOR = 0xFFEDB42B;

    /** Encodes biome temperature as a grey level so vanilla's biome blending smooths it across biome borders. */
    public static final ColorResolver WARMTH_RESOLVER = (biome, x, z) -> greyLevel(biome.getBaseTemperature());

    // Roughly one shade change every 12 blocks, so each tree reads as its own colour.
    private static final double VARIATION_NOISE_FREQUENCY = 0.08;
    private static final int OPAQUE = 0xFF000000;

    @Override
    public int color(BlockState state) {
        return OUT_OF_WORLD_COLOR;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        float warmth = (level.getBlockTint(pos, WARMTH_RESOLVER) & 0xFF) / 255.0F;
        return LarchLeavesColormap.colorAt(warmth, variationAt(pos));
    }

    private static float variationAt(BlockPos pos) {
        float noise = Biome.BIOME_INFO_NOISE.get(pos.getX() * VARIATION_NOISE_FREQUENCY, pos.getZ() * VARIATION_NOISE_FREQUENCY);
        return (noise + 1.0F) / 2.0F;
    }

    private static int greyLevel(float temperature) {
        int level = Math.round(Mth.clamp(temperature, 0.0F, 1.0F) * 255.0F);
        return OPAQUE | level << 16 | level << 8 | level;
    }
}
