package net.winepicfin.extrabiomes.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtraBiomes.MOD_ID);
    public static final RegistryObject<CreativeModeTab> EXTRABIOMES_TAB = CREATIVE_MODE_TAB.register("extrabiomes_tab",()-> CreativeModeTab.builder().icon(()->new ItemStack(ModBlocks.MYSTIC_SAPLING.get()))
            .title(Component.translatable("creativetab.extrabiomes"))
            .displayItems((pParameters, pOutput)->{
                //~~~~~~~~~~~~~Items~~~~~~~~~~~~\\
                pOutput.accept(ModItems.pebble.get());
                pOutput.accept(ModItems.mossy_pebble.get());
                pOutput.accept(ModItems.RAZOR_FEATHER.get());
                pOutput.accept(ModItems.FROGS_LEGS.get());
                pOutput.accept(ModItems.COOKED_FROGS_LEGS.get());
                pOutput.accept(ModItems.BUCKET_OF_GOO.get());
                //~~~~~~~~~~~~~Blocks~~~~~~~~~~~~\\
                pOutput.accept(ModBlocks.DENSE_CLOUD.get());
                pOutput.accept(ModBlocks.DENSE_CLOUD_BRICK.get());
                pOutput.accept(ModBlocks.NETHER_DIAMOND_ORE.get());
                //~~~~~~~~~~~~~Mystic Wood~~~~~~~~~~~~\\
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
                //~~~~~~~~~~~~~Sky Wood~~~~~~~~~~~~\\
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
                //~~~~~~~~~~~~~Palm Wood~~~~~~~~~~~~\\
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

            })
            .build());
    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
