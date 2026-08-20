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

    public static void load() {
        Properties properties = new Properties();
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
                properties.load(in);
            } catch (IOException e) {
                LOGGER.error("Failed to read {}, using defaults", CONFIG_PATH, e);
            }
        }

        Config.biomeWeight = readInt(properties, "biomeWeight", Config.DEFAULT_BIOME_WEIGHT);
        Config.rareBiomeWeight = readInt(properties, "rareBiomeWeight", Config.DEFAULT_RARE_BIOME_WEIGHT);

        properties.setProperty("biomeWeight", String.valueOf(Config.biomeWeight));
        properties.setProperty("rareBiomeWeight", String.valueOf(Config.rareBiomeWeight));
        try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
            properties.store(out, "ExtraBiomes config - biomeWeight/rareBiomeWeight control how frequently this mod's TerraBlender biome regions are picked (see ModTerrablender)");
        } catch (IOException e) {
            LOGGER.error("Failed to write {}", CONFIG_PATH, e);
        }

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
