package net.winepicfin.extrabiomes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.custom.*;
import net.winepicfin.extrabiomes.fluid.ModFluids;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.util.ModWoodTypes;
import net.winepicfin.extrabiomes.worldgen.tree.MysticTreeGrower;
import net.winepicfin.extrabiomes.worldgen.tree.PalmTreeGrower;
import net.winepicfin.extrabiomes.worldgen.tree.SkyTreeGrower;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtraBiomes.MOD_ID);

    public static final RegistryObject<Block> DENSE_CLOUD = registerBlock("dense_cloud", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOL).strength(0.3f)));
    public static final RegistryObject<Block> DENSE_CLOUD_BRICK = registerBlock("dense_cloud_brick", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.WOOL).strength(0.5f)));
    public static final RegistryObject<Block> NETHER_DIAMOND_ORE = registerBlock("nether_diamond_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.NETHERRACK).strength(2f).requiresCorrectToolForDrops(), UniformInt.of(3, 7)));
    public static final RegistryObject<LiquidBlock> GOO = BLOCKS.register("goo_block", () -> new LiquidBlock(ModFluids.SOURCE_GOO, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
    public static final RegistryObject<PebbleBlock> PEBBLE = registerBlock("pebble_block", () -> new PebbleBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));
    public static final RegistryObject<MossyPebbleBlock> MOSSY_PEBBLE = registerBlock("mossy_pebble_block", () -> new MossyPebbleBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));

    //~~~~~~~~~mystic wood~~~~~~~~\\
    public static final RegistryObject<Block> MYSTIC_PLANKS = registerBlock("mystic_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }
    });
    public static final RegistryObject<Block> MYSTIC_LOG = registerBlock("mystic_log", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_STEM).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_WOOD = registerBlock("mystic_wood", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_HYPHAE).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> STRIPED_MYSTIC_LOG = registerBlock("striped_mystic_log", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_STEM).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_LEAVES = registerBlock("mystic_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.CHERRY_LEAVES).sound(SoundType.AMETHYST)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
        }
    });
    public static final RegistryObject<Block> STRIPED_MYSTIC_WOOD = registerBlock("striped_mystic_wood", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_HYPHAE).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_SAPLING = registerBlock("mystic_sapling", () -> new SaplingBlock(new MysticTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).strength(0f)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_STAIRS = registerBlock("mystic_stairs", () -> new StairBlock(() -> ModBlocks.MYSTIC_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_SLAB = registerBlock("mystic_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_BUTTON = registerBlock("mystic_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 10, true) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_PRESSURE_PLATE = registerBlock("mystic_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_FENCE = registerBlock("mystic_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_FENCE_GATE = registerBlock("mystic_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_DOOR = registerBlock("mystic_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_TRAPDOOR = registerBlock("mystic_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> MYSTIC_SIGN = BLOCKS.register("mystic_sign",()->new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_SIGN), ModWoodTypes.MYSTIC));
    public static final RegistryObject<Block> MYSTIC_WALL_SIGN = BLOCKS.register("mystic_wall_sign",()->new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_WALL_SIGN), ModWoodTypes.MYSTIC));
    public static final RegistryObject<Block> MYSTIC_HANGING_SIGN = BLOCKS.register("mystic_hanging_sign",()->new ModHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_HANGING_SIGN), ModWoodTypes.MYSTIC));
    public static final RegistryObject<Block> MYSTIC_WALL_HANGING_SIGN = BLOCKS.register("mystic_wall_hanging_sign",()->new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_WALL_HANGING_SIGN), ModWoodTypes.MYSTIC));
    //~~~~~~~~~sky wood~~~~~~~~\\
    public static final RegistryObject<Block> SKY_PLANKS = registerBlock("sky_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }
    });
    public static final RegistryObject<Block> SKY_LOG = registerBlock("sky_log", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_STEM).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_WOOD = registerBlock("sky_wood", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_HYPHAE).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> STRIPED_SKY_LOG = registerBlock("striped_sky_log", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_STEM).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_LEAVES = registerBlock("sky_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.CHERRY_LEAVES).sound(SoundType.AMETHYST)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
        }
    });
    public static final RegistryObject<Block> STRIPED_SKY_WOOD = registerBlock("striped_sky_wood", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_HYPHAE).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_SAPLING = registerBlock("sky_sapling", () -> new SaplingBlock(new SkyTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).strength(0f)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_STAIRS = registerBlock("sky_stairs", () -> new StairBlock(() -> ModBlocks.SKY_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_SLAB = registerBlock("sky_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_BUTTON = registerBlock("sky_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 10, true) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_PRESSURE_PLATE = registerBlock("sky_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_FENCE = registerBlock("sky_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_FENCE_GATE = registerBlock("sky_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_DOOR = registerBlock("sky_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_TRAPDOOR = registerBlock("sky_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> SKY_SIGN = BLOCKS.register("sky_sign",()->new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN), ModWoodTypes.SKY));
    public static final RegistryObject<Block> SKY_WALL_SIGN = BLOCKS.register("sky_wall_sign",()->new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN), ModWoodTypes.SKY));
    public static final RegistryObject<Block> SKY_HANGING_SIGN = BLOCKS.register("sky_hanging_sign",()->new ModHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), ModWoodTypes.SKY));
    public static final RegistryObject<Block> SKY_WALL_HANGING_SIGN = BLOCKS.register("sky_wall_hanging_sign",()->new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodTypes.SKY));
    //~~~~~~~~~Palm wood~~~~~~~~\\
    public static final RegistryObject<Block> PALM_PLANKS = registerBlock("palm_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }
    });
    public static final RegistryObject<Block> PALM_LOG = registerBlock("palm_log", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_STEM).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_WOOD = registerBlock("palm_wood", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_HYPHAE).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> STRIPED_PALM_LOG = registerBlock("striped_palm_log", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_STEM).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_LEAVES = registerBlock("palm_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.CHERRY_LEAVES).sound(SoundType.AMETHYST)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
        }
    });
    public static final RegistryObject<Block> STRIPED_PALM_WOOD = registerBlock("striped_palm_wood", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_HYPHAE).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_SAPLING = registerBlock("palm_sapling", () -> new SaplingBlock(new PalmTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).strength(0f)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_STAIRS = registerBlock("palm_stairs", () -> new StairBlock(() -> ModBlocks.PALM_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_SLAB = registerBlock("palm_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_BUTTON = registerBlock("palm_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 10, true) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_PRESSURE_PLATE = registerBlock("palm_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_FENCE = registerBlock("palm_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_FENCE_GATE = registerBlock("palm_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_DOOR = registerBlock("palm_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_TRAPDOOR = registerBlock("palm_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> PALM_SIGN = BLOCKS.register("palm_sign",()->new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN), ModWoodTypes.PALM));
    public static final RegistryObject<Block> PALM_WALL_SIGN = BLOCKS.register("palm_wall_sign",()->new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN), ModWoodTypes.PALM));
    public static final RegistryObject<Block> PALM_HANGING_SIGN = BLOCKS.register("palm_hanging_sign",()->new ModHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), ModWoodTypes.PALM));
    public static final RegistryObject<Block> PALM_WALL_HANGING_SIGN = BLOCKS.register("palm_wall_hanging_sign",()->new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodTypes.PALM));
    //~~~~~~~~~Gilded sky wood~~~~~~~~\\
    public static final RegistryObject<Block> GILDED_SKY_PLANKS = registerBlock("gilded_sky_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_LOG = registerBlock("gilded_sky_log", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_STEM).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_WOOD = registerBlock("gilded_sky_wood", () -> new ModLogs(BlockBehaviour.Properties.copy(Blocks.WARPED_HYPHAE).strength(5f)) {
        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_STAIRS = registerBlock("gilded_sky_stairs", () -> new StairBlock(() -> ModBlocks.GILDED_SKY_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_SLAB = registerBlock("gilded_sky_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_BUTTON = registerBlock("gilded_sky_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 10, true) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_PRESSURE_PLATE = registerBlock("gilded_sky_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_FENCE = registerBlock("gilded_sky_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_FENCE_GATE = registerBlock("gilded_sky_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS), SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_DOOR = registerBlock("gilded_sky_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_TRAPDOOR = registerBlock("gilded_sky_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion(), BlockSetType.OAK) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }
    });
    public static final RegistryObject<Block> GILDED_SKY_SIGN = BLOCKS.register("gilded_sky_sign",()->new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN), ModWoodTypes.GILDED_SKY));
    public static final RegistryObject<Block> GILDED_SKY_WALL_SIGN = BLOCKS.register("gilded_sky_wall_sign",()->new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN), ModWoodTypes.GILDED_SKY));
    public static final RegistryObject<Block> GILDED_SKY_HANGING_SIGN = BLOCKS.register("gilded_sky_hanging_sign",()->new ModHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), ModWoodTypes.GILDED_SKY));
    public static final RegistryObject<Block> GILDED_SKY_WALL_HANGING_SIGN = BLOCKS.register("gilded_sky_wall_hanging_sign",()->new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodTypes.GILDED_SKY));
//~~~~~~~~~Mushroom Blocks~~~~~~~~\\
    public static final RegistryObject<Block>  BLACK_MUSHROOM_BLOCK= registerBlock("black_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  BLUE_MUSHROOM_BLOCK= registerBlock("blue_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  CYAN_MUSHROOM_BLOCK= registerBlock("cyan_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  GREEN_MUSHROOM_BLOCK= registerBlock("green_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  ORANGE_MUSHROOM_BLOCK= registerBlock("orange_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  PURPLE_MUSHROOM_BLOCK= registerBlock("purple_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  WHITE_MUSHROOM_BLOCK= registerBlock("white_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  YELLOW_MUSHROOM_BLOCK= registerBlock("yellow_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
    public static final RegistryObject<Block>  GLOW_MUSHROOM_BLOCK= registerBlock("glow_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK).lightLevel(BlockState->15)));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> output = BLOCKS.register(name, block);
        registerBlockItem(name, output);
        return output;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
