package net.winepicfin.extrabiomes.commondatagen.loot;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

// Shared body of both loaders' block loot table generators (forge/.../datagen/loot/ModBlockLootTables
// and fabric/.../datagen/loot/ModBlockLootTables). Both ultimately drive vanilla's
// BlockLootSubProvider (Fabric API's FabricBlockLootTableProvider extends it, to relax its "every
// block in the registry needs a table" completeness check to just this mod's blocks; Forge's own
// subclass does the same via a getKnownBlocks() override), but dropSelf/add/createXTable are all
// *protected* BlockLootSubProvider members - a plain external helper can't call them through a
// passed-in instance (Java's protected-access rule requires the accessing class itself be a
// subclass), so this instead takes bound method references, which each loader's own generate()
// creates from inside the actual subclass (where the protected access is legal) and hands in.
public class ModBlockLootTableEntries {
    public static void populate(
            Consumer<Block> dropSelf,
            BiConsumer<Block, Function<Block, LootTable.Builder>> add,
            Function<Block, LootTable.Builder> createSlabItemTable,
            Function<Block, LootTable.Builder> createDoorTable,
            BiFunctionLeaves createLeavesDrops,
            BiFunctionOre createOreDrop,
            Function<net.minecraft.world.level.ItemLike, LootTable.Builder> createSingleItemTable) {
        dropSelf.accept(ModBlocks.DENSE_CLOUD.get());
        dropSelf.accept(ModBlocks.DENSE_CLOUD_BRICK.get());
        dropSelf.accept(ModBlocks.DENSE_CLOUD_BRICK_STAIRS.get());
        add.accept(ModBlocks.DENSE_CLOUD_BRICK_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.DENSE_CLOUD_BRICK_SLAB.get()));
        add.accept(ModBlocks.NETHER_DIAMOND_ORE.get(), block -> createOreDrop.apply(ModBlocks.NETHER_DIAMOND_ORE.get(), Items.DIAMOND));
        dropSelf.accept(ModBlocks.STICK_PILE.get());

