package net.winepicfin.extrabiomes.fabric.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.features.floatingjungle.FloatingJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FloatingJungleGameTests {
    private static final String[] ISLAND_TEMPLATES = {
            "islet_small_1", "islet_small_2", "islet_small_3", "islet_large_1", "islet_large_2",
            "archipelago_1", "archipelago_2", "archipelago_ruin", "islet_temple"
    };
    // SingleStructureFeature only writes inside a 48-block window; wider templates would be skipped every time.
    private static final int MAX_SPAN = 44;

    @GameTest(structure = ExtraBiomes.MOD_ID + ":empty")
    public void everyIslandTemplateLoadsWithinTheWriteWindow(GameTestHelper helper) {
        for (String name : ISLAND_TEMPLATES) {
            Identifier id = Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "floating_jungle/" + name);
            StructureTemplate template = helper.getLevel().getStructureTemplateManager().get(id).orElse(null);
            helper.assertTrue(template != null, Component.literal("Missing island template " + id));
            Vec3i size = template.getSize();
            helper.assertTrue(size.getX() <= MAX_SPAN && size.getZ() <= MAX_SPAN,
                    Component.literal(id + " is " + size.getX() + "x" + size.getZ() + ", over the " + MAX_SPAN + "-block write window"));
        }
        helper.succeed();
    }

    @GameTest(structure = ExtraBiomes.MOD_ID + ":empty")
    public void placedIslandsContainTheirLandmarks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(BlockPos.ZERO);
        BlockPos origin = new BlockPos((abs.getX() >> 4 << 4) + 8, 100, (abs.getZ() >> 4 << 4) + 8);

        Set<Block> large = placeAndCollect(level, "islet_large_1", origin);
        for (Block expected : List.of(Blocks.GRASS_BLOCK, Blocks.JUNGLE_LEAVES, Blocks.VINE, Blocks.WATER, Blocks.STONE)) {
            helper.assertTrue(large.contains(expected), Component.literal("islet_large_1 is missing " + expected + ", found " + large));
        }

        Set<Block> ruin = placeAndCollect(level, "archipelago_ruin", origin.offset(0, 0, 64));
        for (Block expected : List.of(Blocks.CHEST, Blocks.MOSSY_STONE_BRICKS, Blocks.JUNGLE_LOG)) {
            helper.assertTrue(ruin.contains(expected), Component.literal("archipelago_ruin is missing " + expected));
        }

        Set<Block> temple = placeAndCollect(level, "islet_temple", origin.offset(0, 0, 128));
        for (Block expected : List.of(Blocks.GRASS_BLOCK, Blocks.MOSSY_COBBLESTONE, Blocks.CHEST, Blocks.DISPENSER,
                Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.STICKY_PISTON, Blocks.REDSTONE_WIRE, Blocks.LEVER)) {
            helper.assertTrue(temple.contains(expected), Component.literal("islet_temple is missing " + expected));
        }
        helper.succeed();
    }

    private static Set<Block> placeAndCollect(ServerLevel level, String template, BlockPos origin) {
        Identifier id = Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "floating_jungle/" + template);
        boolean placed = new SingleStructureFeature(id, Optional.empty(), 0, true, 0.95F)
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

    @GameTest(structure = ExtraBiomes.MOD_ID + ":empty")
    public void floatingJungleGetsItsIslands(GameTestHelper helper) {
        Biome biome = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(ModBiomes.FLOATING_JUNGLE).value();
        boolean present = false;
        for (Holder<PlacedFeature> holder : biome.getGenerationSettings().features().get(GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal())) {
            present |= holder.unwrapKey().map(k -> k.equals(FloatingJungleFeatures.SELECT_ISLAND_PLACED_KEY)).orElse(false);
        }
        helper.assertTrue(present, Component.literal("Floating Jungle is missing its island feature in SURFACE_STRUCTURES"));
        helper.succeed();
    }
}
