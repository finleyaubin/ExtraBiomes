package net.winepicfin.extrabiomes.datapack;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.TagFile;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.storage.loot.LootTable;
import net.winepicfin.extrabiomes.testutil.DatapackRegistries;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

// Validates every authored (src/main/resources) and generated (src/generated/resources, from
// `./gradlew runData`) datapack JSON file against the real vanilla Codec for its data type - the
// same parser the actual game uses to accept or reject a datapack file, rather than a hand-rolled
// JSON Schema or an external mcdoc toolchain (see this project's session notes for why those were
// rejected: mcdoc has no stable JSON-Schema export and validating against it needs a Node
// toolchain against Spyglass's own unstable internal APIs).
//
// This is exactly the class of bug a folder-rename regression like tags/blocks -> tags/block
// would trip: vanilla silently ignores an unrecognized data folder, so nothing else in this repo
// would have caught it.
class DatapackSchemaTest {
    private static final List<Path> DATA_ROOTS = List.of(
            Paths.get("src/main/resources/data"),
            Paths.get("src/generated/resources/data"));

    @Test
    void everyDatapackJsonFileMatchesItsVanillaCodec() {
        HolderLookup.Provider registries = DatapackRegistries.get();
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);

        List<String> failures = new ArrayList<>();
        for (Path root : DATA_ROOTS) {
            if (!Files.isDirectory(root)) {
                continue;
            }
            for (Path file : listJsonFiles(root)) {
                Route route = Route.forPath(root.relativize(file));
                if (route == Route.SKIP) {
                    continue;
                }
                if (route == Route.UNKNOWN) {
                    failures.add(file + ": no Codec routed for this data folder - wire it into "
                            + "DatapackSchemaTest.Route or add it to the explicit skip list");
                    continue;
                }
                decode(file, route.codec, ops, failures);
            }
        }

        if (!failures.isEmpty()) {
            fail(failures.size() + " datapack file(s) failed Codec validation:\n"
                    + String.join("\n", failures));
        }
    }

    private static void decode(Path file, Codec<?> codec, RegistryOps<JsonElement> ops, List<String> failures) {
        JsonElement json;
        try {
            json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + file, e);
        }
        DataResult<?> result = codec.parse(ops, json);
        result.error().ifPresent(error -> failures.add(file + ": " + error.message()));
    }

    private static List<Path> listJsonFiles(Path root) {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.naturalOrder())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to walk " + root, e);
        }
    }

    // relativePath is rooted at data/, e.g. "extrabiomes/worldgen/biome/oasis.json".
    private enum Route {
        LOOT_TABLE(LootTable.DIRECT_CODEC, "loot_table"),
        STRUCTURE(Structure.DIRECT_CODEC, "worldgen", "structure"),
        STRUCTURE_SET(StructureSet.DIRECT_CODEC, "worldgen", "structure_set"),
        TEMPLATE_POOL(StructureTemplatePool.DIRECT_CODEC, "worldgen", "template_pool"),
        BIOME(Biome.DIRECT_CODEC, "worldgen", "biome"),
        CONFIGURED_FEATURE(ConfiguredFeature.DIRECT_CODEC, "worldgen", "configured_feature"),
        PLACED_FEATURE(PlacedFeature.DIRECT_CODEC, "worldgen", "placed_feature"),
        NOISE(NormalNoise.NoiseParameters.DIRECT_CODEC, "worldgen", "noise"),
        ADVANCEMENT(Advancement.CODEC, "advancement"),
        RECIPE(Recipe.CODEC, "recipe"),
        TAG(TagFile.CODEC, "tags"),
        // Sentinels, never matched against a folder prefix directly.
        SKIP(null),
        UNKNOWN(null);

        private final Codec<?> codec;
        private final String[] folderPrefix;

        Route(Codec<?> codec, String... folderPrefix) {
            this.codec = codec;
            this.folderPrefix = folderPrefix;
        }

        static Route forPath(Path relativePath) {
            // First segment is the namespace (extrabiomes, minecraft, c, neoforge, ...).
            if (relativePath.getNameCount() < 3) {
                return UNKNOWN;
            }
            // neoforge/biome_modifier/*.json needs NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS,
            // which lives in a NeoForge dependency common doesn't have on its test classpath -
            // deliberately out of scope here (see DatapackRegistries).
            if (relativePath.getName(1).toString().equals("neoforge")) {
                return SKIP;
            }
            for (Route route : values()) {
                if (route.codec == null) {
                    continue;
                }
                if (matchesFolder(relativePath, route.folderPrefix)) {
                    return route;
                }
            }
            return UNKNOWN;
        }

        private static boolean matchesFolder(Path relativePath, String[] folderPrefix) {
            // relativePath.getNameCount() includes the namespace segment (index 0) and the file
            // name itself, so the folder prefix starts at index 1.
            if (relativePath.getNameCount() < folderPrefix.length + 2) {
                return false;
            }
            for (int i = 0; i < folderPrefix.length; i++) {
                if (!relativePath.getName(i + 1).toString().equals(folderPrefix[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}
