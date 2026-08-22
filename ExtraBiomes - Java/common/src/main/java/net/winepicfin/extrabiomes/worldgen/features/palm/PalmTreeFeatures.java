package net.winepicfin.extrabiomes.worldgen.features.palm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.List;

/**
 * Port of Bedrock's palm tree subsystem (features/palm/select_palm_tree_feature.json, a
 * minecraft:weighted_random_feature over palm_tree_{1,2,3,4}.mcstructure - each a
 * minecraft:structure_template_feature).
 * <p>
 * WHY THIS IS PLACED AS RAW STRUCTURES RATHER THAN A PROCEDURAL {@code Feature.TREE}: the 4
 * palm_tree_*.mcstructure files (see tools/viz_tree.py / a horizontal-slice dump of them) are hand
 * authored, not a symmetric trunk+blob canopy - the trunk itself leans/kinks sideways a block or two
 * partway up (palm_tree_1's log column walks from (5,2) at the base to (3,2) by the crown), and the
 * crown is an irregular, non-radially-symmetric spray of individual frond leaves rather than a
 * filled disc (compare palm_tree_1's y=5 layer, a sparse 5-block cross offset from the trunk, to its
 * y=7 layer, a near-solid plus shape). No combination of vanilla TrunkPlacer/FoliagePlacer produces
 * that lean + irregular spray - it needs the literal block layout, which is exactly what
 * {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature}
 * (already used for stone_pillars/taigaspike/glacier/jellycoral) provides.
 * <p>
 * Each structure was converted with tools/mc2java.py into data/extrabiomes/structures/palm/
 * palm_tree_{1,2,3,4}.nbt (block ids extrabiomes:palm_log/palm_leaves both map cleanly via
 * tools/block_map.py - zero warnings from the converter). Random rotation per placement (Bedrock's
 * structure_template_feature entries specify no fixed facing_direction) actually helps here, since
 * it also randomizes which way each tree's lean points.
 * <p>
 * Weights mirror Bedrock's aggregate (which tries structures in order and keeps whichever fits,
 * naturally favoring the smaller ones) via the same sequential-trial RANDOM_SELECTOR chain used by
 * StonePillarsFeature/TaigaSpikeFeatures/BoulderFeatures/MesaFeatures/MushroomFeatures: palm_tree_4
 * (large) 10%, palm_tree_3 (wide) 20%, palm_tree_1 (medium) 45%, palm_tree_2 (small) the guaranteed
 * default (remaining 25%).
 */
public class PalmTreeFeatures {
    private static final ResourceKey<ConfiguredFeature<?, ?>> PALM_SMALL_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "palm_tree_small"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> PALM_MEDIUM_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "palm_tree_medium"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> PALM_WIDE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "palm_tree_wide"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> PALM_LARGE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "palm_tree_large"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SELECT_PALM_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, "select_palm_tree"));

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // anchor = the actual base-log column measured off each .mcstructure (tools/viz_tree.py /
        // a horizontal-slice dump) - these trunks lean, so the base log is NOT at the bounding
        // box's local (0,0,0) corner. Without this, the placement origin's heightmap/water-depth
        // check (which runs against the ORIGIN column) tests the wrong column entirely, and the
        // tree's actual trunk can end up floating above uneven ground or standing in water that
        // was never checked. Anchoring here makes the origin BE the trunk's true ground column.
        context.register(PALM_SMALL_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "palm/palm_tree_2"), new BlockPos(1, 0, 1))
        ));
        context.register(PALM_MEDIUM_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "palm/palm_tree_1"), new BlockPos(5, 0, 2))
        ));
        context.register(PALM_WIDE_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "palm/palm_tree_3"), new BlockPos(4, 0, 5))
        ));
        context.register(PALM_LARGE_KEY, new ConfiguredFeature<>(
                ModStructureScatterFeatures.SINGLE_STRUCTURE.get(),
                new SingleStructureConfiguration(new ResourceLocation(ExtraBiomes.MOD_ID, "palm/palm_tree_4"), new BlockPos(6, 0, 4))
        ));

        context.register(SELECT_PALM_KEY, new ConfiguredFeature<>(
                Feature.RANDOM_SELECTOR,
                new RandomFeatureConfiguration(
                        List.of(
                                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(PALM_LARGE_KEY)), 0.1F),
                                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(PALM_WIDE_KEY)), 2.0F / 9.0F),
                                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(PALM_MEDIUM_KEY)), 0.45F / 0.7F)
                        ),
                        PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(PALM_SMALL_KEY))
                )
        ));
    }
}
