package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import dev.architectury.registry.registries.RegistrySupplier;
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
        simpleItem(ModItems.PEBBLE);
        simpleItem(ModItems.MOSSY_PEBBLE);
        simpleItem(ModItems.COOKED_FROGS_LEGS);
        simpleItem(ModItems.FROGS_LEGS);
        simpleItem(ModItems.RAZOR_FEATHER);
        simpleItem(ModItems.DIAMOND_RAZOR_FEATHER);
        simpleItem(ModItems.NETHERITE_RAZOR_FEATHER);
        simpleItem(ModItems.PIRANHA);
        simpleItem(ModItems.COOKED_PIRANHA);
        simpleItem(ModItems.WORM);
        simpleItem(ModItems.BAIT);
        simpleItem(ModItems.JELLYFISH_JAM_BOTTLE);
        // Small Mushrooms
        saplingItem(ModBlocks.BLACK_MUSHROOM);
        saplingItem(ModBlocks.BLUE_MUSHROOM);
        saplingItem(ModBlocks.CYAN_MUSHROOM);
        saplingItem(ModBlocks.GREEN_MUSHROOM);
        saplingItem(ModBlocks.ORANGE_MUSHROOM);
        saplingItem(ModBlocks.PURPLE_MUSHROOM);
        saplingItem(ModBlocks.WHITE_MUSHROOM);
        saplingItem(ModBlocks.YELLOW_MUSHROOM);
        saplingItem(ModBlocks.GLOW_MUSHROOM);
        simpleItem(ModItems.JELLYFISHING_NET_EMPTY);
        simpleItem(ModItems.JELLYFISHING_NET_FULL);
        simpleItem(ModItems.BUCKET_OF_GOO);
        // Boat items - see ModItems.BOAT_MODEL_ENTRIES (common) for which wood type uses which texture.
        ModItems.BOAT_MODEL_ENTRIES.forEach(entry -> boatItem(entry.item(), entry.texture()));
        trimmedArmorItem(ModItems.FROG_HELMET);
        evenSimplerBlockItem(ModBlocks.DENSE_CLOUD_BRICK_STAIRS);
        evenSimplerBlockItem(ModBlocks.DENSE_CLOUD_BRICK_SLAB);

        // Black Sand
        evenSimplerBlockItem(ModBlocks.BLACK_SANDSTONE_STAIRS);
        evenSimplerBlockItem(ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS);
        evenSimplerBlockItem(ModBlocks.BLACK_SANDSTONE_SLAB);
        evenSimplerBlockItem(ModBlocks.CUT_BLACK_SANDSTONE_SLAB);
        evenSimplerBlockItem(ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB);
        withExistingParent(BuiltInRegistries.BLOCK.getKey(ModBlocks.BLACK_SANDSTONE_WALL.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "block/black_sandstone"));

        // Mystic Wood
        simpleBlockItem(ModBlocks.MYSTIC_DOOR);
        fenceItem(ModBlocks.MYSTIC_FENCE, ModBlocks.MYSTIC_PLANKS);
        buttonItem(ModBlocks.MYSTIC_BUTTON, ModBlocks.MYSTIC_PLANKS);
        trapdoorItem(ModBlocks.MYSTIC_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.MYSTIC_STAIRS);
        evenSimplerBlockItem(ModBlocks.MYSTIC_SLAB);
        evenSimplerBlockItem(ModBlocks.MYSTIC_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.MYSTIC_FENCE_GATE);
        saplingItem(ModBlocks.MYSTIC_SAPLING);
        simpleItem(ModItems.MYSTIC_SIGN);
        simpleItem(ModItems.MYSTIC_HANGING_SIGN);
        // Sky Wood
        simpleBlockItem(ModBlocks.SKY_DOOR);
        fenceItem(ModBlocks.SKY_FENCE, ModBlocks.SKY_PLANKS);
        buttonItem(ModBlocks.SKY_BUTTON, ModBlocks.SKY_PLANKS);
        trapdoorItem(ModBlocks.SKY_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.SKY_STAIRS);
        evenSimplerBlockItem(ModBlocks.SKY_SLAB);
        evenSimplerBlockItem(ModBlocks.SKY_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.SKY_FENCE_GATE);
        saplingItem(ModBlocks.SKY_SAPLING);
        simpleItem(ModItems.SKY_SIGN);
        simpleItem(ModItems.SKY_HANGING_SIGN);
        // Palm Wood
        simpleBlockItem(ModBlocks.PALM_DOOR);
        fenceItem(ModBlocks.PALM_FENCE, ModBlocks.PALM_PLANKS);
        buttonItem(ModBlocks.PALM_BUTTON, ModBlocks.PALM_PLANKS);
        trapdoorItem(ModBlocks.PALM_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.PALM_STAIRS);
        evenSimplerBlockItem(ModBlocks.PALM_SLAB);
        evenSimplerBlockItem(ModBlocks.PALM_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.PALM_FENCE_GATE);
        saplingItem(ModBlocks.PALM_SAPLING);
        simpleItem(ModItems.PALM_SIGN);
        simpleItem(ModItems.PALM_HANGING_SIGN);
        // Gilded Sky Wood
        simpleBlockItem(ModBlocks.GILDED_SKY_DOOR);
        fenceItem(ModBlocks.GILDED_SKY_FENCE, ModBlocks.GILDED_SKY_PLANKS);
        buttonItem(ModBlocks.GILDED_SKY_BUTTON, ModBlocks.GILDED_SKY_PLANKS);
        trapdoorItem(ModBlocks.GILDED_SKY_TRAPDOOR);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_STAIRS);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_SLAB);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.GILDED_SKY_FENCE_GATE);
        simpleItem(ModItems.GILDED_SKY_SIGN);
        simpleItem(ModItems.GILDED_SKY_HANGING_SIGN);
        // Spawn Eggs
        withExistingParent(ModItems.PUCKOO_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.WORM_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.TREEFROG_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HOPPLESHROOM_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.GIANT_TORTOISE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.JELLYFISH_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PIRANHA_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HARPY_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));



    }
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
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
    private void trimmedArmorItem(RegistrySupplier<Item> itemRegistryObject) {
        if(itemRegistryObject.get() instanceof ArmorItem armorItem) {
            trimMaterials.entrySet().forEach(entry -> {

                ResourceKey<TrimMaterial> trimMaterial = entry.getKey();
                float trimValue = entry.getValue();

                Equippable equippable = itemRegistryObject.get().getDefaultInstance().get(DataComponents.EQUIPPABLE);
                EquipmentSlot equipmentSlot = equippable != null ? equippable.slot() : EquipmentSlot.HEAD;
                String armorType = switch (equipmentSlot) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                // Item#toString() returns its full "modid:path" registry name (forge's older
                // toString happened not to on this item, letting "item/" + armorItem work there,
                // but that's not something to rely on) - use the RegistrySupplier's own id instead.
                String armorItemPath = "item/" + itemRegistryObject.getId().getPath();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.withDefaultNamespace(trimPath);
                ResourceLocation trimNameResLoc = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, currentTrimName);

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
                                ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,
                                        "item/" + itemRegistryObject.getId().getPath()));
            });
        }
    }

    private ItemModelBuilder simpleItem(RegistrySupplier<Item> item){
        return withExistingParent(item.getId().getPath(),
            ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"item/" + item.getId().getPath()));
    }

    // Unlike simpleItem(), the texture stem is passed explicitly rather than derived from the item's
    // own registry path - boat items are named "<wood>_boat" (matching this mod's other wood items),
    // but the pre-staged art (ported from the Bedrock module) is named "boat_<wood>", so the two don't
    // match by convention.
    private ItemModelBuilder boatItem(RegistrySupplier<Item> item, String texture) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + texture));
    }
    public void evenSimplerBlockItem(RegistrySupplier<Block> block) {
        this.withExistingParent(ExtraBiomes.MOD_ID + ":" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath(),
                modLoc("block/" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath()));
    }

    public void trapdoorItem(RegistrySupplier<Block> block) {
        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(block.get()).getPath(),
                modLoc("block/" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistrySupplier<Block> block, RegistrySupplier<Block> baseBlock) {
        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "block/" + BuiltInRegistries.BLOCK.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistrySupplier<Block> block, RegistrySupplier<Block> baseBlock) {
        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "block/" + BuiltInRegistries.BLOCK.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistrySupplier<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder saplingItem(RegistrySupplier<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"block/" + item.getId().getPath()));
    }
}
