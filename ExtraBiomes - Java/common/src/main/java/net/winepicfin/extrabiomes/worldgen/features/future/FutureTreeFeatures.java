package net.winepicfin.extrabiomes.worldgen.features.future;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;

/**
 * Ports Bedrock's two "future" tagged tree structure features - only ever placed by the
 * FutureDesert biome (Bedrock BP feature_rules gate on {@code has_biome_tag "future"}).
 * <p>
 * Bedrock source:
 * <pre>
 * features/tree/future_tree_feature_2.json / future_tree_feature_3.json:
 *   minecraft:structure_template_feature
 *   structure_name: "extrabiomes:2_tall_white_tree" / "extrabiomes:3_tall_white_tree"
 *   constraints: { unburied: {}, block_intersection.block_allowlist: [minecraft:air], grounded: {} }
 *
 * feature_rules/tree/future_tree_feature_2.json / future_tree_feature_3.json:
 *   places_feature: "extrabiomes:tree/future_tree_feature_2" / "_3"
 *   conditions.minecraft:biome_filter: has_biome_tag "future"
 *   distribution: iterations 1, scatter_chance 10, x/z uniform [0,16],
 *                 y = query.heightmap(worldx, worldz) (i.e. directly on the surface, no offset)
 * </pre>
 * Despite the name "tree", both converted structures ({@code dump_structure.py}) turn out to be
 * small hand-built shapes made purely of {@code minecraft:azalea_leaves} + a single
 * {@code minecraft:end_rod} (no log/trunk block at all) - 2 blocks tall and 3 blocks tall
 * respectively. That is exactly what is ported here; nothing was substituted or guessed.
 * <p>
 * Mapping notes:
 * <ul>
 *   <li>{@code structure_name} -> the converted .nbt at
 *       data/extrabiomes/structures/future/2_tall_white_tree.nbt and .../3_tall_white_tree.nbt,
 *       reusing the shared {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature}
 *       / {@link SingleStructureConfiguration} infrastructure (this is a raw
 *       {@code minecraft:structure_template_feature}, not a proceduraly-grown tree, so
 *       {@code Feature.TREE}/{@code TreeConfiguration} do not apply here - there is no
 *       trunk/foliage placer shape to reproduce).</li>
 *   <li>No {@code facing_direction} is specified on either Bedrock feature -> random rotation
 *       ({@link SingleStructureConfiguration}'s default, {@code Optional.empty()}).</li>
 *   <li>{@code y = heightmap(worldx, worldz)} with no "-N" offset in Bedrock, but a small
 *       {@code GROUND_OFFSET} is applied here anyway so the base doesn't float just above the
 *       ground, plus {@link HeightmapPlacement#onHeightmap(Heightmap.Types)} on
 *       {@code OCEAN_FLOOR_WG} (ignores fluids, so this can't land on top of water) - together
 *       these also satisfy Bedrock's {@code grounded}/{@code unburied}/air-intersection
 *       constraints (placing on/into the actual ground is inherently grounded and unburied).</li>
 *   <li>{@code iterations: 1}, {@code x/z uniform [0,16]} -> {@link InSquarePlacement#spread()}
 *       (once per chunk, spread across it).</li>
 *   <li>{@code scatter_chance: 10} -> {@link RarityFilter#onAverageOnceEvery(int)} with 10, the
 *       same translation already used for this exact scatter_chance value elsewhere in this
 *       codebase (see BoulderFeatures' select_boulder / select_stick_pile).</li>
 *   <li>{@code has_biome_tag "future"} -> intentionally NOT baked in here; per project convention
 *       the biome-wiring pass adds these PlacedFeatures to FutureDesert directly via
 *       {@code biomeBuilder.addFeature(...)}, and {@link BiomeFilter#biome()} (included below) is
 *       what makes that per-biome registration take effect during generation.</li>
 * </ul>
 */
public class FutureTreeFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> FUTURE_TREE_2_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "future_tree_2"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> FUTURE_TREE_3_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "future_tree_3"));

    /** These are the keys the biome-wiring pass should addFeature(...) with. */
    public static final ResourceKey<PlacedFeature> FUTURE_TREE_2_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "future_tree_2"));
    public static final ResourceKey<PlacedFeature> FUTURE_TREE_3_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "future_tree_3"));

    // Bedrock's y = heightmap(worldx, worldz) has no "-N" offset, but placed flush on the
    // heightmap these small leaf clusters read as floating just above the ground rather than
    // growing out of it - sinking them in slightly keeps their base embedded.
    private static final int GROUND_OFFSET = -1;

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(FUTURE_TREE_2_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "future/2_tall_white_tree"), GROUND_OFFSET)
        ));
        context.register(FUTURE_TREE_3_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "future/3_tall_white_tree"), GROUND_OFFSET)
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(FUTURE_TREE_2_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(FUTURE_TREE_2_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                        BiomeFilter.biome()
                )
        ));
        context.register(FUTURE_TREE_3_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(FUTURE_TREE_3_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                        BiomeFilter.biome()
                )
        ));
    }
}
