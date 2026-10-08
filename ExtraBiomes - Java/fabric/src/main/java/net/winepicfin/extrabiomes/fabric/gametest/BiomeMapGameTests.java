package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.slf4j.Logger;
import terrablender.util.LevelUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Dev tool, not a real test: samples the real TerraBlender-patched overworld biome source for an
// arbitrary seed and writes the result as JSON, for tools/biome_map.py to turn into a PNG/HTML map
// (a Chunkbase-style seed preview that knows about ExtraBiomes). It is a no-op (passes instantly)
// unless EXTRABIOMES_MAP_OUT is set, so it costs CI nothing. Driven through environment variables so
// no Gradle plumbing is needed:
//   EXTRABIOMES_MAP_OUT      output .json path (required to enable)
//   EXTRABIOMES_MAP_SEED     numeric seed, or any text (hashed like Minecraft's world-creation screen)
//   EXTRABIOMES_MAP_CENTER_X / _CENTER_Z   block coordinates of the map centre (default 0)
//   EXTRABIOMES_MAP_RADIUS   half-width in blocks (default 4000)
//   EXTRABIOMES_MAP_STEP     blocks per map cell, multiple of 4 (default 32)
//   EXTRABIOMES_MAP_SURFACE  "false" to sample at a fixed Y=64 instead of the terrain surface
// Same generator setup as BiomeGenerationGameTests: the headless GameTestServer's own level is
// superflat, so the normal overworld generator is built from the world preset and TerraBlender is
// initialized against it, exactly as a real server start would.
public class BiomeMapGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(structure = ExtraBiomes.MOD_ID + ":empty", maxTicks = 60000)
    public void renderBiomeMap(GameTestHelper helper) {
        String out = System.getenv("EXTRABIOMES_MAP_OUT");
        if (out == null || out.isBlank()) {
            helper.succeed();
            return;
        }
        try {
            render(helper, Path.of(out));
        } catch (Exception e) {
            LOGGER.error("[BiomeMapGameTests] failed", e);
            throw new RuntimeException(e);
        }
        helper.succeed();
    }

    private static void render(GameTestHelper helper, Path out) throws Exception {
        long seed = parseSeed(env("EXTRABIOMES_MAP_SEED", "0"));
        int centerX = Integer.parseInt(env("EXTRABIOMES_MAP_CENTER_X", "0"));
        int centerZ = Integer.parseInt(env("EXTRABIOMES_MAP_CENTER_Z", "0"));
        int radius = Integer.parseInt(env("EXTRABIOMES_MAP_RADIUS", "4000"));
        int step = Math.max(4, Integer.parseInt(env("EXTRABIOMES_MAP_STEP", "32")) / 4 * 4);
        boolean surface = !"false".equalsIgnoreCase(env("EXTRABIOMES_MAP_SURFACE", "true"));

        ServerLevel level = helper.getLevel();
        RegistryAccess registryAccess = level.registryAccess();
        LevelStem overworld = WorldPresets.getNormalOverworld(registryAccess);
        NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator) overworld.generator();
        LevelUtils.initializeBiomes(registryAccess, overworld.type(), LevelStem.OVERWORLD, generator, seed);
        BiomeSource biomeSource = generator.getBiomeSource();
        RandomState randomState = RandomState.create(registryAccess.lookupOrThrow(Registries.NOISE), seed, generator.generatorSettings().value());

        int cells = Math.max(1, (radius * 2) / step);
        int minX = centerX - cells * step / 2;
        int minZ = centerZ - cells * step / 2;
        LOGGER.info("[BiomeMapGameTests] seed {} centre ({}, {}) {}x{} cells of {} blocks, surface={}", seed, centerX, centerZ, cells, cells, step, surface);

        Map<String, Integer> index = new HashMap<>();
        List<String> palette = new ArrayList<>();
        ByteBuffer data = ByteBuffer.allocate(cells * cells * 2).order(ByteOrder.LITTLE_ENDIAN);
        for (int row = 0; row < cells; row++) {
            for (int col = 0; col < cells; col++) {
                int x = minX + col * step + step / 2;
                int z = minZ + row * step + step / 2;
                int y = 64;
                if (surface) {
                    y = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, level, randomState);
                }
                Holder<Biome> biome = biomeSource.getNoiseBiome(x >> 2, y >> 2, z >> 2, randomState.sampler());
                String id = biome.unwrapKey().map(k -> k.identifier().toString()).orElse("unknown");
                Integer i = index.get(id);
                if (i == null) {
                    i = palette.size();
                    index.put(id, i);
                    palette.add(id);
                }
                data.putShort((short) (int) i);
            }
            if (row % 32 == 0) LOGGER.info("[BiomeMapGameTests] row {}/{}", row, cells);
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"seed\":").append(seed)
                .append(",\"step\":").append(step)
                .append(",\"minX\":").append(minX).append(",\"minZ\":").append(minZ)
                .append(",\"width\":").append(cells).append(",\"height\":").append(cells)
                .append(",\"surface\":").append(surface)
                .append(",\"palette\":[");
        for (int i = 0; i < palette.size(); i++) {
            if (i > 0) json.append(',');
            json.append('"').append(palette.get(i)).append('"');
        }
        json.append("],\"data\":\"").append(Base64.getEncoder().encodeToString(data.array())).append("\"}");
        if (out.getParent() != null) Files.createDirectories(out.getParent());
        Files.writeString(out, json.toString());
        LOGGER.info("[BiomeMapGameTests] wrote {} ({} biomes)", out, palette.size());
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? fallback : v.trim();
    }

    // Matches the world-creation screen: a numeric seed is used as-is, anything else is String#hashCode.
    private static long parseSeed(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return s.hashCode();
        }
    }
}
