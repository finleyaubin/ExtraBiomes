package net.winepicfin.extrabiomes.gametest;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.BiomeClimateTuning;

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
    private static final int SEARCH_RADIUS_BLOCKS = 15_000;
    private static final int SEARCH_INCREMENT_BLOCKS = 32;
    private static final int SEARCH_STEP_BLOCKS = 128;

    @GameTest(template = "empty", timeoutTicks = 60000)
    public static void allModBiomesAppearInOverworldGeneration(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = level.getSharedSpawnPos();

        List<String> missing = new ArrayList<>();
        for (String expectedPath : BiomeClimateTuning.BY_BEDROCK_KEY.keySet()) {
            Pair<BlockPos, Holder<Biome>> found = level.findClosestBiome3d(holder -> matchesPath(holder, expectedPath), origin, SEARCH_RADIUS_BLOCKS, SEARCH_INCREMENT_BLOCKS, SEARCH_STEP_BLOCKS);
            if (found == null) {
                missing.add(expectedPath);
            }
        }
        helper.assertTrue(missing.isEmpty(),
                missing.size() + "/" + BiomeClimateTuning.BY_BEDROCK_KEY.size() + " biomes not found within "
                        + SEARCH_RADIUS_BLOCKS + " blocks of spawn: " + missing);
        helper.succeed();
    }

    private static boolean matchesPath(Holder<Biome> holder, String expectedPath) {
        return holder.unwrapKey().map(key -> key.location().getNamespace().equals(ExtraBiomes.MOD_ID)
                        && key.location().getPath().equals(expectedPath))
                .orElse(false);
    }
}
