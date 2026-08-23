package net.winepicfin.extrabiomes.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.winepicfin.extrabiomes.Config;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

// Fabric's own config file backing for the loader-agnostic net.winepicfin.extrabiomes.Config
// values - no Fabric equivalent of ForgeConfigSpec's file/GUI generation exists (Cloth Config/
// AutoConfig are third-party mods, not something to add a hard dependency on), so this is a plain
// hand-rolled .properties file under the fabric config dir. See forge/.../ForgeConfig.java for
// Forge's own backing.
public class FabricConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("extrabiomes-config");
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(ExtraBiomes.MOD_ID + ".properties");

    // On the gametest server, a low weight makes narrow/rare biomes impractically hard to find
    // within a reasonable search radius (see BiomeGenerationGameTests), so force it high there
    // regardless of the configured value - real players never see this override. Mirrors
    // ForgeConfig's GAMETEST_BIOME_WEIGHT/ForgeGameTestHooks.isGametestServer() override;
    // "fabric-api.gametest" is the system property fabric/build.gradle's gameTestServer run
    // sets, Fabric's equivalent of Forge's isGametestServer() check.
    private static final int GAMETEST_BIOME_WEIGHT = 100;

    public static void load() {
        boolean isGametest = Boolean.getBoolean("fabric-api.gametest");
        Properties properties = new Properties();
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
                properties.load(in);
            } catch (IOException e) {
                LOGGER.error("Failed to read {}, using defaults", CONFIG_PATH, e);
            }
        }

        int biomeWeight = readInt(properties, "biomeWeight", Config.DEFAULT_BIOME_WEIGHT);
        int secondaryBiomeWeight = readInt(properties, "secondaryBiomeWeight", Config.DEFAULT_SECONDARY_BIOME_WEIGHT);
        int rareBiomeWeight = readInt(properties, "rareBiomeWeight", Config.DEFAULT_RARE_BIOME_WEIGHT);

        // Persist the real configured values, not the gametest override, so a gametest run
        // never overwrites a player's saved config with the forced test weight.
        properties.setProperty("biomeWeight", String.valueOf(biomeWeight));
        properties.setProperty("secondaryBiomeWeight", String.valueOf(secondaryBiomeWeight));
        properties.setProperty("rareBiomeWeight", String.valueOf(rareBiomeWeight));
        try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
            properties.store(out, "ExtraBiomes config - biomeWeight/secondaryBiomeWeight/rareBiomeWeight control how frequently this mod's TerraBlender biome regions are picked (see ModTerrablender)");
        } catch (IOException e) {
            LOGGER.error("Failed to write {}", CONFIG_PATH, e);
        }

        Config.biomeWeight = isGametest ? GAMETEST_BIOME_WEIGHT : biomeWeight;
        Config.secondaryBiomeWeight = isGametest ? GAMETEST_BIOME_WEIGHT : secondaryBiomeWeight;
        Config.rareBiomeWeight = isGametest ? GAMETEST_BIOME_WEIGHT : rareBiomeWeight;

        Config.load();
    }

    private static int readInt(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid value for {} ({}), using default {}", key, value, defaultValue);
            return defaultValue;
        }
    }
}
