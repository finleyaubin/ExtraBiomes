package net.winepicfin.extrabiomes.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExtraBiomes.MOD_ID);
    public  static final RegistryObject<Item> pebble = ITEMS.register("Pebble",()-> new Item(new Item.Properties()));
    public static void  register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
