package net.winepicfin.extrabiomes.fabric.util;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.winepicfin.extrabiomes.block.ModBlocks;

// Fabric equivalent of forge/.../util/ModVanillaCompat.java + the axe-stripping half of
// forge/.../block/custom/ModLogs.java's getToolModifiedState override. Neither needs a custom
// Block subclass on Fabric: FlammableBlockRegistry/StrippableBlockRegistry cover exactly what
// those Forge-only per-block method overrides did (and the overrides were already redundant with
// this same flammability data on Forge - see forge/.../ModVanillaCompat's STICK_PILE entry, which
// registers the same (50, 50) values StickPileBlock's overrides also hardcoded). Fabric's
// log/stick-pile blocks are therefore plain vanilla RotatedPillarBlock (see
// platform/fabric/ExtraBiomesExpectPlatformImpl).
public class FabricVanillaCompat {
    public static void register() {
        registerFlammable(ModBlocks.STICK_PILE.get(), 50, 50);
        // Mystic Wood
        registerFlammable(ModBlocks.MYSTIC_PLANKS.get(), 5, 5);
        registerFlammable(ModBlocks.MYSTIC_LOG.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_WOOD.get(), 5, 20);
        registerFlammable(ModBlocks.STRIPPED_MYSTIC_LOG.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_LEAVES.get(), 30, 60);
        registerFlammable(ModBlocks.STRIPPED_PALM_WOOD.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_SAPLING.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_STAIRS.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_SLAB.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_BUTTON.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_PRESSURE_PLATE.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_FENCE.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_FENCE_GATE.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_DOOR.get(), 5, 20);
        registerFlammable(ModBlocks.MYSTIC_TRAPDOOR.get(), 5, 20);
        // Palm Wood
        registerFlammable(ModBlocks.PALM_PLANKS.get(), 5, 5);
        registerFlammable(ModBlocks.PALM_LOG.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_WOOD.get(), 5, 20);
        registerFlammable(ModBlocks.STRIPPED_PALM_LOG.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_LEAVES.get(), 30, 60);
        registerFlammable(ModBlocks.STRIPPED_PALM_WOOD.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_SAPLING.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_STAIRS.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_SLAB.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_BUTTON.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_PRESSURE_PLATE.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_FENCE.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_FENCE_GATE.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_DOOR.get(), 5, 20);
        registerFlammable(ModBlocks.PALM_TRAPDOOR.get(), 5, 20);
        // Sky Wood
        registerFlammable(ModBlocks.SKY_PLANKS.get(), 5, 5);
        registerFlammable(ModBlocks.SKY_LOG.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_WOOD.get(), 5, 20);
        registerFlammable(ModBlocks.STRIPPED_SKY_LOG.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_LEAVES.get(), 30, 60);
        registerFlammable(ModBlocks.STRIPPED_PALM_WOOD.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_SAPLING.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_STAIRS.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_SLAB.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_BUTTON.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_PRESSURE_PLATE.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_FENCE.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_FENCE_GATE.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_DOOR.get(), 5, 20);
        registerFlammable(ModBlocks.SKY_TRAPDOOR.get(), 5, 20);
        // Gilded Sky Wood
        registerFlammable(ModBlocks.GILDED_SKY_PLANKS.get(), 5, 5);
        registerFlammable(ModBlocks.GILDED_SKY_LOG.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_WOOD.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_STAIRS.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_SLAB.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_BUTTON.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_PRESSURE_PLATE.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_FENCE.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_FENCE_GATE.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_DOOR.get(), 5, 20);
        registerFlammable(ModBlocks.GILDED_SKY_TRAPDOOR.get(), 5, 20);

        // Axe stripping - matches forge/.../block/custom/ModLogs.java's getToolModifiedState
        StrippableBlockRegistry.register(ModBlocks.MYSTIC_LOG.get(), ModBlocks.STRIPPED_MYSTIC_LOG.get());
        StrippableBlockRegistry.register(ModBlocks.MYSTIC_WOOD.get(), ModBlocks.STRIPPED_MYSTIC_WOOD.get());
        StrippableBlockRegistry.register(ModBlocks.SKY_LOG.get(), ModBlocks.STRIPPED_SKY_LOG.get());
        StrippableBlockRegistry.register(ModBlocks.SKY_WOOD.get(), ModBlocks.STRIPPED_SKY_WOOD.get());
        StrippableBlockRegistry.register(ModBlocks.GILDED_SKY_LOG.get(), ModBlocks.STRIPPED_GILDED_SKY_LOG.get());
        StrippableBlockRegistry.register(ModBlocks.GILDED_SKY_WOOD.get(), ModBlocks.STRIPPED_GILDED_SKY_WOOD.get());
    }

    private static void registerFlammable(Block block, int burn, int spread) {
        FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
    }
}
