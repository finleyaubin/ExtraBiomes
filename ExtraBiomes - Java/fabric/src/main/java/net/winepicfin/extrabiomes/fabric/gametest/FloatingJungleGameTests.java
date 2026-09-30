package net.winepicfin.extrabiomes.fabric.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.features.floatingjungle.FloatingJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FloatingJungleGameTests {
    private static final String[] ISLAND_TEMPLATES = {
            "islet_small_1", "islet_small_2", "islet_small_3", "islet_large_1", "islet_large_2",
            "archipelago_1", "archipelago_2", "archipelago_ruin"
    };
    // SingleStructureFeature only writes inside a 48-block window; wider templates would be skipped every time.
    private static final int MAX_SPAN = 44;

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void everyIslandTemplateLoadsWithinTheWriteWindow(GameTestHelper helper) {
        for (String name : ISLAND_TEMPLATES) {
            ResourceLocation id = new ResourceLocation(ExtraBiomes.MOD_ID, "floating_jungle/" + name);
            StructureTemplate template = helper.getLevel().getStructureManager().get(id).orElse(null);
            helper.assertTrue(template != null, "Missing island template " + id);
            Vec3i size = template.getSize();
            helper.assertTrue(size.getX() <= MAX_SPAN && size.getZ() <= MAX_SPAN,
                    id + " is " + size.getX() + "x" + size.getZ() + ", over the " + MAX_SPAN + "-block write window");
        }
        helper.succeed();
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void placedIslandsContainTheirLandmarks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(BlockPos.ZERO);
        BlockPos origin = new BlockPos((abs.getX() >> 4 << 4) + 8, 100, (abs.getZ() >> 4 << 4) + 8);

        Set<Block> large = placeAndCollect(level, "islet_large_1", origin);
        for (Block expected : List.of(Blocks.GRASS_BLOCK, Blocks.JUNGLE_LEAVES, Blocks.VINE, Blocks.WATER, Blocks.STONE)) {
            helper.assertTrue(large.contains(expected), "islet_large_1 is missing " + expected + ", found " + large);
        }

        Set<Block> ruin = placeAndCollect(level, "archipelago_ruin", origin.offset(0, 0, 64));
        for (Block expected : List.of(Blocks.CHEST, Blocks.MOSSY_STONE_BRICKS, Blocks.JUNGLE_LOG)) {
            helper.assertTrue(ruin.contains(expected), "archipelago_ruin is missing " + expected);
        }
        helper.succeed();
    }

    private static Set<Block> placeAndCollect(ServerLevel level, String template, BlockPos origin) {
        ResourceLocation id = new ResourceLocation(ExtraBiomes.MOD_ID, "floating_jungle/" + template);
        SingleStructureConfiguration config = new SingleStructureConfiguration(id, Optional.empty(), 0, true, 0.95F);
        boolean placed = new ConfiguredFeature<>(ModStructureScatterFeatures.SINGLE_STRUCTURE.get(), config)
                .place(level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
        if (!placed) {
            throw new AssertionError(template + " refused to place at " + origin);
        }
        Set<Block> found = new HashSet<>();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-24, 0, -24), origin.offset(24, 40, 24))) {
            found.add(level.getBlockState(pos).getBlock());
        }
        return found;
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void floatingJungleGetsItsIslands(GameTestHelper helper) {
        Biome biome = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(ModBiomes.FLOATING_JUNGLE);
        boolean present = false;
        HolderSet<PlacedFeature> surfaceStructures = biome.getGenerationSettings().features().get(GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal());
        for (Holder<PlacedFeature> holder : surfaceStructures) {
            present |= holder.unwrapKey().map(k -> k.equals(FloatingJungleFeatures.SELECT_ISLAND_PLACED_KEY)).orElse(false);
        }
        helper.assertTrue(present, "Floating Jungle is missing its island feature in SURFACE_STRUCTURES");
        helper.succeed();
    }
}
