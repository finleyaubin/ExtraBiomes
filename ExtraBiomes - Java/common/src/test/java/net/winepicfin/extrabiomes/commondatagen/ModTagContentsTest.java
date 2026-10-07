package net.winepicfin.extrabiomes.commondatagen;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Guards the shared vanilla/convention tags (wooden_doors, saplings, stripped_logs, ...) against the
// "added a block but forgot to tag it" bug that left palm logs out of #logs. It reads source files
// rather than the registries, so it runs unchanged on every version branch - re-run it after every
// backport. Cwd is the common/ module directory.
class ModTagContentsTest {

    private static final Path JAVA_ROOT = Paths.get("..");
    private static final Path MOD_BLOCKS = Paths.get("src/main/java/net/winepicfin/extrabiomes/block/ModBlocks.java");
    private static final Path TAG_CONTENTS = Paths.get("src/main/java/net/winepicfin/extrabiomes/commondatagen/ModTagContents.java");

    private static String read(Path path) throws IOException {
        return Files.readString(path);
    }

    private static List<String> matches(String source, String regex) {
        List<String> found = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(source);
        while (m.find()) {
            found.add(m.group(1));
        }
        return found;
    }

    /** Wood prefixes ("mystic", "gilded_sky", ...) come from the registerStandardWoodSet calls, so a new wood type is picked up automatically. */
    private static List<String> woodPrefixes() throws IOException {
        return matches(read(MOD_BLOCKS), "registerStandardWoodSet\\(\"([a-z_]+)\"");
    }

    private static void assertMentioned(String tagContents, String listName, String constant) {
        // Restrict to the declaration of that list so a constant mentioned in another list can't satisfy it.
        int start = tagContents.indexOf(" " + listName + " =");
        assertTrue(start >= 0, "ModTagContents has no list " + listName);
        int end = tagContents.indexOf(");", start);
        assertTrue(tagContents.substring(start, end).contains(constant + ","), listName + " is missing " + constant
                + " (or it is not followed by a comma)");
    }

    @Test
    void everyWoodSetPieceIsInItsTag() throws IOException {
        String contents = read(TAG_CONTENTS).replace(");", ",);");
        List<String> prefixes = woodPrefixes();
        assertTrue(prefixes.size() >= 4, "expected at least the 4 known wood sets, found " + prefixes);
        for (String prefix : prefixes) {
            String p = prefix.toUpperCase();
            assertMentioned(contents, "WOODEN_STAIRS", "ModBlocks." + p + "_STAIRS");
            assertMentioned(contents, "WOODEN_SLABS", "ModBlocks." + p + "_SLAB");
            assertMentioned(contents, "WOODEN_BUTTONS", "ModBlocks." + p + "_BUTTON");
            assertMentioned(contents, "WOODEN_PRESSURE_PLATES", "ModBlocks." + p + "_PRESSURE_PLATE");
            assertMentioned(contents, "WOODEN_DOORS", "ModBlocks." + p + "_DOOR");
            assertMentioned(contents, "WOODEN_TRAPDOORS", "ModBlocks." + p + "_TRAPDOOR");
            assertMentioned(contents, "STANDING_SIGNS", "ModBlocks." + p + "_SIGN");
            assertMentioned(contents, "WALL_SIGNS", "ModBlocks." + p + "_WALL_SIGN");
            assertMentioned(contents, "CEILING_HANGING_SIGNS", "ModBlocks." + p + "_HANGING_SIGN");
            assertMentioned(contents, "WALL_HANGING_SIGNS", "ModBlocks." + p + "_WALL_HANGING_SIGN");
            assertMentioned(contents, "SIGN_ITEMS", "ModItems." + p + "_SIGN");
            assertMentioned(contents, "HANGING_SIGN_ITEMS", "ModItems." + p + "_HANGING_SIGN");
            assertMentioned(contents, "STRIPPED_LOGS", "ModBlocks.STRIPPED_" + p + "_LOG");
            assertMentioned(contents, "STRIPPED_WOODS", "ModBlocks.STRIPPED_" + p + "_WOOD");
        }
    }

    @Test
    void everySaplingAndNetherOreIsTagged() throws IOException {
        String blocks = read(MOD_BLOCKS);
        String contents = read(TAG_CONTENTS).replace(");", ",);");
        for (String sapling : matches(blocks, "registerBlock\\(\"([a-z_]+_sapling)\"")) {
            assertMentioned(contents, "SAPLINGS", "ModBlocks." + sapling.toUpperCase());
        }
        for (String ore : matches(blocks, "registerBlock\\(\"(nether_[a-z_]+_ore)\"")) {
            assertMentioned(contents, "ORES", "ModBlocks." + ore.toUpperCase());
        }
    }

    @Test
    void everyLoaderGeneratorUsesEverySharedList() throws IOException {
        String contents = read(TAG_CONTENTS);
        List<String> lists = matches(contents, "public static final List<[^>]*>+ ([A-Z_]+) =");
        assertTrue(lists.size() > 10, "failed to parse the lists out of ModTagContents: " + lists);

        List<Path> generators = new ArrayList<>();
        try (Stream<Path> files = Files.walk(JAVA_ROOT)) {
            files.filter(p -> p.getFileName().toString().matches("Mod(Block|Item)TagGenerator\\.java"))
                    .filter(p -> !p.toString().contains("build"))
                    .forEach(generators::add);
        }
        assertTrue(generators.size() >= 2, "no loader tag generators found under " + JAVA_ROOT.toAbsolutePath());

        // Every loader (neoforge/fabric/forge) has a block and an item generator; each shared list must be
        // used by at least one of that loader's two, so no loader silently skips a tag.
        for (String loader : List.of("neoforge", "fabric", "forge")) {
            List<Path> mine = generators.stream().filter(p -> p.toString().contains("/" + loader + "/")
                    || p.toString().contains("\\" + loader + "\\")).toList();
            if (mine.isEmpty()) {
                continue; // loader not present on this version branch
            }
            assertEquals(2, mine.size(), loader + " should have exactly a block and an item tag generator: " + mine);
            StringBuilder combined = new StringBuilder();
            for (Path p : mine) {
                combined.append(read(p));
            }
            for (String list : lists) {
                assertTrue(combined.toString().contains("ModTagContents." + list),
                        loader + " tag generators never use ModTagContents." + list);
            }
        }
    }
}
