package net.winepicfin.extrabiomes.gametest;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.gametest.GameTestHooks;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.List;
import java.util.function.Consumer;

public final class ModGameTests {
    private static final DeferredRegister<Consumer<GameTestHelper>> FUNCTIONS =
            DeferredRegister.create(Registries.TEST_FUNCTION, ExtraBiomes.MOD_ID);
    private static final int DEFAULT_MAX_TICKS = 100;
    private static final int LONG_MAX_TICKS = 60_000;

    private record Test(Class<?> owner, String method, Consumer<GameTestHelper> body, String template, int maxTicks, boolean manualOnly) {
        Test(Class<?> owner, String method, Consumer<GameTestHelper> body) {
            this(owner, method, body, "empty", DEFAULT_MAX_TICKS, false);
        }

        ResourceLocation id() {
            return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, (owner.getSimpleName() + "." + method).toLowerCase());
        }
    }

    private static final List<Test> TESTS = List.of(
            new Test(BiomeGenerationGameTests.class, "allModBiomesAppearInOverworldGeneration",
                    BiomeGenerationGameTests::allModBiomesAppearInOverworldGeneration, "empty", LONG_MAX_TICKS, false),
            new Test(StructureGenerationGameTests.class, "skyCityAppearsInOverworldGeneration",
                    StructureGenerationGameTests::skyCityAppearsInOverworldGeneration, "empty", LONG_MAX_TICKS, false),
            new Test(SkyCityStructureEditGameTests.class, "layoutSkyCityBuildingsForEditing",
                    SkyCityStructureEditGameTests::layoutSkyCityBuildingsForEditing, "sky_city_edit_void", LONG_MAX_TICKS, true),
            new Test(BlockGridGameTests.class, "layoutEveryBlockStateForInspection",
                    BlockGridGameTests::layoutEveryBlockStateForInspection, "block_grid_void", LONG_MAX_TICKS, true),
            new Test(SpawnEggItemGameTests.class, "everySpawnEggResolvesRequiredFeaturesWithoutThrowing",
                    SpawnEggItemGameTests::everySpawnEggResolvesRequiredFeaturesWithoutThrowing),
            new Test(BiomeModifierApplicationGameTests.class, "jungleGetsUndergroundJungleFeaturesAndSpawns",
                    BiomeModifierApplicationGameTests::jungleGetsUndergroundJungleFeaturesAndSpawns),
            new Test(BiomeModifierApplicationGameTests.class, "mushroomFieldsGetsHugeMushroomsAndSpawns",
                    BiomeModifierApplicationGameTests::mushroomFieldsGetsHugeMushroomsAndSpawns),
            new Test(BiomeModifierApplicationGameTests.class, "darkForestGetsHugeMushrooms",
                    BiomeModifierApplicationGameTests::darkForestGetsHugeMushrooms),
            new Test(BiomeModifierApplicationGameTests.class, "plainsGetsHarpySpawn",
                    BiomeModifierApplicationGameTests::plainsGetsHarpySpawn));

    private ModGameTests() {
    }

    public static void register(IEventBus modEventBus) {
        if (!GameTestHooks.isGametestEnabled()) {
            return;
        }
        for (Test test : TESTS) {
            FUNCTIONS.register(test.id().getPath(), () -> test.body());
        }
        FUNCTIONS.register(modEventBus);
        modEventBus.addListener(ModGameTests::registerInstances);
    }

    private static void registerInstances(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition> environment;
        try {
            environment = event.registerEnvironment(
                    ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "default"),
                    new TestEnvironmentDefinition.AllOf(List.of()));
        } catch (IllegalStateException registryFrozen) {
            // A dedicated server re-fires this event from handleServerStarting after RegistryDataLoader already fired it and froze the registries
            return;
        }
        for (Test test : TESTS) {
            var data = new TestData<>(environment, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, test.template()),
                    test.maxTicks(), 0, true, Rotation.NONE, test.manualOnly(), 1, 1, false);
            event.registerTest(test.id(), new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, test.id()), data));
        }
    }
}
