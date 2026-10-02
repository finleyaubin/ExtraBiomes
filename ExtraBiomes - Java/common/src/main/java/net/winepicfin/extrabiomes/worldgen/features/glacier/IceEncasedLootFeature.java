package net.winepicfin.extrabiomes.worldgen.features.glacier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.storage.loot.LootTable;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class IceEncasedLootFeature implements Feature {
    public static final MapCodec<IceEncasedLootFeature> CODEC = MapCodec.unit(IceEncasedLootFeature::new);

    private static final ResourceKey<LootTable> LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "chests/ice_vault"));

    @Override
    public MapCodec<IceEncasedLootFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        if (!isSolidCube(level, origin)) {
            return false;
        }

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-1, -1, -1), origin.offset(1, 1, 1))) {
            level.setBlock(pos, Blocks.ICE.defaultBlockState(), 2);
        }
        BlockState chest = Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
        level.setBlock(origin, chest, 2);
        RandomizableContainer.setBlockEntityLootTable(level, random, origin, LOOT_TABLE);
        return true;
    }

    private static boolean isSolidCube(WorldGenLevel level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-1, -1, -1), center.offset(1, 1, 1))) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity()) {
                return false;
            }
        }
        return true;
    }
}
