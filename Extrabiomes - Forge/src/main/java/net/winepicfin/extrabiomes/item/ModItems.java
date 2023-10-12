package net.winepicfin.extrabiomes.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.fluid.ModFluids;
import net.winepicfin.extrabiomes.item.custom.RazorFeatherItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExtraBiomes.MOD_ID);
    public  static final RegistryObject<Item> pebble = ITEMS.register("pebble",()-> new Item(new Item.Properties()));
    public  static final RegistryObject<Item> mossy_pebble = ITEMS.register("mossy_pebble",()-> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BUCKET_OF_GOO = ITEMS.register("bucket_of_goo",()-> new BucketItem(ModFluids.SOURCE_GOO, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> RAZOR_FEATHER = ITEMS.register("razor_feather",()-> new RazorFeatherItem(new Item.Properties()));
    public static final RegistryObject<Item> FROGS_LEGS = ITEMS.register("frogs_legs",()-> new Item(new Item.Properties().food(ModFoods.FROGS_LEGS)));
    public static final RegistryObject<Item> COOKED_FROGS_LEGS = ITEMS.register("cooked_frogs_legs",()-> new Item(new Item.Properties().food(ModFoods.COOKED_FROGS_LEGS)));
    public static final RegistryObject<Item> FROG_HELMET = ITEMS.register("frog_helmet",()-> new ArmorItem(ModItemMaterials.FROG, ArmorItem.Type.HELMET, new Item.Properties()));

    public static void  register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
