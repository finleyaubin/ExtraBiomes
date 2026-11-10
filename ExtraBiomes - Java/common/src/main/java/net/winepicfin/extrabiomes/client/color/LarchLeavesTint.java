package net.winepicfin.extrabiomes.client.color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Tints larch leaves from {@link LarchLeavesColormap}: biome temperature picks gold or green, position picks the shade. */
public class LarchLeavesTint implements BlockColor, ItemColor {
    /** Autumn gold, for leaves shown without a world around them (inventory, held item). */
    public static final int OUT_OF_WORLD_COLOR = 0xEDB42B;

    /** Encodes biome temperature as a grey level so vanilla's biome blending smooths it across biome borders. */
    public static final ColorResolver WARMTH_RESOLVER = (biome, x, z) -> greyLevel(biome.getBaseTemperature());

    // Roughly one shade change every 12 blocks, so each tree reads as its own colour.
    private static final double VARIATION_NOISE_FREQUENCY = 0.08;

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level == null || pos == null) {
            return OUT_OF_WORLD_COLOR;
        }
        float warmth = (level.getBlockTint(pos, WARMTH_RESOLVER) & 0xFF) / 255.0F;
        return LarchLeavesColormap.colorAt(warmth, variationAt(pos));
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return OUT_OF_WORLD_COLOR;
    }

    private static float variationAt(BlockPos pos) {
        double noise = Biome.BIOME_INFO_NOISE.getValue(pos.getX() * VARIATION_NOISE_FREQUENCY, pos.getZ() * VARIATION_NOISE_FREQUENCY, false);
        return (float) ((noise + 1.0) / 2.0);
    }

    private static int greyLevel(float temperature) {
        int level = Math.round(Mth.clamp(temperature, 0.0F, 1.0F) * 255.0F);
        return level << 16 | level << 8 | level;
    }
}
