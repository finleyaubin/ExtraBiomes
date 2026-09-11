package net.winepicfin.extrabiomes.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

// NeoForge 21.4 removed its own ItemModelProvider/ItemModelBuilder/ExistingFileHelper convenience
// API - see ModBlockStateProvider's header comment for the confirming jar diff. This is a port of
// fabric/'s ModItemModelProvider (already built on plain vanilla DataProvider + hand-written JSON
// for the same reason) onto this module's item list.
public class ModItemModelProvider implements DataProvider {
    private final PackOutput.PathProvider modelPathProvider;
    // 1.21.4 added a required indirection layer: an item no longer resolves its rendered model
    // directly from models/item/<id>.json by convention - it now needs an explicit
    // assets/<ns>/items/<id>.json "client item" file pointing at that model (introduced in snapshot
    // 24w45a, see minecraft.wiki/w/Items_model_definition). There's no fallback deriving one from
    // the old models/item/ path anymore, so every item registered below needs a matching entry here
    // via clientItem(), or it silently renders as a missing/no-model item with zero warning at
    // datagen time - only a runtime "No model loaded for default item ID" warning gives it away.
    private final PackOutput.PathProvider itemPathProvider;
    private final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
    private final Map<ResourceLocation, Supplier<JsonElement>> items = new HashMap<>();

    public ModItemModelProvider(PackOutput output) {
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        this.itemPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        registerModels();
    }

    // Registers the assets/<ns>/items/<itemId>.json wrapper pointing at an already-registered
    // models/item/<modelId>.json - the simple "minecraft:model" case that covers every item here
    // except the trimmed armor's per-trim selection (see trimmedArmorItem, which builds its own
    // "minecraft:select" items/ entry directly instead of calling this).
    private void clientItem(ResourceLocation itemId, ResourceLocation modelId) {
        items.put(itemId, () -> {
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", modelId.toString());
            JsonObject json = new JsonObject();
            json.add("model", model);
            return json;
        });
    }

    private void registerModels() {
        simpleItem(ModItems.PEBBLE.get());
        simpleItem(ModItems.MOSSY_PEBBLE.get());
        simpleItem(ModItems.COOKED_FROGS_LEGS.get());
        simpleItem(ModItems.FROGS_LEGS.get());
        simpleItem(ModItems.RAZOR_FEATHER.get());
        simpleItem(ModItems.DIAMOND_RAZOR_FEATHER.get());
        simpleItem(ModItems.NETHERITE_RAZOR_FEATHER.get());
        simpleItem(ModItems.PIRANHA.get());
        simpleItem(ModItems.COOKED_PIRANHA.get());
        simpleItem(ModItems.WORM.get());
        simpleItem(ModItems.BAIT.get());
        simpleItem(ModItems.JELLYFISH_JAM_BOTTLE.get());
        simpleItem(ModItems.JELLYFISHING_NET_EMPTY.get());
        simpleItem(ModItems.JELLYFISHING_NET_FULL.get());
        simpleItem(ModItems.BUCKET_OF_GOO.get());
        trimmedArmorItem(ModItems.FROG_HELMET.get());

        // Sign / hanging sign items have their own flat inventory icon, separate from the in-world
        // block model (which is block-entity-rendered) that ModBlockStateProvider wrote.
        simpleItem(ModItems.MYSTIC_SIGN.get());
        simpleItem(ModItems.MYSTIC_HANGING_SIGN.get());
        simpleItem(ModItems.SKY_SIGN.get());
        simpleItem(ModItems.SKY_HANGING_SIGN.get());
        simpleItem(ModItems.PALM_SIGN.get());
        simpleItem(ModItems.PALM_HANGING_SIGN.get());
        simpleItem(ModItems.GILDED_SKY_SIGN.get());
        simpleItem(ModItems.GILDED_SKY_HANGING_SIGN.get());

        // Doors delegate to their block model instead (see ModBlockStateProvider.doorBlockState).
        simpleItem(ModBlocks.MYSTIC_DOOR.get().asItem());
        simpleItem(ModBlocks.SKY_DOOR.get().asItem());
        simpleItem(ModBlocks.PALM_DOOR.get().asItem());
        simpleItem(ModBlocks.GILDED_SKY_DOOR.get().asItem());

        // Black Sandstone Wall - "wall_inventory" parent needs an explicit item entry (walls, unlike
        // most blocks, use a dedicated inventory-only model rather than reusing a placed-block model).
        withExistingParent(ModBlocks.BLACK_SANDSTONE_WALL.getId().getPath(), "minecraft:block/wall_inventory")
                .add("wall", ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "block/black_sandstone").toString());

