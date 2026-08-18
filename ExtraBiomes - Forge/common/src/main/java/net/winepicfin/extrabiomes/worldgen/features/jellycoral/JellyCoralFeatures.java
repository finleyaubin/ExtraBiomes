package net.winepicfin.extrabiomes.worldgen.features.jellycoral;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;

/**
 * Ports Bedrock's "extrabiomes:jellycoral/jellycoral_{1,2,3,4}" ({@code minecraft:structure_template_feature})
 * plus their feature_rules distributions ("extrabiomes:jellycoral_{1,2,3,4}"), each reusing the shared
 * {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature} infra.
 * <p>
 * Bedrock source (identical shape for all four, only {@code structure_name}/identifier differ):
 * <pre>
 * features/jellycoral/jellycoral_N.json:
 *   structure_name: "extrabiomes:jellycoral_N"
 *   constraints: { block_intersection.block_allowlist: [minecraft:water, minecraft:seagrass, minecraft:kelp], grounded: {} }
 * feature_rules/jellycoral/jellycoral_N.json:
 *   distribution: iterations 20, scatter_chance 100.0, x/z uniform [0,16],
 *                 y uniform [0, query.heightmap(worldx,worldz) * 2]
 *   conditions: biome_filter has_biome_tag "jellyfish"
 * </pre>
 * Mapping notes:
 * <ul>
 *   <li>No {@code facing_direction} is specified in Bedrock, so rotation is left random -
 *       {@link SingleStructureConfiguration}'s {@code (structure, groundOffset)} constructor is used,
 *       which implies {@code Optional.empty()} rotation (uniformly random per placement).</li>
 *   <li>{@code iterations: 20} + {@code scatter_chance: 100.0} (always run, up to 20 scatter attempts
 *       per chunk) -> {@link CountPlacement#of(int)} with 20.</li>
 *   <li>{@code x/z uniform [0,16]} -> {@link InSquarePlacement#spread()}.</li>
 *   <li>The Bedrock {@code y} expression ({@code uniform [0, heightmap*2]}) is effectively overridden by
 *       the {@code grounded} constraint, which snaps the placement down onto solid ground/seafloor
 *       regardless of the sampled y - so this is expressed here as
 *       {@link HeightmapPlacement#onHeightmap} on {@code OCEAN_FLOOR_WG} (the Java equivalent of "find the
 *       seafloor surface"), with {@code groundOffset = 0} (no extra embed depth was implied by Bedrock).</li>
 *   <li>{@code constraints.grounded} + {@code block_intersection.block_allowlist} (structure must sit in/on
 *       water, seagrass or kelp) -> a {@link BlockPredicateFilter} testing the placement position itself
 *       (the block the structure's origin intersects, sitting on top of the ocean floor) against
 *       {@link BlockPredicate#matchesBlocks} for water/seagrass/kelp, exactly mirroring how
 *       {@code OasisPuddleFeature} expresses Bedrock's "constraints" block as a PlacementModifier.</li>
 *   <li>{@code has_biome_tag "jellyfish"} -> intentionally NOT included here; per project convention the
 *       biome wiring pass adds each PlacedFeature to the JellyfishFields biome directly via
 *       biomeBuilder.addFeature(...), and {@link BiomeFilter#biome()} (last modifier below) is what makes
 *       that per-biome registration actually take effect during generation.</li>
 *   <li>Simplification: the converted .nbt structures reference several Bedrock block ids that
 *       tools/mc2java.py does not yet map (minecraft:coral_block, minecraft:coral_fan,
 *       minecraft:coral_fan_hang, minecraft:kelp, minecraft:sea_pickle - see the conversion warnings
 *       printed for each jellycoral_N.nbt) - these will come through as air in the converted structures
 *       until that mapping table is extended; not something patched as part of this change.</li>
 * </ul>
 */
public class JellyCoralFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> JELLYCORAL_1_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_1"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> JELLYCORAL_2_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_2"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> JELLYCORAL_3_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_3"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> JELLYCORAL_4_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_4"));

    public static final ResourceKey<PlacedFeature> JELLYCORAL_1_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_1"));
    public static final ResourceKey<PlacedFeature> JELLYCORAL_2_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_2"));
    public static final ResourceKey<PlacedFeature> JELLYCORAL_3_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_3"));
    public static final ResourceKey<PlacedFeature> JELLYCORAL_4_PLACED_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral_4"));

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        registerConfigured(context, JELLYCORAL_1_KEY, "jellycoral_1");
        registerConfigured(context, JELLYCORAL_2_KEY, "jellycoral_2");
        registerConfigured(context, JELLYCORAL_3_KEY, "jellycoral_3");
        registerConfigured(context, JELLYCORAL_4_KEY, "jellycoral_4");
    }

    private static void registerConfigured(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, String structureName) {
        context.register(key, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "jellycoral/" + structureName), 0)
        ));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        registerPlaced(context, configuredFeatures, JELLYCORAL_1_PLACED_KEY, JELLYCORAL_1_KEY);
        registerPlaced(context, configuredFeatures, JELLYCORAL_2_PLACED_KEY, JELLYCORAL_2_KEY);
        registerPlaced(context, configuredFeatures, JELLYCORAL_3_PLACED_KEY, JELLYCORAL_3_KEY);
        registerPlaced(context, configuredFeatures, JELLYCORAL_4_PLACED_KEY, JELLYCORAL_4_KEY);
    }

    private static void registerPlaced(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                        ResourceKey<PlacedFeature> placedKey, ResourceKey<ConfiguredFeature<?, ?>> configuredKey) {
        context.register(placedKey, new PlacedFeature(
                configuredFeatures.getOrThrow(configuredKey),
                List.of(
                        CountPlacement.of(20),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new BlockPos(0, 0, 0), Blocks.WATER, Blocks.SEAGRASS, Blocks.KELP)),
                        BiomeFilter.biome()
                )
        ));
    }
}
