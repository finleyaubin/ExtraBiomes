package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.LinkedHashMap;

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
        trimmedArmorItem(ModItems.FROG_HELMET);

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
        //~~~~~~~~~~~~~Gilded Sky Wood~~~~~~~~~~\\
        simpleBlockItem(ModBlocks.GILDED_SKY_DOOR);
        fenceItem(ModBlocks.GILDED_SKY_FENCE, ModBlocks.GILDED_SKY_PLANKS);
        buttonItem(ModBlocks.GILDED_SKY_BUTTON, ModBlocks.GILDED_SKY_PLANKS);
        trapdoorItem(ModBlocks.GILDED_SKY_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_STAIRS);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_SLAB);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_FENCE_GATE);


    }
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }
    // Shoutout to El_Redstoniano for making this
    private void trimmedArmorItem(RegistryObject<Item> itemRegistryObject) {
        final String MOD_ID = ExtraBiomes.MOD_ID; // Change this to your mod id

        if(itemRegistryObject.get() instanceof ArmorItem armorItem) {
            trimMaterials.entrySet().forEach(entry -> {

                ResourceKey<TrimMaterial> trimMaterial = entry.getKey();
                float trimValue = entry.getValue();

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = "item/" + armorItem;
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = new ResourceLocation(MOD_ID, armorItemPath);
                ResourceLocation trimResLoc = new ResourceLocation(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = new ResourceLocation(MOD_ID, currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc)
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)

                this.withExistingParent(itemRegistryObject.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                new ResourceLocation(MOD_ID,
                                        "item/" + itemRegistryObject.getId().getPath()));
            });
        }
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
