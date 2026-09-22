package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Not a regression test - a manual-inspection rig. Walks every extrabiomes block currently in the
// block registry (so newly-added blocks just show up here, nothing to keep in sync by hand) and
// pastes every one of its possible blockstates into a void, one row per block, wrapped into a grid
// per row so a block with hundreds of states (walls, doors) doesn't produce one absurdly long row.
// Each state gets an empty gap block on every side so connecting blocks (fences/walls/panes) don't
// visually fuse into their neighbour and hide their own shape.
@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class BlockGridGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CELL_SPACING = 2; // 1 block + 1 gap, so connecting blocks don't fuse
    private static final int MAX_COLUMNS = 16;
    private static final int ROW_GAP = 3; // empty rows between one block's sub-grid and the next block's

    // manualOnly: excluded from /test runall (and CI's discovery, see gradle-build.yml) - dev tool only.
    @GameTest(template = "block_grid_void", timeoutTicks = 60000, batch = "extrabiomes", manualOnly = true)
    public static void layoutEveryBlockStateForInspection(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);

        List<Block> modBlocks = new ArrayList<>();
        for (Block block : blockRegistry) {
            ResourceLocation id = blockRegistry.getKey(block);
            if (id != null && ExtraBiomes.MOD_ID.equals(id.getNamespace())) {
                modBlocks.add(block);
            }
        }
        modBlocks.sort(Comparator.comparing(block -> blockRegistry.getKey(block).getPath()));

        int zCursor = 1;
        int totalStates = 0;
        for (Block block : modBlocks) {
            List<BlockState> states = block.getStateDefinition().getPossibleStates();
            int rowsUsed = placeBlockGrid(helper, states, zCursor);

            LOGGER.info("[BlockGridGameTests] placed {} ({} states) starting at z={}", blockRegistry.getKey(block), states.size(), zCursor);
            zCursor += rowsUsed * CELL_SPACING + ROW_GAP;
            totalStates += states.size();
        }

        LOGGER.info("[BlockGridGameTests] placed {} blocks, {} total states", modBlocks.size(), totalStates);
        helper.succeed();
    }

    // Places one block's states in a sub-grid (wrapping every MAX_COLUMNS) starting at the given z.
    // Returns how many sub-rows were used, so the caller can advance past this block's whole grid.
    private static int placeBlockGrid(GameTestHelper helper, List<BlockState> states, int zStart) {
        int column = 0;
        int row = 0;
        for (BlockState state : states) {
            int x = 1 + column * CELL_SPACING;
            int z = zStart + row * CELL_SPACING;
            helper.setBlock(new BlockPos(x, 1, z), state);

            column++;
            if (column >= MAX_COLUMNS) {
                column = 0;
                row++;
            }
        }
        return row + (column > 0 ? 1 : 0);
    }
}
