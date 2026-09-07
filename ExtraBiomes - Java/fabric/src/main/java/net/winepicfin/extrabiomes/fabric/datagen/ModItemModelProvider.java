package net.winepicfin.extrabiomes.fabric.datagen;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.Map;

// Fabric port of forge/datagen/ModItemModelProvider.java. Every item that's really just the item-form
// of a block (stairs, slabs, fences, buttons, plates, gates, trapdoors, saplings, cube-shaped blocks,
// etc) already gets its item model auto-derived by ModBlockStateProvider (mirroring how Forge's own
// simpleBlockWithItem/stairsBlock/etc block-state helpers already covered those, leaving Forge's own
// ModItemModelProvider.registerModels() to only list genuinely standalone items). This class only
// covers that same standalone-item subset: hand items with their own texture, sign/hanging-sign
// inventory icons (separate from the in-world block-entity-rendered model), the trimmed frog helmet,
// spawn eggs, and the black sandstone wall's item icon.
public class ModItemModelProvider implements DataProvider {
    private final PackOutput.PathProvider modelPathProvider;
    private final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();

    public ModItemModelProvider(net.fabricmc.fabric.api.datagen.v1.FabricDataOutput output) {
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        registerModels();
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
        // block model (which is rendered by a block entity renderer) that ModBlockStateProvider wrote.
        simpleItem(ModItems.MYSTIC_SIGN.get());
        simpleItem(ModItems.MYSTIC_HANGING_SIGN.get());
        simpleItem(ModItems.SKY_SIGN.get());
        simpleItem(ModItems.SKY_HANGING_SIGN.get());
        simpleItem(ModItems.PALM_SIGN.get());
        simpleItem(ModItems.PALM_HANGING_SIGN.get());
        simpleItem(ModItems.GILDED_SKY_SIGN.get());
        simpleItem(ModItems.GILDED_SKY_HANGING_SIGN.get());

        // Trapdoors delegate to their block model instead (see ModBlockStateProvider.trapdoorBlockState).
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

        // Spawn Eggs
        spawnEgg(ModItems.PUCKOO_SPAWN_EGG.get());
        spawnEgg(ModItems.WORM_SPAWN_EGG.get());
        spawnEgg(ModItems.TREEFROG_SPAWN_EGG.get());
        spawnEgg(ModItems.HOPPLESHROOM_SPAWN_EGG.get());
        spawnEgg(ModItems.GIANT_TORTOISE_SPAWN_EGG.get());
        spawnEgg(ModItems.JELLYFISH_SPAWN_EGG.get());
        spawnEgg(ModItems.PIRANHA_SPAWN_EGG.get());
        spawnEgg(ModItems.HARPY_SPAWN_EGG.get());
    }

    private void simpleItem(Item item) {
        ResourceLocation id = ModelLocationUtils.getModelLocation(item);
        // decorateItemModelLocation(path) defaults to the "minecraft" namespace, not this mod's - producing missing textures.
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + BuiltInRegistries.ITEM.getKey(item).getPath());
        models.put(id, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", texture.toString());
            json.add("textures", textures);
            return json;
        });
    }

    // Unlike simpleItem(), the texture stem is passed explicitly rather than derived from the item's
    // own registry path - boat items are named "<wood>_boat" (matching this mod's other wood items),
    // but the pre-staged art (ported from the Bedrock module) is named "boat_<wood>", so the two don't
    // match by convention.
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
    }

    private void spawnEgg(Item item) {
        withExistingParent(BuiltInRegistries.ITEM.getKey(item).getPath(), "minecraft:item/template_spawn_egg");
    }

    // Minimal withExistingParent-style builder: registers a model whose only content is "parent" plus
    // whatever textures the caller adds.
    private ItemModelBuilder withExistingParent(String path, String parent) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + path);
        ItemModelBuilder builder = new ItemModelBuilder(parent);
        models.put(id, builder::build);
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

    // Ports Forge's El_Redstoniano-credited trim-override generator for the frog helmet: one override
    // entry per vanilla trim material, each pointing at a small generated "<item>_<trim>_trim" model
    // (layer0 = the helmet's own texture, layer1 = that trim material's armor trim texture), keyed by
    // the "trim_type" item-model predicate value used by vanilla's own trimmed armor items.
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
        ItemModelBuilder base = new ItemModelBuilder("minecraft:item/generated").add("layer0", itemTexture.toString());
        ResourceLocation baseId = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "item/" + itemPath);

        models.put(baseId, () -> {
            JsonObject json = base.build().getAsJsonObject();
            JsonObject overrides = new JsonObject();
            // Vanilla's override list format ({"predicate": {...}, "model": "..."}) needs a raw array,
            // built directly here since ItemModelBuilder only handles the simple parent+textures shape.
            com.google.gson.JsonArray overrideArray = new com.google.gson.JsonArray();
            for (Map.Entry<ResourceKey<TrimMaterial>, Float> entry : entries) {
                String trimName = entry.getKey().location().getPath();
                String modelName = itemPath + "_" + trimName + "_trim";
                JsonObject override = new JsonObject();
                JsonObject predicate = new JsonObject();
                predicate.addProperty("trim_type", entry.getValue());
                override.add("predicate", predicate);
                override.addProperty("model", ExtraBiomes.MOD_ID + ":item/" + modelName);
                overrideArray.add(override);
            }
            json.add("overrides", overrideArray);
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
        java.util.List<CompletableFuture<?>> futures = new java.util.ArrayList<>();
        models.forEach((id, supplier) -> futures.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(id))));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Item Models";
    }
}
