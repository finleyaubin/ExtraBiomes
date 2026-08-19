package net.winepicfin.extrabiomes.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> EXTRABIOMES_TAB = CREATIVE_MODE_TAB.register("extrabiomes_tab",()-> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).icon(()->new ItemStack(ModBlocks.MYSTIC_SAPLING.get()))
            .title(Component.translatable("creativetab.extrabiomes"))
            .displayItems((pParameters, pOutput)->{
                // Items
                pOutput.accept(ModItems.PEBBLE.get());
                pOutput.accept(ModItems.MOSSY_PEBBLE.get());
                pOutput.accept(ModItems.RAZOR_FEATHER.get());
                pOutput.accept(ModItems.DIAMOND_RAZOR_FEATHER.get());
                pOutput.accept(ModItems.NETHERITE_RAZOR_FEATHER.get());
                pOutput.accept(ModItems.FROGS_LEGS.get());
                pOutput.accept(ModItems.COOKED_FROGS_LEGS.get());
                pOutput.accept(ModItems.PIRANHA.get());
                pOutput.accept(ModItems.COOKED_PIRANHA.get());
                pOutput.accept(ModItems.WORM.get());
                pOutput.accept(ModItems.BAIT.get());
                pOutput.accept(ModItems.JELLYFISH_JAM_BOTTLE.get());
                pOutput.accept(ModItems.JELLYFISHING_NET_EMPTY.get());
                pOutput.accept(ModItems.JELLYFISHING_NET_FULL.get());
                pOutput.accept(ModItems.BUCKET_OF_GOO.get());
                pOutput.accept(ModItems.FROG_HELMET.get());
                // Blocks
                pOutput.accept(ModBlocks.DENSE_CLOUD.get());
                pOutput.accept(ModBlocks.DENSE_CLOUD_BRICK.get());
                pOutput.accept(ModBlocks.NETHER_DIAMOND_ORE.get());
                // Black Sand
                pOutput.accept(ModBlocks.BLACK_SAND.get());
                pOutput.accept(ModBlocks.BLACK_SANDSTONE.get());
                pOutput.accept(ModBlocks.CHISELED_BLACK_SANDSTONE.get());
                pOutput.accept(ModBlocks.CUT_BLACK_SANDSTONE.get());
                pOutput.accept(ModBlocks.SMOOTH_BLACK_SANDSTONE.get());
                pOutput.accept(ModBlocks.BLACK_SANDSTONE_SLAB.get());
                pOutput.accept(ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get());
                pOutput.accept(ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get());
                pOutput.accept(ModBlocks.BLACK_SANDSTONE_STAIRS.get());
                pOutput.accept(ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.get());
                pOutput.accept(ModBlocks.BLACK_SANDSTONE_WALL.get());
                // Mystic Wood
                pOutput.accept(ModBlocks.MYSTIC_PLANKS.get());
                pOutput.accept(ModBlocks.MYSTIC_STAIRS.get());
                pOutput.accept(ModBlocks.MYSTIC_SLAB.get());
                pOutput.accept(ModBlocks.MYSTIC_BUTTON.get());
                pOutput.accept(ModBlocks.MYSTIC_PRESSURE_PLATE.get());
                pOutput.accept(ModBlocks.MYSTIC_FENCE.get());
                pOutput.accept(ModBlocks.MYSTIC_FENCE_GATE.get());
                pOutput.accept(ModBlocks.MYSTIC_DOOR.get());
                pOutput.accept(ModBlocks.MYSTIC_TRAPDOOR.get());
                pOutput.accept(ModBlocks.MYSTIC_LOG.get());
                pOutput.accept(ModBlocks.MYSTIC_WOOD.get());
                pOutput.accept(ModBlocks.STRIPED_MYSTIC_LOG.get());
                pOutput.accept(ModBlocks.STRIPED_MYSTIC_WOOD.get());
                pOutput.accept(ModBlocks.MYSTIC_SAPLING.get());
                pOutput.accept(ModBlocks.MYSTIC_LEAVES.get());
                pOutput.accept(ModBlocks.MYSTIC_SIGN.get());
                pOutput.accept(ModBlocks.MYSTIC_HANGING_SIGN.get());
                // Sky Wood
                pOutput.accept(ModBlocks.SKY_PLANKS.get());
                pOutput.accept(ModBlocks.SKY_STAIRS.get());
                pOutput.accept(ModBlocks.SKY_SLAB.get());
                pOutput.accept(ModBlocks.SKY_BUTTON.get());
                pOutput.accept(ModBlocks.SKY_PRESSURE_PLATE.get());
                pOutput.accept(ModBlocks.SKY_FENCE.get());
                pOutput.accept(ModBlocks.SKY_FENCE_GATE.get());
                pOutput.accept(ModBlocks.SKY_DOOR.get());
                pOutput.accept(ModBlocks.SKY_TRAPDOOR.get());
                pOutput.accept(ModBlocks.SKY_LOG.get());
                pOutput.accept(ModBlocks.SKY_WOOD.get());
                pOutput.accept(ModBlocks.STRIPED_SKY_LOG.get());
                pOutput.accept(ModBlocks.STRIPED_SKY_WOOD.get());
                pOutput.accept(ModBlocks.SKY_SAPLING.get());
                pOutput.accept(ModBlocks.SKY_LEAVES.get());
                pOutput.accept(ModBlocks.SKY_SIGN.get());
                pOutput.accept(ModBlocks.SKY_HANGING_SIGN.get());
                // Palm Wood
                pOutput.accept(ModBlocks.PALM_PLANKS.get());
                pOutput.accept(ModBlocks.PALM_STAIRS.get());
                pOutput.accept(ModBlocks.PALM_SLAB.get());
                pOutput.accept(ModBlocks.PALM_BUTTON.get());
                pOutput.accept(ModBlocks.PALM_PRESSURE_PLATE.get());
                pOutput.accept(ModBlocks.PALM_FENCE.get());
                pOutput.accept(ModBlocks.PALM_FENCE_GATE.get());
                pOutput.accept(ModBlocks.PALM_DOOR.get());
                pOutput.accept(ModBlocks.PALM_TRAPDOOR.get());
                pOutput.accept(ModBlocks.PALM_LOG.get());
                pOutput.accept(ModBlocks.PALM_WOOD.get());
                pOutput.accept(ModBlocks.STRIPED_PALM_LOG.get());
                pOutput.accept(ModBlocks.STRIPED_PALM_WOOD.get());
                pOutput.accept(ModBlocks.PALM_SAPLING.get());
                pOutput.accept(ModBlocks.PALM_LEAVES.get());
                pOutput.accept(ModBlocks.PALM_SIGN.get());
                pOutput.accept(ModBlocks.PALM_HANGING_SIGN.get());
                // Gilded Sky Wood
                pOutput.accept(ModBlocks.GILDED_SKY_PLANKS.get());
                pOutput.accept(ModBlocks.GILDED_SKY_STAIRS.get());
                pOutput.accept(ModBlocks.GILDED_SKY_SLAB.get());
                pOutput.accept(ModBlocks.GILDED_SKY_BUTTON.get());
                pOutput.accept(ModBlocks.GILDED_SKY_PRESSURE_PLATE.get());
                pOutput.accept(ModBlocks.GILDED_SKY_FENCE.get());
                pOutput.accept(ModBlocks.GILDED_SKY_FENCE_GATE.get());
                pOutput.accept(ModBlocks.GILDED_SKY_DOOR.get());
                pOutput.accept(ModBlocks.GILDED_SKY_TRAPDOOR.get());
                pOutput.accept(ModBlocks.GILDED_SKY_LOG.get());
                pOutput.accept(ModBlocks.GILDED_SKY_WOOD.get());
                pOutput.accept(ModBlocks.GILDED_SKY_SIGN.get());
                pOutput.accept(ModBlocks.GILDED_SKY_HANGING_SIGN.get());
                // Mushrooms
                pOutput.accept(ModBlocks.BLACK_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.BLUE_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.CYAN_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.GREEN_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.ORANGE_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.PURPLE_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.WHITE_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.YELLOW_MUSHROOM_BLOCK.get());
                pOutput.accept(ModBlocks.GLOW_MUSHROOM_BLOCK.get());
                // Spawn Eggs
                pOutput.accept(ModItems.PUCKOO_SPAWN_EGG.get());
                pOutput.accept(ModItems.WORM_SPAWN_EGG.get());
                pOutput.accept(ModItems.TREEFROG_SPAWN_EGG.get());
                pOutput.accept(ModItems.HOPPLESHROOM_SPAWN_EGG.get());
                pOutput.accept(ModItems.GIANT_TORTOISE_SPAWN_EGG.get());
                pOutput.accept(ModItems.JELLYFISH_SPAWN_EGG.get());
                pOutput.accept(ModItems.PIRANHA_SPAWN_EGG.get());
                pOutput.accept(ModItems.HARPY_SPAWN_EGG.get());


            })
            .build());
    public static void register() {
        CREATIVE_MODE_TAB.register();
    }
}
