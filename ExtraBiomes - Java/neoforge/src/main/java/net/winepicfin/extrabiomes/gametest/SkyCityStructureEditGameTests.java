package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.slf4j.Logger;

// Not a regression test - a manual-editing rig. Pastes every sky_city building and path piece into
// a void, laid out in rows, each with its own SAVE-mode structure block already pointed at the
// right structure id and size, so fixing a mis-ported trapdoor/stair is "run this test, walk over,
// fix the block, click Save in the structure block GUI" instead of hand-editing the .nbt bytes.
@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class SkyCityStructureEditGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String[] BUILDINGS = {"house_1", "sky_challet", "tower_1", "tower_2"};
    private static final String[] PATHS = {"cross", "curve", "fountain", "path_end", "path", "roundabout", "s_bend", "straight", "t"};
    private static final int MARGIN = 4;

    // manualOnly: excluded from /test runall (and CI's discovery, see gradle-build.yml) - dev tool only.
    @GameTest(template = "sky_city_edit_void", timeoutTicks = 60000, batch = "extrabiomes", manualOnly = true)
    public static void layoutSkyCityBuildingsForEditing(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        StructureTemplateManager templates = level.getStructureManager();
        int buildingsRowDepth = placeRow(helper, level, templates, BUILDINGS, "buildings", 1);
        placeRow(helper, level, templates, PATHS, "paths", 1 + buildingsRowDepth + MARGIN);
        helper.succeed();
    }

    // Places one row of structures side by side at a fixed z, each with its own SAVE-mode structure
    // block at its corner. Returns the row's deepest structure (its z size), so the caller can stack
    // another row behind it without the two overlapping.
    private static int placeRow(GameTestHelper helper, ServerLevel level, StructureTemplateManager templates,
                                 String[] names, String subfolder, int zStart) {
        int xCursor = 1;
        int maxDepth = 0;
        for (String name : names) {
            ResourceLocation id = new ResourceLocation(ExtraBiomes.MOD_ID, "sky_city/" + subfolder + "/" + name);
            StructureTemplate template = templates.get(id).orElseThrow();
            Vec3i size = template.getSize();

            BlockPos origin = new BlockPos(xCursor, 1, zStart);
            template.placeInWorld(level, helper.absolutePos(origin), helper.absolutePos(origin),
                    new StructurePlaceSettings(), level.getRandom(), 2);

            // Structure block sits one block outside the pasted volume's corner; structurePos
            // (offset from the block to the volume) walks back to that same corner.
            BlockPos structureBlockPos = origin.offset(-1, 0, -1);
            helper.setBlock(structureBlockPos, Blocks.STRUCTURE_BLOCK.defaultBlockState());
            if (level.getBlockEntity(helper.absolutePos(structureBlockPos)) instanceof StructureBlockEntity sbe) {
                sbe.setMode(StructureMode.SAVE);
                sbe.setStructureName(id);
                sbe.setStructurePos(new BlockPos(1, 0, 1));
                sbe.setStructureSize(size);
                sbe.setShowBoundingBox(true);
                sbe.setIgnoreEntities(false);
            }

            LOGGER.info("[SkyCityStructureEditGameTests] placed {}/{} at x={} z={} (size {})", subfolder, name, xCursor, zStart, size);
            xCursor += size.getX() + MARGIN;
            maxDepth = Math.max(maxDepth, size.getZ());
        }
        return maxDepth;
    }
}
