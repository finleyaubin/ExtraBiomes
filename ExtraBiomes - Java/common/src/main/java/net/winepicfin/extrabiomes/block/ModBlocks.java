package net.winepicfin.extrabiomes.block;

import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.custom.*;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;
import net.winepicfin.extrabiomes.util.ModWoodTypes;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.tree.MysticTreeGrower;
import net.winepicfin.extrabiomes.worldgen.tree.PalmTreeGrower;
import net.winepicfin.extrabiomes.worldgen.tree.SkyTreeGrower;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.BLOCK);

    // noOcclusion() is needed because dense cloud is thematically translucent, but copies fully-opaque vanilla materials (white_wool/stone_bricks) that would otherwise block light.
    public static final RegistrySupplier<Block> DENSE_CLOUD = registerBlock("dense_cloud", () -> new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WHITE_WOOL).sound(SoundType.WOOL).strength(MiscBlockTuning.DENSE_CLOUD_DESTROY_SECONDS).noOcclusion()));
    public static final RegistrySupplier<Block> DENSE_CLOUD_BRICK = registerBlock("dense_cloud_brick", () -> new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE_BRICKS).sound(SoundType.WOOL).strength(MiscBlockTuning.DENSE_CLOUD_BRICK_DESTROY_SECONDS).noOcclusion()));
    public static final RegistrySupplier<Block> DENSE_CLOUD_BRICK_SLAB = registerBlock("dense_cloud_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE_BRICK_SLAB).sound(SoundType.WOOL).strength(0.5f).noOcclusion()));
    public static final RegistrySupplier<Block> DENSE_CLOUD_BRICK_STAIRS = registerBlock("dense_cloud_brick_stairs", () -> new StairBlock(ModBlocks.DENSE_CLOUD_BRICK.get().defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE_BRICK_STAIRS).sound(SoundType.WOOL).strength(0.5f).noOcclusion()));
    public static final RegistrySupplier<Block> NETHER_DIAMOND_ORE = registerBlock("nether_diamond_ore", () -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.ofLegacyCopy(Blocks.NETHERRACK).strength(2f).requiresCorrectToolForDrops()));
    public static final RegistrySupplier<LiquidBlock> GOO = BLOCKS.register("goo_block", () -> ExtraBiomesExpectPlatform.createGooLiquidBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WATER).sound(SoundType.SLIME_BLOCK).noLootTable()));
    public static final RegistrySupplier<PebbleBlock> PEBBLE = registerBlock("pebble_block", () -> new PebbleBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE).noOcclusion().noLootTable()));
    public static final RegistrySupplier<MossyPebbleBlock> MOSSY_PEBBLE = registerBlock("mossy_pebble_block", () -> new MossyPebbleBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE).noOcclusion().noLootTable()));
    public static final RegistrySupplier<Block> STICK_PILE = registerBlock("stick_pile", () -> ExtraBiomesExpectPlatform.createStickPileBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_LOG).noOcclusion().strength(StickPileTuning.DESTROY_SECONDS)));
    // Structure-only worldgen block, see FountainSpoutBlock - no BlockItem needed.
    public static final RegistrySupplier<Block> FOUNTAIN_SPOUT = BLOCKS.register("fountain_spout", () -> new FountainSpoutBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion().noLootTable().strength(-1.0f, 3600000.0f)));

    // Bedrock's black_sand has no gravity (engine limitation); Java copies vanilla SAND's falling behaviour instead.
    // FallingBlock became abstract as of 1.20.4 (subclasses must implement codec()) - ColoredFallingBlock
    // is vanilla's own concrete subclass, used by Blocks.SAND/GRAVEL themselves. 0x191919 matches
    // MapColor.COLOR_BLACK's own RGB value, kept consistent with the mapColor() below.
    public static final RegistrySupplier<Block> BLACK_SAND = registerBlock("black_sand", () -> new ColoredFallingBlock(new ColorRGBA(0x191919), BlockBehaviour.Properties.ofLegacyCopy(Blocks.SAND).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> BLACK_SANDSTONE = registerBlock("black_sandstone", () -> new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.SANDSTONE).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> CHISELED_BLACK_SANDSTONE = registerBlock("chiseled_black_sandstone", () -> new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CHISELED_SANDSTONE).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> CUT_BLACK_SANDSTONE = registerBlock("cut_black_sandstone", () -> new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CUT_SANDSTONE).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> SMOOTH_BLACK_SANDSTONE = registerBlock("smooth_black_sandstone", () -> new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.SMOOTH_SANDSTONE).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> BLACK_SANDSTONE_SLAB = registerBlock("black_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.SANDSTONE_SLAB).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> CUT_BLACK_SANDSTONE_SLAB = registerBlock("cut_black_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CUT_SANDSTONE_SLAB).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> SMOOTH_BLACK_SANDSTONE_SLAB = registerBlock("smooth_black_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.SMOOTH_SANDSTONE_SLAB).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> BLACK_SANDSTONE_STAIRS = registerBlock("black_sandstone_stairs", () -> new StairBlock(ModBlocks.BLACK_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.SANDSTONE_STAIRS).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> SMOOTH_BLACK_SANDSTONE_STAIRS = registerBlock("smooth_black_sandstone_stairs", () -> new StairBlock(ModBlocks.SMOOTH_BLACK_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.SMOOTH_SANDSTONE_STAIRS).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistrySupplier<Block> BLACK_SANDSTONE_WALL = registerBlock("black_sandstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.SANDSTONE_WALL).mapColor(MapColor.COLOR_BLACK)));

    private static final StandardWoodSet MYSTIC_WOOD_SET = registerStandardWoodSet("mystic", ModWoodTypes.MYSTIC);
    public static final RegistrySupplier<Block> MYSTIC_PLANKS = MYSTIC_WOOD_SET.planks();
    public static final RegistrySupplier<Block> MYSTIC_LOG = registerBlock("mystic_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> MYSTIC_WOOD = registerBlock("mystic_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> STRIPPED_MYSTIC_LOG = registerBlock("stripped_mystic_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> MYSTIC_LEAVES = registerBlock("mystic_leaves", () -> new ModLeavesWithSupport(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CHERRY_LEAVES).sound(SoundType.GRASS)));
    public static final RegistrySupplier<Block> STRIPPED_MYSTIC_WOOD = registerBlock("stripped_mystic_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> MYSTIC_SAPLING = registerBlock("mystic_sapling", () -> new SaplingBlock(MysticTreeGrower.GROWER, BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_SAPLING).strength(0f)));
    public static final RegistrySupplier<Block> MYSTIC_STAIRS = MYSTIC_WOOD_SET.stairs();
    public static final RegistrySupplier<Block> MYSTIC_SLAB = MYSTIC_WOOD_SET.slab();
    public static final RegistrySupplier<Block> MYSTIC_BUTTON = MYSTIC_WOOD_SET.button();
    public static final RegistrySupplier<Block> MYSTIC_PRESSURE_PLATE = MYSTIC_WOOD_SET.pressurePlate();
    public static final RegistrySupplier<Block> MYSTIC_FENCE = MYSTIC_WOOD_SET.fence();
    public static final RegistrySupplier<Block> MYSTIC_FENCE_GATE = MYSTIC_WOOD_SET.fenceGate();
    public static final RegistrySupplier<Block> MYSTIC_DOOR = MYSTIC_WOOD_SET.door();
    public static final RegistrySupplier<Block> MYSTIC_TRAPDOOR = MYSTIC_WOOD_SET.trapdoor();
    private static final StandardWoodSigns MYSTIC_SIGNS = registerWoodSigns("mystic", ModWoodTypes.MYSTIC, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
    public static final RegistrySupplier<Block> MYSTIC_SIGN = MYSTIC_SIGNS.sign();
    public static final RegistrySupplier<Block> MYSTIC_WALL_SIGN = MYSTIC_SIGNS.wallSign();
    public static final RegistrySupplier<Block> MYSTIC_HANGING_SIGN = MYSTIC_SIGNS.hangingSign();
    public static final RegistrySupplier<Block> MYSTIC_WALL_HANGING_SIGN = MYSTIC_SIGNS.wallHangingSign();
    private static final StandardWoodSet SKY_WOOD_SET = registerStandardWoodSet("sky", ModWoodTypes.SKY);
    public static final RegistrySupplier<Block> SKY_PLANKS = SKY_WOOD_SET.planks();
    public static final RegistrySupplier<Block> SKY_LOG = registerBlock("sky_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> SKY_WOOD = registerBlock("sky_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> STRIPPED_SKY_LOG = registerBlock("stripped_sky_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> SKY_LEAVES = registerBlock("sky_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CHERRY_LEAVES).sound(SoundType.GRASS)) );
    public static final RegistrySupplier<Block> STRIPPED_SKY_WOOD = registerBlock("stripped_sky_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> SKY_SAPLING = registerBlock("sky_sapling", () -> new SaplingBlock(SkyTreeGrower.GROWER, BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_SAPLING).strength(0f)));
    public static final RegistrySupplier<Block> SKY_STAIRS = SKY_WOOD_SET.stairs();
    public static final RegistrySupplier<Block> SKY_SLAB = SKY_WOOD_SET.slab();
    public static final RegistrySupplier<Block> SKY_BUTTON = SKY_WOOD_SET.button();
    public static final RegistrySupplier<Block> SKY_PRESSURE_PLATE = SKY_WOOD_SET.pressurePlate();
    public static final RegistrySupplier<Block> SKY_FENCE = SKY_WOOD_SET.fence();
    public static final RegistrySupplier<Block> SKY_FENCE_GATE = SKY_WOOD_SET.fenceGate();
    public static final RegistrySupplier<Block> SKY_DOOR = SKY_WOOD_SET.door();
    public static final RegistrySupplier<Block> SKY_TRAPDOOR = SKY_WOOD_SET.trapdoor();
    private static final StandardWoodSigns SKY_SIGNS = registerWoodSigns("sky", ModWoodTypes.SKY, Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
    public static final RegistrySupplier<Block> SKY_SIGN = SKY_SIGNS.sign();
    public static final RegistrySupplier<Block> SKY_WALL_SIGN = SKY_SIGNS.wallSign();
    public static final RegistrySupplier<Block> SKY_HANGING_SIGN = SKY_SIGNS.hangingSign();
    public static final RegistrySupplier<Block> SKY_WALL_HANGING_SIGN = SKY_SIGNS.wallHangingSign();
    private static final StandardWoodSet PALM_WOOD_SET = registerStandardWoodSet("palm", ModWoodTypes.PALM);
    public static final RegistrySupplier<Block> PALM_PLANKS = PALM_WOOD_SET.planks();
    public static final RegistrySupplier<Block> PALM_LOG = registerBlock("palm_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> PALM_WOOD = registerBlock("palm_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> STRIPPED_PALM_LOG = registerBlock("stripped_palm_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> PALM_LEAVES = registerBlock("palm_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CHERRY_LEAVES).sound(SoundType.GRASS)) );
    public static final RegistrySupplier<Block> STRIPPED_PALM_WOOD = registerBlock("stripped_palm_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> PALM_SAPLING = registerBlock("palm_sapling", () -> new PalmSaplingBlock(PalmTreeGrower.GROWER, BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_SAPLING).strength(0f)));
    public static final RegistrySupplier<Block> PALM_STAIRS = PALM_WOOD_SET.stairs();
    public static final RegistrySupplier<Block> PALM_SLAB = PALM_WOOD_SET.slab();
    public static final RegistrySupplier<Block> PALM_BUTTON = PALM_WOOD_SET.button();
    public static final RegistrySupplier<Block> PALM_PRESSURE_PLATE = PALM_WOOD_SET.pressurePlate();
    public static final RegistrySupplier<Block> PALM_FENCE = PALM_WOOD_SET.fence();
    public static final RegistrySupplier<Block> PALM_FENCE_GATE = PALM_WOOD_SET.fenceGate();
    public static final RegistrySupplier<Block> PALM_DOOR = PALM_WOOD_SET.door();
    public static final RegistrySupplier<Block> PALM_TRAPDOOR = PALM_WOOD_SET.trapdoor();
    private static final StandardWoodSigns PALM_SIGNS = registerWoodSigns("palm", ModWoodTypes.PALM, Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
    public static final RegistrySupplier<Block> PALM_SIGN = PALM_SIGNS.sign();
    public static final RegistrySupplier<Block> PALM_WALL_SIGN = PALM_SIGNS.wallSign();
    public static final RegistrySupplier<Block> PALM_HANGING_SIGN = PALM_SIGNS.hangingSign();
    public static final RegistrySupplier<Block> PALM_WALL_HANGING_SIGN = PALM_SIGNS.wallHangingSign();
    private static final StandardWoodSet GILDED_SKY_WOOD_SET = registerStandardWoodSet("gilded_sky", ModWoodTypes.GILDED_SKY);
    public static final RegistrySupplier<Block> GILDED_SKY_PLANKS = GILDED_SKY_WOOD_SET.planks();
    public static final RegistrySupplier<Block> GILDED_SKY_LOG = registerBlock("gilded_sky_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> GILDED_SKY_WOOD = registerBlock("gilded_sky_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> STRIPPED_GILDED_SKY_LOG = registerBlock("stripped_gilded_sky_log", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_STEM).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> STRIPPED_GILDED_SKY_WOOD = registerBlock("stripped_gilded_sky_wood", () -> ExtraBiomesExpectPlatform.createLogBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STRIPPED_WARPED_HYPHAE).strength(ModLogsTuning.DESTROY_SECONDS)));
    public static final RegistrySupplier<Block> GILDED_SKY_STAIRS = GILDED_SKY_WOOD_SET.stairs();
    public static final RegistrySupplier<Block> GILDED_SKY_SLAB = GILDED_SKY_WOOD_SET.slab();
    public static final RegistrySupplier<Block> GILDED_SKY_BUTTON = GILDED_SKY_WOOD_SET.button();
    public static final RegistrySupplier<Block> GILDED_SKY_PRESSURE_PLATE = GILDED_SKY_WOOD_SET.pressurePlate();
    public static final RegistrySupplier<Block> GILDED_SKY_FENCE = GILDED_SKY_WOOD_SET.fence();
    public static final RegistrySupplier<Block> GILDED_SKY_FENCE_GATE = GILDED_SKY_WOOD_SET.fenceGate();
    public static final RegistrySupplier<Block> GILDED_SKY_DOOR = GILDED_SKY_WOOD_SET.door();
    public static final RegistrySupplier<Block> GILDED_SKY_TRAPDOOR = GILDED_SKY_WOOD_SET.trapdoor();
    private static final StandardWoodSigns GILDED_SKY_SIGNS = registerWoodSigns("gilded_sky", ModWoodTypes.GILDED_SKY, Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
    public static final RegistrySupplier<Block> GILDED_SKY_SIGN = GILDED_SKY_SIGNS.sign();
    public static final RegistrySupplier<Block> GILDED_SKY_WALL_SIGN = GILDED_SKY_SIGNS.wallSign();
    public static final RegistrySupplier<Block> GILDED_SKY_HANGING_SIGN = GILDED_SKY_SIGNS.hangingSign();
    public static final RegistrySupplier<Block> GILDED_SKY_WALL_HANGING_SIGN = GILDED_SKY_SIGNS.wallHangingSign();
    // Bonemeal on each grows that colour's own huge mushroom structure (MushroomFeatures).
    public static final RegistrySupplier<Block>  BLACK_MUSHROOM= registerBlock("black_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_BLACK_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  BLUE_MUSHROOM= registerBlock("blue_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_BLUE_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  CYAN_MUSHROOM= registerBlock("cyan_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_CYAN_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  GREEN_MUSHROOM= registerBlock("green_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_GREEN_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  ORANGE_MUSHROOM= registerBlock("orange_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_ORANGE_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  PURPLE_MUSHROOM= registerBlock("purple_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_PURPLE_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  WHITE_MUSHROOM= registerBlock("white_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_WHITE_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  YELLOW_MUSHROOM= registerBlock("yellow_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_YELLOW_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM)));
    public static final RegistrySupplier<Block>  GLOW_MUSHROOM= registerBlock("glow_mushroom", () -> new MushroomBlock(MushroomFeatures.HUGE_GLOW_MUSHROOM_KEY, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM).lightLevel(BlockState->MiscBlockTuning.GLOW_MUSHROOM_LIGHT_EMISSION)));
    public static final RegistrySupplier<Block>  BLACK_MUSHROOM_BLOCK= registerBlock("black_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  BLUE_MUSHROOM_BLOCK= registerBlock("blue_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  CYAN_MUSHROOM_BLOCK= registerBlock("cyan_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  GREEN_MUSHROOM_BLOCK= registerBlock("green_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  ORANGE_MUSHROOM_BLOCK= registerBlock("orange_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  PURPLE_MUSHROOM_BLOCK= registerBlock("purple_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  WHITE_MUSHROOM_BLOCK= registerBlock("white_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  YELLOW_MUSHROOM_BLOCK= registerBlock("yellow_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT)));
    public static final RegistrySupplier<Block>  GLOW_MUSHROOM_BLOCK= registerBlock("glow_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.RED_MUSHROOM_BLOCK).sound(SoundType.SHROOMLIGHT).lightLevel(BlockState->MiscBlockTuning.GLOW_MUSHROOM_BLOCK_LIGHT_EMISSION)));


    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        RegistrySupplier<T> output = BLOCKS.register(name, block);
        registerBlockItem(name, output);
        return output;
    }

    private static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // planks/stairs/slab/etc. follow an identical pattern across every wood set - only the name prefix and wood type differ.
    private record StandardWoodSet(RegistrySupplier<Block> planks, RegistrySupplier<Block> stairs, RegistrySupplier<Block> slab, RegistrySupplier<Block> button, RegistrySupplier<Block> pressurePlate, RegistrySupplier<Block> fence, RegistrySupplier<Block> fenceGate, RegistrySupplier<Block> door, RegistrySupplier<Block> trapdoor) {
    }

    private static StandardWoodSet registerStandardWoodSet(String name, WoodType woodType) {
        RegistrySupplier<Block> planks = registerBlock(name + "_planks", () -> new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS)));
        RegistrySupplier<Block> stairs = registerBlock(name + "_stairs", () -> new StairBlock(planks.get().defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS)));
        RegistrySupplier<Block> slab = registerBlock(name + "_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS)));
        // The trailing "arrows can activate" boolean moved into BlockSetType itself as of 1.20.4 -
        // BlockSetType.OAK's own default already matches the old explicit "true".
        RegistrySupplier<Block> button = registerBlock(name + "_button", () -> new ButtonBlock(BlockSetType.OAK, 10, BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_BUTTON)));
        // Sensitivity moved from a constructor arg to BlockSetType itself as of 1.20.4 - BlockSetType.OAK's
        // own default is already PressurePlateSensitivity.EVERYTHING, matching the old explicit arg.
        RegistrySupplier<Block> pressurePlate = registerBlock(name + "_pressure_plate", () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS)));
        RegistrySupplier<Block> fence = registerBlock(name + "_fence", () -> new FenceBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS)));
        RegistrySupplier<Block> fenceGate = registerBlock(name + "_fence_gate", () -> new FenceGateBlock(woodType, BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS)));
        RegistrySupplier<Block> door = registerBlock(name + "_door", () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS).noOcclusion()));
        RegistrySupplier<Block> trapdoor = registerBlock(name + "_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofLegacyCopy(Blocks.WARPED_PLANKS).noOcclusion()));
        return new StandardWoodSet(planks, stairs, slab, button, pressurePlate, fence, fenceGate, door, trapdoor);
    }

    // Sign block parents vary by wood set (mystic copies the warped signs, the rest copy oak), so they're passed in rather than hard-coded.
    private record StandardWoodSigns(RegistrySupplier<Block> sign, RegistrySupplier<Block> wallSign, RegistrySupplier<Block> hangingSign, RegistrySupplier<Block> wallHangingSign) {
    }

    private static StandardWoodSigns registerWoodSigns(String name, WoodType woodType, Block signBase, Block wallSignBase, Block hangingSignBase, Block wallHangingSignBase) {
        RegistrySupplier<Block> sign = BLOCKS.register(name + "_sign", () -> new ModStandingSignBlock(BlockBehaviour.Properties.ofLegacyCopy(signBase), woodType));
        RegistrySupplier<Block> wallSign = BLOCKS.register(name + "_wall_sign", () -> new ModWallSignBlock(BlockBehaviour.Properties.ofLegacyCopy(wallSignBase), woodType));
        RegistrySupplier<Block> hangingSign = BLOCKS.register(name + "_hanging_sign", () -> new ModHangingSignBlock(BlockBehaviour.Properties.ofLegacyCopy(hangingSignBase), woodType));
        RegistrySupplier<Block> wallHangingSign = BLOCKS.register(name + "_wall_hanging_sign", () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.ofLegacyCopy(wallHangingSignBase), woodType));
        return new StandardWoodSigns(sign, wallSign, hangingSign, wallHangingSign);
    }

    public static void register() {
        BLOCKS.register();
    }
}
