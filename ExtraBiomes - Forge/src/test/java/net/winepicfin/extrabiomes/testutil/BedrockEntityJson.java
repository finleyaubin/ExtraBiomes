package net.winepicfin.extrabiomes.testutil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Loads a single JSON file from ExtraBiomes - Bedrock/packs/... (resolved relative to the repo
// root, one directory up from this Gradle module) so parity tests assert against the actual
// Bedrock source file instead of literals copy-pasted into the test.
//
// Bedrock's JSON files aren't always strictly valid JSON - some (e.g. blocks/stick_pile.json)
// contain "//" line comments - so those are stripped before parsing.
public final class BedrockEntityJson {
    private static final Path PACKS_DIR = Paths.get("..", "ExtraBiomes - Bedrock", "packs").normalize();

    private final JsonObject root;

    private BedrockEntityJson(JsonObject root) {
        this.root = root;
    }

    // relativePath is rooted at packs/, e.g. "BP/entities/giant_tortoise.json".
    public static BedrockEntityJson load(String relativePath) {
        Path path = PACKS_DIR.resolve(relativePath);
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            return new BedrockEntityJson(JsonParser.parseString(stripLineComments(content)).getAsJsonObject());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read Bedrock source file: " + path, e);
        }
    }

    public double getDouble(String... path) {
        return get(path).getAsDouble();
    }

    public float getFloat(String... path) {
        return get(path).getAsFloat();
    }

    public JsonElement get(String... path) {
        JsonElement current = root;
        StringBuilder trail = new StringBuilder();
        for (String key : path) {
            trail.append('/').append(key);
            if (current == null || !current.isJsonObject() || !current.getAsJsonObject().has(key)) {
                throw new IllegalArgumentException("Missing JSON path " + trail + " in Bedrock source file");
            }
            current = current.getAsJsonObject().get(key);
        }
        return current;
    }

    private static String stripLineComments(String json) {
        StringBuilder out = new StringBuilder(json.length());
        boolean inString = false;
        boolean escaped = false;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (inString) {
                out.append(c);
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
                out.append(c);
                continue;
            }
            if (c == '/' && i + 1 < json.length() && json.charAt(i + 1) == '/') {
                while (i < json.length() && json.charAt(i) != '\n') {
                    i++;
                }
                out.append('\n');
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }
}
