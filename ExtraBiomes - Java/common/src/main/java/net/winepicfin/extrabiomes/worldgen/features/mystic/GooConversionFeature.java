package net.winepicfin.extrabiomes.worldgen.features.mystic;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.winepicfin.extrabiomes.block.ModBlocks;

/**
 * Java equivalent of Bedrock's per-biome "sea_material": "extrabiomes:goo" override for Mystic
 * Forest (ExtraBiomes - Bedrock/packs/BP/biomes/mystic_forest.biome.json). Java's aquifer system
 * has no per-biome fluid override, so this instead mirrors vanilla's own
 * {@code FreezeTopLayerFeature} approach (placed at TOP_LAYER_MODIFICATION, after lakes/aquifers
 * have already generated): starting from a heightmap-placed origin (so it only ever begins right
 * at the surface, never underground), it scans straight down converting contiguous WATER blocks
 * into {@link ModBlocks#GOO}, stopping at the first non-water block or after SEARCH_DEPTH blocks -
 * whichever comes first. That depth cap, combined with only ever starting from the world surface,
 * is what keeps this from reaching sealed underground/cave water: those aren't found by scanning
 * down from the surface heightmap at all, and even a deep surface lake only converts its top
 * SEARCH_DEPTH blocks.
 */
public class GooConversionFeature extends Feature<NoneFeatureConfiguration> {

    // Deep enough for a typical surface pond/lake, shallow enough to avoid deep ocean trenches or cave-adjacent water.
    private static final int SEARCH_DEPTH = 16;

    public GooConversionFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos pos = context.origin().mutable();
        BlockState gooState = ModBlocks.GOO.get().defaultBlockState();

        boolean placedAny = false;
        for (int i = 0; i < SEARCH_DEPTH; i++) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.WATER)) {
                level.setBlock(pos, gooState, 2);
                placedAny = true;
            } else if (placedAny) {
                // Non-water after already converting some means we've reached the water body's floor.
                break;
            }
            pos.move(0, -1, 0);
        }
        return placedAny;
    }
}
