package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.BiomeClimateTuning;
import terrablender.util.LevelUtils;

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
//
// The one substantive difference from the Forge test is which level gets searched. Forge runs its
// game tests inside a normal dedicated server, so helper.getLevel() there *is* a regular
// TerraBlender-patched overworld. Fabric instead runs vanilla's headless GameTestServer, which
// builds its level from WorldPresets.FLAT - a superflat FlatLevelSource whose FixedBiomeSource can
// only ever return plains, and which TerraBlender skips entirely (LevelUtils only applies to a
// NoiseBasedChunkGenerator backed by a MultiNoiseBiomeSource). Searching helper.getLevel()
// directly therefore reports every mod biome as missing no matter how the regions are configured.
// So build the normal overworld generator from the world preset registry and initialize
// TerraBlender against it exactly as a real server start would, then search that.
public class BiomeGenerationGameTests {
    private static final int SEARCH_RADIUS_BLOCKS = 15_000;
    private static final int SEARCH_INCREMENT_BLOCKS = 32;
    private static final int SEARCH_STEP_BLOCKS = 128;

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty", timeoutTicks = 60000)
    public static void allModBiomesAppearInOverworldGeneration(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        RegistryAccess registryAccess = level.registryAccess();
        long seed = level.getSeed();

        LevelStem overworld = WorldPresets.getNormalOverworld(registryAccess);
        NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator) overworld.generator();
        LevelUtils.initializeBiomes(registryAccess, overworld.type(), LevelStem.OVERWORLD, generator, seed);

        BiomeSource biomeSource = generator.getBiomeSource();
        RandomState randomState = RandomState.create(generator.generatorSettings().value(), registryAccess.lookupOrThrow(Registries.NOISE), seed);
        BlockPos origin = level.getSharedSpawnPos();

        List<String> missing = new ArrayList<>();
        for (String expectedPath : BiomeClimateTuning.BY_BEDROCK_KEY.keySet()) {
            Pair<BlockPos, Holder<Biome>> found = biomeSource.findClosestBiome3d(origin, SEARCH_RADIUS_BLOCKS, SEARCH_INCREMENT_BLOCKS, SEARCH_STEP_BLOCKS, holder -> matchesPath(holder, expectedPath), randomState.sampler(), level);
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
