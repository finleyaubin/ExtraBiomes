package net.winepicfin.extrabiomes.commondatagen;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TexturePathsTest {
    private static final Path TEXTURES = Path.of("src/main/resources/assets/extrabiomes/textures");

    @Test
    void everyBlockTextureLivesWhereTexturePathsSaysItDoes() throws IOException {
        assertLayoutMatches("block", null, TexturePaths::block);
    }

    @Test
    void everyItemTextureLivesWhereTexturePathsSaysItDoes() throws IOException {
        assertLayoutMatches("item", "item/armour", TexturePaths::item);
    }

    private static void assertLayoutMatches(String dir, String skippedSubdir, Function<String, String> resolver) throws IOException {
        List<String> misplaced = new ArrayList<>();
        try (Stream<Path> files = Files.walk(TEXTURES.resolve(dir))) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().contains(".png"))
                    .filter(p -> !p.getFileName().toString().endsWith("~"))
                    .filter(p -> skippedSubdir == null || !TEXTURES.relativize(p).startsWith(skippedSubdir))
                    .forEach(p -> {
                        String name = p.getFileName().toString();
                        String stem = name.substring(0, name.indexOf(".png"));
                        String expected = resolver.apply(stem) + name.substring(stem.length());
                        String actual = TEXTURES.relativize(p).toString().replace('\\', '/');
                        if (!actual.equals(expected)) misplaced.add(actual + " should be " + expected);
                    });
        }
        assertTrue(misplaced.isEmpty(), () -> "Textures not where TexturePaths expects:\n" + String.join("\n", misplaced));
    }
}