        // Black Sand
        dropSelf.accept(ModBlocks.BLACK_SAND.get());
        dropSelf.accept(ModBlocks.BLACK_SANDSTONE.get());
        dropSelf.accept(ModBlocks.CHISELED_BLACK_SANDSTONE.get());
        dropSelf.accept(ModBlocks.CUT_BLACK_SANDSTONE.get());
        dropSelf.accept(ModBlocks.SMOOTH_BLACK_SANDSTONE.get());
        dropSelf.accept(ModBlocks.BLACK_SANDSTONE_STAIRS.get());
        dropSelf.accept(ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.get());
        dropSelf.accept(ModBlocks.BLACK_SANDSTONE_WALL.get());
        add.accept(ModBlocks.BLACK_SANDSTONE_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.BLACK_SANDSTONE_SLAB.get()));
        add.accept(ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get()));
        add.accept(ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get()));

        // Mystic Wood
        dropSelf.accept(ModBlocks.MYSTIC_PLANKS.get());
        dropSelf.accept(ModBlocks.MYSTIC_LOG.get());
        dropSelf.accept(ModBlocks.MYSTIC_WOOD.get());
        dropSelf.accept(ModBlocks.STRIPED_MYSTIC_LOG.get());
        dropSelf.accept(ModBlocks.STRIPED_MYSTIC_WOOD.get());
        dropSelf.accept(ModBlocks.MYSTIC_STAIRS.get());
        dropSelf.accept(ModBlocks.MYSTIC_BUTTON.get());
        dropSelf.accept(ModBlocks.MYSTIC_PRESSURE_PLATE.get());
        dropSelf.accept(ModBlocks.MYSTIC_TRAPDOOR.get());
        dropSelf.accept(ModBlocks.MYSTIC_FENCE.get());
        dropSelf.accept(ModBlocks.MYSTIC_FENCE_GATE.get());
        add.accept(ModBlocks.MYSTIC_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.MYSTIC_SLAB.get()));
        add.accept(ModBlocks.MYSTIC_DOOR.get(), block -> createDoorTable.apply(ModBlocks.MYSTIC_DOOR.get()));
        dropSelf.accept(ModBlocks.MYSTIC_SAPLING.get());
        add.accept(ModBlocks.MYSTIC_LEAVES.get(), block -> createLeavesDrops.apply(block, ModBlocks.MYSTIC_LEAVES.get()));
        add.accept(ModBlocks.MYSTIC_SIGN.get(), block -> createSingleItemTable.apply(ModItems.MYSTIC_SIGN.get()));
        add.accept(ModBlocks.MYSTIC_WALL_SIGN.get(), block -> createSingleItemTable.apply(ModItems.MYSTIC_SIGN.get()));
        add.accept(ModBlocks.MYSTIC_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.MYSTIC_HANGING_SIGN.get()));
        add.accept(ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.MYSTIC_HANGING_SIGN.get()));
        // Sky Wood
        dropSelf.accept(ModBlocks.SKY_PLANKS.get());
        dropSelf.accept(ModBlocks.SKY_LOG.get());
        dropSelf.accept(ModBlocks.SKY_WOOD.get());
        dropSelf.accept(ModBlocks.STRIPED_SKY_LOG.get());
        dropSelf.accept(ModBlocks.STRIPED_SKY_WOOD.get());
        dropSelf.accept(ModBlocks.SKY_STAIRS.get());
        dropSelf.accept(ModBlocks.SKY_BUTTON.get());
        dropSelf.accept(ModBlocks.SKY_PRESSURE_PLATE.get());
        dropSelf.accept(ModBlocks.SKY_TRAPDOOR.get());
        dropSelf.accept(ModBlocks.SKY_FENCE.get());
        dropSelf.accept(ModBlocks.SKY_FENCE_GATE.get());
        add.accept(ModBlocks.SKY_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.SKY_SLAB.get()));
        add.accept(ModBlocks.SKY_DOOR.get(), block -> createDoorTable.apply(ModBlocks.SKY_DOOR.get()));
        dropSelf.accept(ModBlocks.SKY_SAPLING.get());
        add.accept(ModBlocks.SKY_LEAVES.get(), block -> createLeavesDrops.apply(block, ModBlocks.SKY_LEAVES.get()));
        add.accept(ModBlocks.SKY_SIGN.get(), block -> createSingleItemTable.apply(ModItems.SKY_SIGN.get()));
        add.accept(ModBlocks.SKY_WALL_SIGN.get(), block -> createSingleItemTable.apply(ModItems.SKY_SIGN.get()));
        add.accept(ModBlocks.SKY_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.SKY_HANGING_SIGN.get()));
        add.accept(ModBlocks.SKY_WALL_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.SKY_HANGING_SIGN.get()));
        // Palm Wood
        dropSelf.accept(ModBlocks.PALM_PLANKS.get());
        dropSelf.accept(ModBlocks.PALM_LOG.get());
        dropSelf.accept(ModBlocks.PALM_WOOD.get());
        dropSelf.accept(ModBlocks.STRIPED_PALM_LOG.get());
        dropSelf.accept(ModBlocks.STRIPED_PALM_WOOD.get());
        dropSelf.accept(ModBlocks.PALM_STAIRS.get());
        dropSelf.accept(ModBlocks.PALM_BUTTON.get());
        dropSelf.accept(ModBlocks.PALM_PRESSURE_PLATE.get());
        dropSelf.accept(ModBlocks.PALM_TRAPDOOR.get());
        dropSelf.accept(ModBlocks.PALM_FENCE.get());
        dropSelf.accept(ModBlocks.PALM_FENCE_GATE.get());
        add.accept(ModBlocks.PALM_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.PALM_SLAB.get()));
        add.accept(ModBlocks.PALM_DOOR.get(), block -> createDoorTable.apply(ModBlocks.PALM_DOOR.get()));
        dropSelf.accept(ModBlocks.PALM_SAPLING.get());
        add.accept(ModBlocks.PALM_LEAVES.get(), block -> createLeavesDrops.apply(block, ModBlocks.PALM_LEAVES.get()));
        add.accept(ModBlocks.PALM_SIGN.get(), block -> createSingleItemTable.apply(ModItems.PALM_SIGN.get()));
        add.accept(ModBlocks.PALM_WALL_SIGN.get(), block -> createSingleItemTable.apply(ModItems.PALM_SIGN.get()));
        add.accept(ModBlocks.PALM_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.PALM_HANGING_SIGN.get()));
        add.accept(ModBlocks.PALM_WALL_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.PALM_HANGING_SIGN.get()));
        // Gilded sky Wood
        dropSelf.accept(ModBlocks.GILDED_SKY_PLANKS.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_LOG.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_WOOD.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_STAIRS.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_BUTTON.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_PRESSURE_PLATE.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_TRAPDOOR.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_FENCE.get());
        dropSelf.accept(ModBlocks.GILDED_SKY_FENCE_GATE.get());
        add.accept(ModBlocks.GILDED_SKY_SLAB.get(), block -> createSlabItemTable.apply(ModBlocks.GILDED_SKY_SLAB.get()));
        add.accept(ModBlocks.GILDED_SKY_DOOR.get(), block -> createDoorTable.apply(ModBlocks.GILDED_SKY_DOOR.get()));
        add.accept(ModBlocks.GILDED_SKY_SIGN.get(), block -> createSingleItemTable.apply(ModItems.GILDED_SKY_SIGN.get()));
        add.accept(ModBlocks.GILDED_SKY_WALL_SIGN.get(), block -> createSingleItemTable.apply(ModItems.GILDED_SKY_SIGN.get()));
        add.accept(ModBlocks.GILDED_SKY_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.GILDED_SKY_HANGING_SIGN.get()));
        add.accept(ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.get(), block -> createSingleItemTable.apply(ModItems.GILDED_SKY_HANGING_SIGN.get()));
        // Small Mushrooms
        dropSelf.accept(ModBlocks.BLACK_MUSHROOM.get());
        dropSelf.accept(ModBlocks.BLUE_MUSHROOM.get());
        dropSelf.accept(ModBlocks.CYAN_MUSHROOM.get());
        dropSelf.accept(ModBlocks.GREEN_MUSHROOM.get());
        dropSelf.accept(ModBlocks.ORANGE_MUSHROOM.get());
        dropSelf.accept(ModBlocks.PURPLE_MUSHROOM.get());
        dropSelf.accept(ModBlocks.WHITE_MUSHROOM.get());
        dropSelf.accept(ModBlocks.YELLOW_MUSHROOM.get());
        // Mushrooms
        dropSelf.accept(ModBlocks.BLACK_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.BLUE_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.CYAN_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.GREEN_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.ORANGE_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.PURPLE_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.WHITE_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.YELLOW_MUSHROOM_BLOCK.get());
        dropSelf.accept(ModBlocks.GLOW_MUSHROOM_BLOCK.get());
    }

    // createLeavesDrops/createOreDrop take more parameters than java.util.function's stock
    // BiFunction shapes conveniently support alongside the varargs float... chances parameter,
    // so each loader binds a tiny adapter lambda around its own protected method instead.
    @FunctionalInterface
    public interface BiFunctionLeaves {
        LootTable.Builder apply(Block leavesBlock, Block saplingBlock);
    }

    @FunctionalInterface
    public interface BiFunctionOre {
        LootTable.Builder apply(Block oreBlock, net.minecraft.world.item.Item item);
    }
}
