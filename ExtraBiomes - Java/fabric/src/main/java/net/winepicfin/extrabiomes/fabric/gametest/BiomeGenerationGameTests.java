package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.BiomeClimateTuning;

import java.util.ArrayList;
import java.util.List;

// Fabric equivalent of forge/gametest/BiomeGenerationGameTests.java - see that class for the
// rationale (real chunk-generator search vs the plain-JUnit parity tests). Registered via the
// "fabric-gametest" entrypoint in fabric.mod.json instead of Forge's @GameTestHolder; run with
// `./gradlew :fabric:runGameTestServer` (requires -Dfabric-api.gametest=true, wired into that
// run's vmArgs in fabric/build.gradle). Reuses the same shared empty structure template as forge
// (common/src/main/resources/data/extrabiomes/structures/empty.nbt) - unlike Forge's
// @PrefixGameTestTemplate, Fabric does not auto-prefix the template name with the mod id, so the
// namespace is spelled out explicitly below.
public class BiomeGenerationGameTests {
    private static final int SEARCH_RADIUS_BLOCKS = 15_000;
    private static final int SEARCH_INCREMENT_BLOCKS = 32;
    private static final int SEARCH_STEP_BLOCKS = 128;

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty", timeoutTicks = 60000)
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