        // Boat items - see ModItems.BOAT_MODEL_ENTRIES (common) for which wood type uses which texture.
        ModItems.BOAT_MODEL_ENTRIES.forEach(entry -> boatItem(entry.item().get(), entry.texture()));

        // Spawn Eggs. Colors must match ModItems' registration calls - vanilla no longer tints
        // these via the Item/ItemColor classes, only via this "tints" array (see spawnEgg()).
        spawnEgg(ModItems.PUCKOO_SPAWN_EGG.get(), 0xffffff, 0xea7630);
        spawnEgg(ModItems.WORM_SPAWN_EGG.get(), 0xff81d9, 0xff4343);
        spawnEgg(ModItems.TREEFROG_SPAWN_EGG.get(), 0x329b17, 0x034722);
        spawnEgg(ModItems.HOPPLESHROOM_SPAWN_EGG.get(), 0x9b1717, 0xfdd8d8);
        spawnEgg(ModItems.GIANT_TORTOISE_SPAWN_EGG.get(), 0x364710, 0xa66643);
        spawnEgg(ModItems.JELLYFISH_SPAWN_EGG.get(), 0x932a9e, 0xdc7ce6);
        spawnEgg(ModItems.PIRANHA_SPAWN_EGG.get(), 0x444444, 0x251515);
        spawnEgg(ModItems.HARPY_SPAWN_EGG.get(), 0x2319af, 0xe9c600);
    }

    private void simpleItem(Item item) {
        ResourceLocation id = ModelLocationUtils.getModelLocation(item);
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + BuiltInRegistries.ITEM.getKey(item).getPath());
        models.put(id, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", texture.toString());
            json.add("textures", textures);
            return json;
        });
        clientItem(BuiltInRegistries.ITEM.getKey(item), id);
    }

    // Unlike simpleItem(), the texture stem is passed explicitly rather than derived from the item's
    // own registry path - boat items are named "<wood>_boat" (matching this mod's other wood items),
    // but the pre-staged art (ported from the Bedrock module) is named "boat_<wood>".
    private void boatItem(Item item, String texture) {
        ResourceLocation id = ModelLocationUtils.getModelLocation(item);
        ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + texture);
        models.put(id, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", textureLocation.toString());
            json.add("textures", textures);
            return json;
        });
        clientItem(BuiltInRegistries.ITEM.getKey(item), id);
    }

    // Spawn egg color is no longer an Item-level ItemColor tint in 1.21.4 - it's a "tints" array
    // baked directly into the items/*.json client item, same as vanilla's own egg items (see
    // e.g. assets/minecraft/items/creeper_spawn_egg.json). No separate models/item/*.json needed,
    // vanilla's own template_spawn_egg model is referenced directly.
    private void spawnEgg(Item item, int backgroundColor, int highlightColor) {
        items.put(BuiltInRegistries.ITEM.getKey(item), () -> {
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", "minecraft:item/template_spawn_egg");
            JsonArray tints = new JsonArray();
            tints.add(tint(backgroundColor));
            tints.add(tint(highlightColor));
            model.add("tints", tints);
            JsonObject json = new JsonObject();
            json.add("model", model);
            return json;
        });
    }

    private static JsonObject tint(int rgb) {
        JsonObject tint = new JsonObject();
        tint.addProperty("type", "minecraft:constant");
        tint.addProperty("value", 0xFF000000 | rgb);
        return tint;
    }

    private ItemModelBuilder withExistingParent(String path, String parent) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + path);
        ItemModelBuilder builder = new ItemModelBuilder(parent);
        models.put(id, builder::build);
        clientItem(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, path), id);
        return builder;
    }

    private static class ItemModelBuilder {
        private final String parent;
        private final Map<String, String> textures = new LinkedHashMap<>();

        private ItemModelBuilder(String parent) {
            this.parent = parent;
        }

        ItemModelBuilder add(String slot, String texture) {
            textures.put(slot, texture);
            return this;
        }

        JsonElement build() {
            JsonObject json = new JsonObject();
            json.addProperty("parent", parent);
            if (!textures.isEmpty()) {
                JsonObject textureJson = new JsonObject();
                textures.forEach(textureJson::addProperty);
                json.add("textures", textureJson);
            }
            return json;
        }
    }

    // Shoutout to El_Redstoniano for making this: one override entry per vanilla trim material, each
    // pointing at a small generated "<item>_<trim>_trim" model (layer0 = the helmet's own texture,
    // layer1 = that trim material's armor trim texture), keyed by the "trim_type" item-model
    // predicate value used by vanilla's own trimmed armor items.
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = new LinkedHashMap<>();

    static {
        TRIM_MATERIALS.put(TrimMaterials.QUARTZ, 0.1F);
        TRIM_MATERIALS.put(TrimMaterials.IRON, 0.2F);
        TRIM_MATERIALS.put(TrimMaterials.NETHERITE, 0.3F);
        TRIM_MATERIALS.put(TrimMaterials.REDSTONE, 0.4F);
        TRIM_MATERIALS.put(TrimMaterials.COPPER, 0.5F);
        TRIM_MATERIALS.put(TrimMaterials.GOLD, 0.6F);
        TRIM_MATERIALS.put(TrimMaterials.EMERALD, 0.7F);
        TRIM_MATERIALS.put(TrimMaterials.DIAMOND, 0.8F);
        TRIM_MATERIALS.put(TrimMaterials.LAPIS, 0.9F);
        TRIM_MATERIALS.put(TrimMaterials.AMETHYST, 1.0F);
    }

    private void trimmedArmorItem(Item item) {
        if (!(item instanceof ArmorItem)) return;
        Equippable equippable = item.getDefaultInstance().get(DataComponents.EQUIPPABLE);
        EquipmentSlot equipmentSlot = equippable != null ? equippable.slot() : EquipmentSlot.HEAD;
        String armorType = switch (equipmentSlot) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> "";
        };
        String itemPath = BuiltInRegistries.ITEM.getKey(item).getPath();
        ResourceLocation itemTexture = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + itemPath);

        List<Map.Entry<ResourceKey<TrimMaterial>, Float>> entries = List.copyOf(TRIM_MATERIALS.entrySet());
        ResourceLocation baseId = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + itemPath);

        models.put(baseId, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", itemTexture.toString());
            json.add("textures", textures);
            return json;
        });

        // 1.21.4 moved trim-material selection out of the model file's legacy "overrides" predicate
        // array (which the new item-model resolver no longer reads) and into the items/*.json client
        // item itself via a "minecraft:select" node keyed on the "minecraft:trim_material" property -
        // same shape vanilla's own trimmed armor (e.g. iron_helmet.json) uses. "when" takes the trim
        // material's own resource location, not the old float priority value.
        items.put(BuiltInRegistries.ITEM.getKey(item), () -> {
            JsonArray cases = new JsonArray();
            for (Map.Entry<ResourceKey<TrimMaterial>, Float> entry : entries) {
                String trimName = entry.getKey().location().getPath();
                String modelName = itemPath + "_" + trimName + "_trim";
                JsonObject caseModel = new JsonObject();
                caseModel.addProperty("type", "minecraft:model");
                caseModel.addProperty("model", ExtraBiomes.MOD_ID + ":item/" + modelName);
                JsonObject caseEntry = new JsonObject();
                caseEntry.add("model", caseModel);
                caseEntry.addProperty("when", entry.getKey().location().toString());
                cases.add(caseEntry);
            }
            JsonObject fallback = new JsonObject();
            fallback.addProperty("type", "minecraft:model");
            fallback.addProperty("model", baseId.toString());
            JsonObject select = new JsonObject();
            select.addProperty("type", "minecraft:select");
            select.addProperty("property", "minecraft:trim_material");
            select.add("cases", cases);
            select.add("fallback", fallback);
            JsonObject json = new JsonObject();
            json.add("model", select);
            return json;
        });

        for (Map.Entry<ResourceKey<TrimMaterial>, Float> entry : entries) {
            String trimName = entry.getKey().location().getPath();
            String modelName = itemPath + "_" + trimName + "_trim";
            ResourceLocation trimModelId = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + modelName);
            ResourceLocation trimTexture = ResourceLocation.fromNamespaceAndPath("minecraft", "trims/items/" + armorType + "_trim_" + trimName);
            models.put(trimModelId, () -> {
                JsonObject json = new JsonObject();
                json.addProperty("parent", "minecraft:item/generated");
                JsonObject textures = new JsonObject();
                textures.addProperty("layer0", itemTexture.toString());
                textures.addProperty("layer1", trimTexture.toString());
                json.add("textures", textures);
                return json;
            });
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        models.forEach((id, supplier) -> futures.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(id))));
        items.forEach((id, supplier) -> futures.add(DataProvider.saveStable(cache, supplier.get(), itemPathProvider.json(id))));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Item Models";
    }
}
