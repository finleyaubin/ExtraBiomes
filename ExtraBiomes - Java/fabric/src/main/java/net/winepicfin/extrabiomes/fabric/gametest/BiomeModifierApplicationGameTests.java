package net.winepicfin.extrabiomes.fabric.gametest;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;

// Fabric equivalent of forge/gametest/BiomeModifierApplicationGameTests.java - see that class for
// the rationale (confirming FabricBiomeModifiers.register()'s BiomeModifications calls actually
// land on the live biome, not just that the code that would apply them compiles and runs).
//
// Unlike this package's BiomeGenerationGameTests (which needs to build a real NoiseBasedChunkGenerator
// + initialize TerraBlender because it cares about *where in the world* a biome generates), this
// only cares about a biome's own registered content, which Fabric API's BiomeModifications already
// bakes into the registry itself during the datapack reload that happens before any level exists -
// so the plain GameTestServer level's own registry access (helper.getLevel().registryAccess()) is
// sufficient here, same as Forge's version, with no TerraBlender/chunk-generator setup needed.
public class BiomeModifierApplicationGameTests {

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void jungleGetsUndergroundJungleFeaturesAndSpawns(GameTestHelper helper) {
        Biome jungle = biome(helper, Biomes.JUNGLE);

        assertHasFeature(helper, jungle, GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_PLACED_KEY);
        assertHasFeature(helper, jungle, GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_UPPER_PLACED_KEY);
        assertHasFeature(helper, jungle, GenerationStep.Decoration.UNDERGROUND_DECORATION, UndergroundJungleFeatures.CAVE_VINE_PLACED_KEY);

        assertHasSpawn(helper, jungle, MobCategory.MONSTER, ModEntities.GIANT_TORTOISE.get());
        assertHasSpawn(helper, jungle, MobCategory.WATER_CREATURE, ModEntities.PIRANHA.get());
        assertHasSpawn(helper, jungle, MobCategory.CREATURE, ModEntities.TREEFROG.get());

        helper.succeed();
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void mushroomFieldsGetsHugeMushroomsAndSpawns(GameTestHelper helper) {
        Biome mushroomFields = biome(helper, Biomes.MUSHROOM_FIELDS);

        assertHasFeature(helper, mushroomFields, GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY);
        assertHasFeature(helper, mushroomFields, GenerationStep.Decoration.LOCAL_MODIFICATIONS, MushroomFeatures.MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY);
        assertHasSpawn(helper, mushroomFields, MobCategory.CREATURE, ModEntities.HOPPLESHROOM.get());

        helper.succeed();
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void darkForestGetsHugeMushrooms(GameTestHelper helper) {
        Biome darkForest = biome(helper, Biomes.DARK_FOREST);

        assertHasFeature(helper, darkForest, GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

        helper.succeed();
    }

    private static Biome biome(GameTestHelper helper, ResourceKey<Biome> key) {
        ServerLevel level = helper.getLevel();
        return level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(key).value();
    }

    private static void assertHasFeature(GameTestHelper helper, Biome biome, GenerationStep.Decoration step, ResourceKey<PlacedFeature> expected) {
        var stepLists = biome.getGenerationSettings().features();
        boolean present = false;
        if (step.ordinal() < stepLists.size()) {
            for (Holder<PlacedFeature> holder : stepLists.get(step.ordinal())) {
                if (holder.unwrapKey().map(k -> k.equals(expected)).orElse(false)) {
                    present = true;
                    break;
                }
            }
        }
        helper.assertTrue(present, "Expected " + expected.location() + " in " + step + " but it was missing");
    }

    private static void assertHasSpawn(GameTestHelper helper, Biome biome, MobCategory category, EntityType<?> expected) {
        boolean present = biome.getMobSettings().getMobs(category).unwrap().stream()
                .anyMatch(spawnerData -> spawnerData.type == expected);
        helper.assertTrue(present, "Expected " + category + " spawn of " + expected + " but it was missing");
    }
}
