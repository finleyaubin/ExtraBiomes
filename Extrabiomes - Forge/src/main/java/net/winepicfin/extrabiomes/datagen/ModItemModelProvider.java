package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExtraBiomes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.pebble);
        simpleItem(ModItems.mossy_pebble);
        simpleItem(ModItems.COOKED_FROGS_LEGS);
        simpleItem(ModItems.FROGS_LEGS);
        simpleItem(ModItems.RAZOR_FEATHER);
        simpleItem(ModItems.BUCKET_OF_GOO);

        //~~~~~~~~~~~~~Mystic Wood~~~~~~~~~~\\
        simpleBlockItem(ModBlocks.MYSTIC_DOOR);
        fenceItem(ModBlocks.MYSTIC_FENCE, ModBlocks.MYSTIC_PLANKS);
        buttonItem(ModBlocks.MYSTIC_BUTTON, ModBlocks.MYSTIC_PLANKS);
        trapdoorItem(ModBlocks.MYSTIC_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.MYSTIC_STAIRS);
        evenSimplerBlockItem(ModBlocks.MYSTIC_SLAB);
        evenSimplerBlockItem(ModBlocks.MYSTIC_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.MYSTIC_FENCE_GATE);
        saplingItem(ModBlocks.MYSTIC_SAPLING);
        //~~~~~~~~~~~~~Sky Wood~~~~~~~~~~\\
        simpleBlockItem(ModBlocks.SKY_DOOR);
        fenceItem(ModBlocks.SKY_FENCE, ModBlocks.SKY_PLANKS);
        buttonItem(ModBlocks.SKY_BUTTON, ModBlocks.SKY_PLANKS);
        trapdoorItem(ModBlocks.SKY_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.SKY_STAIRS);
        evenSimplerBlockItem(ModBlocks.SKY_SLAB);
        evenSimplerBlockItem(ModBlocks.SKY_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.SKY_FENCE_GATE);
        saplingItem(ModBlocks.SKY_SAPLING);
        //~~~~~~~~~~~~~Palm Wood~~~~~~~~~~\\
        simpleBlockItem(ModBlocks.PALM_DOOR);
        fenceItem(ModBlocks.PALM_FENCE, ModBlocks.PALM_PLANKS);
        buttonItem(ModBlocks.PALM_BUTTON, ModBlocks.PALM_PLANKS);
        trapdoorItem(ModBlocks.PALM_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.PALM_STAIRS);
        evenSimplerBlockItem(ModBlocks.PALM_SLAB);
        evenSimplerBlockItem(ModBlocks.PALM_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.PALM_FENCE_GATE);
        saplingItem(ModBlocks.PALM_SAPLING);


    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(ExtraBiomes.MOD_ID,"item/" + item.getId().getPath()));
    }
    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(ExtraBiomes.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(ExtraBiomes.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(ExtraBiomes.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(ExtraBiomes.MOD_ID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder saplingItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(ExtraBiomes.MOD_ID,"block/" + item.getId().getPath()));
    }
}
