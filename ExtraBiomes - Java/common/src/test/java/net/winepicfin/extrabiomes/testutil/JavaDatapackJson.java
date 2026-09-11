package net.winepicfin.extrabiomes.testutil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Loads a JSON file from this Gradle module's own resources (relativePath resolved against the
// module root, e.g. "src/generated/resources/data/extrabiomes/loot_tables/entities/worm.json").
// Unlike Bedrock's source files (see BedrockEntityJson), these are datagen output, so they're
// always strict JSON with no comments to strip.
public final class JavaDatapackJson {
    public static JsonObject load(String relativePath) {
        Path path = Paths.get(relativePath);
        try {
            return JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Failed to read Java datapack file: " + path + " - run `./gradlew runData` first if it's missing",
                    e);
        }
    }

    private JavaDatapackJson() {
    }
}
