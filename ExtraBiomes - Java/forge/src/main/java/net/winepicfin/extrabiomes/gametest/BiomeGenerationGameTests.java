package net.winepicfin.extrabiomes.gametest;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.BiomeClimateTuning;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

// Unlike the plain-JUnit parity tests (BiomeClimateParityTest, BiomeRegistrationParityTest), this
// actually spins up a real dedicated server (via `./gradlew runGameTestServer`) and searches the
// live, fully-loaded overworld chunk generator for each biome via
// ServerLevel#findClosestBiome3d - the exact same method the vanilla `/locate biome` command
// uses (LocateCommand, radius/increment/searchStep 15000/32/64). That's the difference in
// confidence versus the JUnit checks: those assert wiring exists and constants match; this
// asserts the actual generator picks each biome at all within a normal exploration distance,
// which would catch e.g. a TerraBlender region/weight misconfiguration that leaves a biome
// registered but never selected by real generation.
//
// Requires no physical structure, so it uses a shared 1x1x1 air template
// (data/extrabiomes/structures/empty.nbt) rather than one authored per test.
@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class BiomeGenerationGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SEARCH_RADIUS_BLOCKS = 15_000;
    private static final int SEARCH_INCREMENT_BLOCKS = 32;
    private static final int SEARCH_STEP_BLOCKS = 128;

    // findClosestBiome3d has no early exit for a biome that's absent (or outside the search box)
    // - it walks the *entire* 15000-block radius, which alone can take 30+ seconds on a shared
    // CI runner. Running all 26 searches back-to-back inside a single synchronous method (as
    // this used to) crams that worst case into one server tick, and ServerWatchdog force-crashes
    // the server if any one tick runs long (confirmed via a captured crash report: eight
    // consecutive full/failed sweeps alone cost 272s in well under 5 minutes). A GameTestSequence
    // instead runs one biome's search per thenExecute/thenIdle(1) step, so each *tick* only ever
    // has to absorb a single biome's worst case - comfortably under even vanilla's default
    // max-tick-time, with no need for CI to disable or stretch the watchdog for this test at all.
    //
    // Search origin is a fixed world coordinate, not level.getSharedSpawnPos(): two local runs
    // against a pinned seed (one reusing an old world save, one a fully fresh one - matching CI's
    // clean checkout) independently placed every one of the 26 biomes, including the ones CI
    // reported "NOT FOUND", within 2500-6900 blocks of BlockPos(0, 80, 0). CI's own log showed
    // those searches running to full completion (not being cut off) and still coming up empty -
    // i.e. the biome map itself was fine, but CI's *origin* was somewhere the biomes genuinely
    // aren't nearby. getSharedSpawnPos() runs Vanilla's own spawn-suitability search over
    // concurrently-generated chunks, which can converge on a different valid candidate depending
    // on chunk-completion ordering on machines with different core counts - a source of
    // environment-dependent drift that has nothing to do with the pinned world seed. Anchoring on
    // a fixed coordinate instead makes the search fully reproducible given the same seed,
    // regardless of the runner's hardware.
    @GameTest(template = "empty", timeoutTicks = 60000)
    public static void allModBiomesAppearInOverworldGeneration(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = new BlockPos(0, 80, 0);

        // Logs every biome's search result (not just failures) so a CI/local gametest log always
        // shows the actual distance-from-spawn TerraBlender placed each biome at on that seed -
        // useful for eyeballing whether a biome is merely "technically present somewhere in
        // 15000 blocks" vs. actually reachable by normal exploration, without needing to rerun
        // with extra instrumentation each time this needs checking.
        List<String> missing = new ArrayList<>();
        GameTestSequence sequence = helper.startSequence();
        for (String expectedPath : BiomeClimateTuning.BY_BEDROCK_KEY.keySet()) {
            sequence = sequence.thenExecute(() -> {
                Pair<BlockPos, Holder<Biome>> found = level.findClosestBiome3d(holder -> matchesPath(holder, expectedPath), origin, SEARCH_RADIUS_BLOCKS, SEARCH_INCREMENT_BLOCKS, SEARCH_STEP_BLOCKS);
                if (found == null) {
                    missing.add(expectedPath);
                    LOGGER.error("[BiomeGenerationGameTests] {}: NOT FOUND within {} blocks", expectedPath, SEARCH_RADIUS_BLOCKS);
                } else {
                    int dist = (int) Math.sqrt(origin.distSqr(found.getFirst()));
                    LOGGER.info("[BiomeGenerationGameTests] {}: found at {} ({} blocks from spawn)", expectedPath, found.getFirst(), dist);
                }
            }).thenIdle(1);
        }
        sequence.thenExecute(() -> helper.assertTrue(missing.isEmpty(),
                        missing.size() + "/" + BiomeClimateTuning.BY_BEDROCK_KEY.size() + " biomes not found within "
                                + SEARCH_RADIUS_BLOCKS + " blocks of spawn: " + missing))
                .thenSucceed();
    }

    private static boolean matchesPath(Holder<Biome> holder, String expectedPath) {
        return holder.unwrapKey().map(key -> key.location().getNamespace().equals(ExtraBiomes.MOD_ID)
                        && key.location().getPath().equals(expectedPath))
                .orElse(false);
    }
}
