package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Bedrock's "extrabiomes:the_netherlands/netherlands_cave_carver_feature" ({@code minecraft:cave_carver_feature},
 * from "ExtraBiomes - Bedrock/packs/BP/features/the_netherlands/netherlands_cave_carver_feature.json") plus its
 * "extrabiomes:the_netherlands_cave_carver_feature" feature_rules (placement_pass "pregeneration_pass", biome_tag
 * "the_netherlands").
 * <p>
 * This is a world CARVER, not a Feature/PlacedFeature - registered under {@link Registries#CONFIGURED_CARVER} and
 * must be wired into biomes via {@code biomeBuilder.addCarver(GenerationStep.Carving.AIR, ...)}, NOT
 * {@code addFeature(...)} (flagged in wiringInstructions since ModWorldGenProvider's RegistrySetBuilder needs an
 * extra {@code .add(Registries.CONFIGURED_CARVER, NetherlandsCaveCarver::bootstrapCarver)} entry).
 * <p>
 * Field mapping from the Bedrock JSON to {@link CaveCarverConfiguration} (constructor
 * {@code (float probability, HeightProvider y, FloatProvider yScale, VerticalAnchor lavaLevel,
 * HolderSet<Block> replaceable, FloatProvider horizontalRadiusMultiplier, FloatProvider verticalRadiusMultiplier,
 * FloatProvider floorLevel)} - the debug-settings overload defaults to {@code CarverDebugSettings.DEFAULT}):
 * <ul>
 *   <li>{@code skip_carve_chance: 15} - Bedrock semantics is "1-in-15 chance the carver actually RUNS" (same value
 *       used by vanilla Bedrock's own overworld cave carvers), i.e. Java's {@code probability} is 1/15 &#8776; 0.0667F,
 *       not its complement. Using the complement (14/15) caused the carver to run on ~93% of positions instead of
 *       ~6.7%, hollowing out nearly the entire underground area under this biome.</li>
 *   <li>{@code y_scale: [0.5, 0.5]} -&gt; {@code ConstantFloat.of(0.5F)}.</li>
 *   <li>{@code height_limit: 120} -&gt; the carve height range's upper bound: {@code UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(120))}.
 *       SIMPLIFICATION: Bedrock gives only a single upper limit with no lower bound, so {@code VerticalAnchor.absolute(0)}
 *       is assumed as the floor (this biome's terrain does not read as needing sub-zero caves).</li>
 *   <li>{@code horizontal_radius_multiplier: [1, 5]} / {@code vertical_radius_multiplier: [1, 5]} -&gt;
 *       {@code UniformFloat.of(1F, 5F)} for both.</li>
 *   <li>{@code floor_level: [-0.7, -0.7]} -&gt; {@code ConstantFloat.of(-0.7F)}.</li>
 *   <li>{@code fill_with: "minecraft:air"} and {@code width_modifier: 0} have no direct Java
 *       {@link CaveCarverConfiguration} equivalent (air is already the vanilla cave carver's default carve
 *       material, and there is no standalone "width_modifier" field on this configuration) - not modelled,
 *       matching this project's established simplification convention for fields without a Java analogue.</li>
 *   <li>{@code lava_level} - absent from the Bedrock JSON entirely -&gt; kept at vanilla's usual
 *       {@code VerticalAnchor.absolute(-54)} default (this biome's lava-lake behaviour below caves is unspecified
 *       by Bedrock, so vanilla's own default is the closest faithful choice).</li>
 *   <li>{@code replaceable} - absent from the Bedrock JSON (Bedrock carvers always carve through whatever the
 *       biome's terrain block is) -&gt; {@code BlockTags.OVERWORLD_CARVER_REPLACEABLES}, vanilla's standard "what
 *       counts as carvable stone/dirt/etc." tag, resolved via {@code context.lookup(Registries.BLOCK)}.</li>
 * </ul>
 */
public class NetherlandsCaveCarver {
    public static final ResourceKey<ConfiguredWorldCarver<?>> NETHERLANDS_CAVE_KEY =
            ResourceKey.create(Registries.CONFIGURED_CARVER, new ResourceLocation(ExtraBiomes.MOD_ID, "netherlands_cave"));

    public static void bootstrapCarver(BootstapContext<ConfiguredWorldCarver<?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        HolderSet<Block> replaceable = blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES);
        context.register(NETHERLANDS_CAVE_KEY, new ConfiguredWorldCarver<>(WorldCarver.CAVE, new CaveCarverConfiguration(
                1F / 15F,
                UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(120)),
                ConstantFloat.of(0.5F),
                VerticalAnchor.absolute(-54),
                replaceable,
                UniformFloat.of(1.0F, 5.0F),
                UniformFloat.of(1.0F, 5.0F),
                ConstantFloat.of(-0.7F)
        )));
    }
}
