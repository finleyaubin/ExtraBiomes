package net.winepicfin.extrabiomes.gametest;

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
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;

// ModBiomeModifiers.bootstrap() only *declares* which vanilla biomes should gain this mod's
// features/spawns - none of the existing tests actually confirm Forge's BiomeModifier registry
// applied that at runtime. Datagen (BiomeRegistrationParityTest et al.) proves the modifier JSON
// itself was written correctly; it says nothing about whether Forge's ModifiableBiomeInfo
// actually folded that JSON's contents into the live biome served up by a running server - the
// two are wired together by the game's own biome-loading pipeline, not by any code in this mod,
// so nothing else here would have caught a break there. This was a real, previously-undetected
// blind spot: a session diagnosed it manually (temporary logging dumping a live biome's generation
// settings) before this test formalized that check permanently.
//
// Unlike BiomeGenerationGameTests (which asks "does this biome generate somewhere in the world"),
// this asks "does this *specific* vanilla biome carry the *specific* features/spawns this mod
// added to it" - so it inspects helper.getLevel()'s live registered Biome objects directly rather
// than searching generated chunks. Reuses the same shared 1x1x1 empty structure template as the
// other gametests in this package.
@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class BiomeModifierApplicationGameTests {

    @GameTest(template = "empty")
    public static void jungleGetsUndergroundJungleFeaturesAndSpawns(GameTestHelper helper) {
        Biome jungle = biome(helper, Biomes.JUNGLE);

        assertHasFeature(helper, jungle, GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_PLACED_KEY);
        assertHasFeature(helper, jungle, GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_UPPER_PLACED_KEY);
        assertHasFeature(helper, jungle, GenerationStep.Decoration.UNDERGROUND_DECORATION, UndergroundJungleFeatures.CAVE_VINE_PLACED_KEY);

        assertHasSpawn(helper, jungle, MobCategory.CREATURE, ModEntities.GIANT_TORTOISE.get());
        assertHasSpawn(helper, jungle, MobCategory.WATER_CREATURE, ModEntities.PIRANHA.get());
        assertHasSpawn(helper, jungle, MobCategory.CREATURE, ModEntities.TREEFROG.get());

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void mushroomFieldsGetsHugeMushroomsAndSpawns(GameTestHelper helper) {
        Biome mushroomFields = biome(helper, Biomes.MUSHROOM_FIELDS);

        assertHasFeature(helper, mushroomFields, GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY);
        assertHasFeature(helper, mushroomFields, GenerationStep.Decoration.LOCAL_MODIFICATIONS, MushroomFeatures.MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY);
        assertHasSpawn(helper, mushroomFields, MobCategory.CREATURE, ModEntities.HOPPLESHROOM.get());

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void darkForestGetsHugeMushrooms(GameTestHelper helper) {
        Biome darkForest = biome(helper, Biomes.DARK_FOREST);

        assertHasFeature(helper, darkForest, GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

        helper.succeed();
    }

    private static Biome biome(GameTestHelper helper, ResourceKey<Biome> key) {
        ServerLevel level = helper.getLevel();
        return level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(key).value();
    }

    // BiomeGenerationSettings only exposes the full per-step feature list (features()), not a
    // single-step lookup - Forge's own BiomeGenerationSettingsBuilder indexes into it the same way
    // (see net.minecraftforge.common.world.BiomeGenerationSettingsBuilder#getFeatures).
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
