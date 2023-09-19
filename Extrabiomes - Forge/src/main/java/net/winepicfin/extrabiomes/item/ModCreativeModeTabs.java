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
    public static final RegistryObject<CreativeModeTab> EXTRABIOMES_TAB = CREATIVE_MODE_TAB.register("extrabiomes_tab",()-> CreativeModeTab.builder().icon(()->new ItemStack(ModItems.mossy_pebble.get()))
            .title(Component.translatable("creativetab.extrabiomes"))
            .displayItems((pParameters, pOutput)->{
                pOutput.accept(ModItems.pebble.get());
                pOutput.accept(ModItems.mossy_pebble.get());
                pOutput.accept(ModItems.RAZOR_FEATHER.get());
                pOutput.accept(ModBlocks.DENSE_CLOUD.get());
                pOutput.accept(ModBlocks.DENSE_CLOUD_BRICK.get());
                pOutput.accept(ModBlocks.NETHER_DIAMOND_ORE.get());

            })
            .build());
    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
