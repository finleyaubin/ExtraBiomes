package net.winepicfin.extrabiomes.gametest;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.slf4j.Logger;

// Same rationale as BiomeGenerationGameTests - this searches the live, fully-loaded overworld
// chunk generator via ChunkGenerator#findNearestMapStructure, the exact method backing vanilla's
// `/locate structure` command (LocateCommand#locateStructure - same 100-chunk search radius,
// skipKnownStructures=false). Unlike a biome lookup, locating a real jigsaw structure has no
// standalone-generator API: internally it calls LevelReader#getChunk(x, z,
// ChunkStatus.STRUCTURE_STARTS), which actually generates chunks up to that status through the
// level's live ChunkSource. That means (unlike BiomeGenerationGameTests) there's no Fabric
// equivalent of this test - Fabric's GameTestServer runs a flat, void-biome world with no real
// chunk generator to generate from, and swapping one in (as the Fabric biome test does for biome
// sampling) doesn't help here since chunk generation itself is the thing being invoked.
@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class StructureGenerationGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    // Matches vanilla's own `/locate structure` search radius (LocateCommand.MAX_STRUCTURE_SEARCH_RADIUS), in chunks.
    private static final int SEARCH_RADIUS_CHUNKS = 100;

    @GameTest(template = "empty", timeoutTicks = 60000)
    public static void skyCityAppearsInOverworldGeneration(GameTestHelper helper) {
        LOGGER.info("[StructureGenerationGameTests] skyCityAppearsInOverworldGeneration: starting");
        ServerLevel level = helper.getLevel();
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        Registry<Structure> structures = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Holder<Structure> skyCity = structures.getHolderOrThrow(ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "sky_city")));
        BlockPos origin = new BlockPos(0, 80, 0);

        Pair<BlockPos, Holder<Structure>> found = generator.findNearestMapStructure(level, HolderSet.direct(skyCity), origin, SEARCH_RADIUS_CHUNKS, false);
        if (found == null) {
            LOGGER.error("[StructureGenerationGameTests] sky_city: NOT FOUND within {} chunks of spawn", SEARCH_RADIUS_CHUNKS);
        } else {
            int dist = (int) Math.sqrt(origin.distSqr(found.getFirst()));
            LOGGER.info("[StructureGenerationGameTests] sky_city: found at {} ({} blocks from spawn)", found.getFirst(), dist);
        }
        if (found != null) {
            LOGGER.info("[StructureGenerationGameTests] skyCityAppearsInOverworldGeneration: passed");
        } else {
            LOGGER.error("[StructureGenerationGameTests] skyCityAppearsInOverworldGeneration: failed");
        }
        helper.assertTrue(found != null, "sky_city structure not found within " + SEARCH_RADIUS_CHUNKS + " chunks of spawn");
        helper.succeed();
    }
}
