package net.winepicfin.extrabiomes.commondatagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.List;

/**
 * Single source of truth for which of our blocks/items belong in the shared vanilla and convention
 * tags (wooden_doors, saplings, stripped_logs, ...). Every loader's tag generator reads these lists
 * instead of keeping its own copy, so a block can't end up tagged on one loader and not another.
 * Keep this file identical across the version branches; see ModTagContentsTest.
 */
public final class ModTagContents {
    private ModTagContents() {
    }

    public static final List<RegistrySupplier<Block>> WOODEN_STAIRS = List.of(
            ModBlocks.MYSTIC_STAIRS, ModBlocks.SKY_STAIRS, ModBlocks.PALM_STAIRS, ModBlocks.GILDED_SKY_STAIRS);

    public static final List<RegistrySupplier<Block>> WOODEN_SLABS = List.of(
            ModBlocks.MYSTIC_SLAB, ModBlocks.SKY_SLAB, ModBlocks.PALM_SLAB, ModBlocks.GILDED_SKY_SLAB);

    public static final List<RegistrySupplier<Block>> WOODEN_BUTTONS = List.of(
            ModBlocks.MYSTIC_BUTTON, ModBlocks.SKY_BUTTON, ModBlocks.PALM_BUTTON, ModBlocks.GILDED_SKY_BUTTON);

    public static final List<RegistrySupplier<Block>> WOODEN_PRESSURE_PLATES = List.of(
            ModBlocks.MYSTIC_PRESSURE_PLATE, ModBlocks.SKY_PRESSURE_PLATE, ModBlocks.PALM_PRESSURE_PLATE, ModBlocks.GILDED_SKY_PRESSURE_PLATE);

    public static final List<RegistrySupplier<Block>> WOODEN_DOORS = List.of(
            ModBlocks.MYSTIC_DOOR, ModBlocks.SKY_DOOR, ModBlocks.PALM_DOOR, ModBlocks.GILDED_SKY_DOOR);

    public static final List<RegistrySupplier<Block>> WOODEN_TRAPDOORS = List.of(
            ModBlocks.MYSTIC_TRAPDOOR, ModBlocks.SKY_TRAPDOOR, ModBlocks.PALM_TRAPDOOR, ModBlocks.GILDED_SKY_TRAPDOOR);

    /** Non-wooden stairs/slabs/walls (the wooden ones are added through the wooden_* tags). */
    public static final List<RegistrySupplier<Block>> STONE_STAIRS = List.of(
            ModBlocks.DENSE_CLOUD_STAIRS, ModBlocks.DENSE_CLOUD_BRICK_STAIRS,
            ModBlocks.BLACK_SANDSTONE_STAIRS, ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS);

    public static final List<RegistrySupplier<Block>> STONE_SLABS = List.of(
            ModBlocks.DENSE_CLOUD_SLAB, ModBlocks.DENSE_CLOUD_BRICK_SLAB,
            ModBlocks.BLACK_SANDSTONE_SLAB, ModBlocks.CUT_BLACK_SANDSTONE_SLAB, ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB);

    public static final List<RegistrySupplier<Block>> WALLS = List.of(ModBlocks.BLACK_SANDSTONE_WALL);

    public static final List<RegistrySupplier<Block>> STANDING_SIGNS = List.of(
            ModBlocks.MYSTIC_SIGN, ModBlocks.SKY_SIGN, ModBlocks.PALM_SIGN, ModBlocks.GILDED_SKY_SIGN);

    public static final List<RegistrySupplier<Block>> WALL_SIGNS = List.of(
            ModBlocks.MYSTIC_WALL_SIGN, ModBlocks.SKY_WALL_SIGN, ModBlocks.PALM_WALL_SIGN, ModBlocks.GILDED_SKY_WALL_SIGN);

    public static final List<RegistrySupplier<Block>> CEILING_HANGING_SIGNS = List.of(
            ModBlocks.MYSTIC_HANGING_SIGN, ModBlocks.SKY_HANGING_SIGN, ModBlocks.PALM_HANGING_SIGN, ModBlocks.GILDED_SKY_HANGING_SIGN);

    public static final List<RegistrySupplier<Block>> WALL_HANGING_SIGNS = List.of(
            ModBlocks.MYSTIC_WALL_HANGING_SIGN, ModBlocks.SKY_WALL_HANGING_SIGN, ModBlocks.PALM_WALL_HANGING_SIGN, ModBlocks.GILDED_SKY_WALL_HANGING_SIGN);

    public static final List<RegistrySupplier<Item>> SIGN_ITEMS = List.of(
            ModItems.MYSTIC_SIGN, ModItems.SKY_SIGN, ModItems.PALM_SIGN, ModItems.GILDED_SKY_SIGN);

    public static final List<RegistrySupplier<Item>> HANGING_SIGN_ITEMS = List.of(
            ModItems.MYSTIC_HANGING_SIGN, ModItems.SKY_HANGING_SIGN, ModItems.PALM_HANGING_SIGN, ModItems.GILDED_SKY_HANGING_SIGN);

    public static final List<RegistrySupplier<Block>> SAPLINGS = List.of(
            ModBlocks.MYSTIC_SAPLING, ModBlocks.SKY_SAPLING, ModBlocks.PALM_SAPLING);

    public static final List<RegistrySupplier<Block>> SAND = List.of(ModBlocks.BLACK_SAND);

    /** Logs with the bark stripped, for c:stripped_logs. */
    public static final List<RegistrySupplier<Block>> STRIPPED_LOGS = List.of(
            ModBlocks.STRIPPED_MYSTIC_LOG, ModBlocks.STRIPPED_SKY_LOG, ModBlocks.STRIPPED_PALM_LOG, ModBlocks.STRIPPED_GILDED_SKY_LOG);

    /** The all-bark "wood" blocks with the bark stripped, for c:stripped_woods. */
    public static final List<RegistrySupplier<Block>> STRIPPED_WOODS = List.of(
            ModBlocks.STRIPPED_MYSTIC_WOOD, ModBlocks.STRIPPED_SKY_WOOD, ModBlocks.STRIPPED_PALM_WOOD, ModBlocks.STRIPPED_GILDED_SKY_WOOD);

    /** Nether ores, for c:ores. */
    public static final List<RegistrySupplier<Block>> ORES = List.of(
            ModBlocks.NETHER_COAL_ORE, ModBlocks.NETHER_COPPER_ORE, ModBlocks.NETHER_DIAMOND_ORE, ModBlocks.NETHER_EMERALD_ORE,
            ModBlocks.NETHER_IRON_ORE, ModBlocks.NETHER_LAPIS_ORE, ModBlocks.NETHER_REDSTONE_ORE);

}
