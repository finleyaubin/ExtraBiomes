package net.winepicfin.extrabiomes.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.block.custom.MossyPebbleBlock;
import net.winepicfin.extrabiomes.block.custom.PebbleBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

// NeoForge 21.4 removed its own BlockStateProvider/ConfiguredModel/ModelFile/ExistingFileHelper
// convenience API (net.neoforged.neoforge.client.model.generators.*, net.neoforged.neoforge.common.
// data.ExistingFileHelper) outright - confirmed by diffing neoforge-21.3.97-universal.jar (has those
// classes) against neoforge-21.4.157-universal.jar (doesn't; net.minecraft.client.data.models.
// ModelProvider is now patched to implement NeoForge's IModelProviderExtension directly instead).
// This is a straight port of fabric/'s ModBlockStateProvider (which already had to be built this way,
// since Fabric never had Forge's convenience API to begin with) onto vanilla's public
// ModelTemplates/TextureMapping/MultiVariantGenerator/PropertyDispatch/Variant building blocks - see
// that file for the per-shape derivation notes (stair/button/trapdoor rotation offsets etc).
public class ModBlockStateProvider implements DataProvider {
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    // 1.21.4 added a required indirection layer: an item no longer resolves its rendered model
    // directly from models/item/<id>.json by convention - it now needs an explicit
    // assets/<ns>/items/<id>.json "client item" file pointing at that model (introduced in snapshot
    // 24w45a, see minecraft.wiki/w/Items_model_definition). Every block-item model this class writes
    // via delegateItemModel()/saplingItemModel() needs a matching entry here, or that block's item
    // silently renders as a missing/no-model item.
    private final PackOutput.PathProvider itemPathProvider;
    private final Map<Block, BlockStateGenerator> blockStates = new HashMap<>();
    private final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
    private final Map<ResourceLocation, Supplier<JsonElement>> items = new HashMap<>();

    public ModBlockStateProvider(PackOutput output) {
        this.blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        this.itemPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        registerStatesAndModels();
    }

    private void clientItem(Block block, ResourceLocation modelId) {
        ResourceLocation itemId = BuiltInRegistries.BLOCK.getKey(block);
        items.put(itemId, () -> {
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", modelId.toString());
            JsonObject json = new JsonObject();
            json.add("model", model);
            return json;
        });
    }

    private void registerStatesAndModels() {
        blockWithItem(ModBlocks.NETHER_DIAMOND_ORE);
        blockWithItem(ModBlocks.DENSE_CLOUD_BRICK);
        stairsBlock(ModBlocks.DENSE_CLOUD_BRICK_STAIRS.get(), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()));
        slabBlock(ModBlocks.DENSE_CLOUD_BRICK_SLAB.get(), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()));
        blockWithItem(ModBlocks.DENSE_CLOUD);
        fluidBlock(ModBlocks.GOO.get());
        pebbleBlock(ModBlocks.PEBBLE.get(), "pebble", PebbleBlock.SIZE);
        pebbleBlock(ModBlocks.MOSSY_PEBBLE.get(), "mossy_pebble", MossyPebbleBlock.SIZE);
        stickPileBlock(ModBlocks.STICK_PILE.get());
        // black sand
        blockWithItem(ModBlocks.BLACK_SAND);
        cubeBottomTopBlock(ModBlocks.BLACK_SANDSTONE.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()), modLoc("black_sandstone_bottom"), modLoc("black_sandstone_top"));
        cubeBottomTopBlock(ModBlocks.CHISELED_BLACK_SANDSTONE.get(), blockTexture(ModBlocks.CHISELED_BLACK_SANDSTONE.get()), modLoc("black_sandstone_top"), modLoc("black_sandstone_top"));
        cubeBottomTopBlock(ModBlocks.CUT_BLACK_SANDSTONE.get(), blockTexture(ModBlocks.CUT_BLACK_SANDSTONE.get()), modLoc("black_sandstone_top"), modLoc("black_sandstone_top"));
        cubeAllBlock(ModBlocks.SMOOTH_BLACK_SANDSTONE.get(), modLoc("black_sandstone_top"));
        stairsBlock(ModBlocks.BLACK_SANDSTONE_STAIRS.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()), modLoc("black_sandstone_bottom"), modLoc("black_sandstone_top"));
        stairsBlock(ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.get(), modLoc("black_sandstone_top"));
        slabBlock(ModBlocks.BLACK_SANDSTONE_SLAB.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()), modLoc("black_sandstone_bottom"), modLoc("black_sandstone_top"));
        slabBlock(ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get(), blockTexture(ModBlocks.CUT_BLACK_SANDSTONE.get()), modLoc("black_sandstone_top"), modLoc("black_sandstone_top"));
        slabBlock(ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get(), modLoc("black_sandstone_top"));
        wallBlock(ModBlocks.BLACK_SANDSTONE_WALL.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()));
        // mystic wood
        blockWithItem(ModBlocks.MYSTIC_PLANKS);
        logBlock(ModBlocks.MYSTIC_LOG.get());
        logBlock(ModBlocks.STRIPPED_MYSTIC_LOG.get());
        axisBlock(ModBlocks.MYSTIC_WOOD.get(), blockTexture(ModBlocks.MYSTIC_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_MYSTIC_WOOD.get(), blockTexture(ModBlocks.STRIPPED_MYSTIC_LOG.get()));
        blockWithItemCutout(ModBlocks.MYSTIC_LEAVES);
        saplingBlock(ModBlocks.MYSTIC_SAPLING.get());
        stairsBlock(ModBlocks.MYSTIC_STAIRS.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        slabBlock(ModBlocks.MYSTIC_SLAB.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        buttonBlock(ModBlocks.MYSTIC_BUTTON.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        pressurePlateBlock(ModBlocks.MYSTIC_PRESSURE_PLATE.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        fenceBlock(ModBlocks.MYSTIC_FENCE.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        fenceGateBlock(ModBlocks.MYSTIC_FENCE_GATE.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        doorBlockState(ModBlocks.MYSTIC_DOOR.get(), modLoc("mystic_door_bottom"), modLoc("mystic_door_top"));
        trapdoorBlockState(ModBlocks.MYSTIC_TRAPDOOR.get(), modLoc("mystic_trapdoor"));
        signBlockState(ModBlocks.MYSTIC_SIGN.get(), ModBlocks.MYSTIC_WALL_SIGN.get());
        hangingSignBlockState(ModBlocks.MYSTIC_HANGING_SIGN.get(), ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        // sky wood
        blockWithItem(ModBlocks.SKY_PLANKS);
        logBlock(ModBlocks.SKY_LOG.get());
        logBlock(ModBlocks.STRIPPED_SKY_LOG.get());
        axisBlock(ModBlocks.SKY_WOOD.get(), blockTexture(ModBlocks.SKY_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_SKY_WOOD.get(), blockTexture(ModBlocks.STRIPPED_SKY_LOG.get()));
        blockWithItemCutout(ModBlocks.SKY_LEAVES);
        saplingBlock(ModBlocks.SKY_SAPLING.get());
        stairsBlock(ModBlocks.SKY_STAIRS.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        slabBlock(ModBlocks.SKY_SLAB.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        buttonBlock(ModBlocks.SKY_BUTTON.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        pressurePlateBlock(ModBlocks.SKY_PRESSURE_PLATE.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        fenceBlock(ModBlocks.SKY_FENCE.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        fenceGateBlock(ModBlocks.SKY_FENCE_GATE.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        doorBlockState(ModBlocks.SKY_DOOR.get(), modLoc("sky_door_bottom"), modLoc("sky_door_top"));
        trapdoorBlockState(ModBlocks.SKY_TRAPDOOR.get(), modLoc("sky_trapdoor"));
        signBlockState(ModBlocks.SKY_SIGN.get(), ModBlocks.SKY_WALL_SIGN.get());
        hangingSignBlockState(ModBlocks.SKY_HANGING_SIGN.get(), ModBlocks.SKY_WALL_HANGING_SIGN.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        // palm wood
        blockWithItem(ModBlocks.PALM_PLANKS);
        logBlock(ModBlocks.PALM_LOG.get());
        logBlock(ModBlocks.STRIPPED_PALM_LOG.get());
        axisBlock(ModBlocks.PALM_WOOD.get(), blockTexture(ModBlocks.PALM_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_PALM_WOOD.get(), blockTexture(ModBlocks.STRIPPED_PALM_LOG.get()));
        blockWithItemCutout(ModBlocks.PALM_LEAVES);
        customSaplingBlock(ModBlocks.PALM_SAPLING.get(), modLoc("palm_sapling"));
        stairsBlock(ModBlocks.PALM_STAIRS.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        slabBlock(ModBlocks.PALM_SLAB.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        buttonBlock(ModBlocks.PALM_BUTTON.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        pressurePlateBlock(ModBlocks.PALM_PRESSURE_PLATE.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        fenceBlock(ModBlocks.PALM_FENCE.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        fenceGateBlock(ModBlocks.PALM_FENCE_GATE.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        doorBlockState(ModBlocks.PALM_DOOR.get(), modLoc("palm_door_bottom"), modLoc("palm_door_top"));
        trapdoorBlockState(ModBlocks.PALM_TRAPDOOR.get(), modLoc("palm_trapdoor"));
        signBlockState(ModBlocks.PALM_SIGN.get(), ModBlocks.PALM_WALL_SIGN.get());
        hangingSignBlockState(ModBlocks.PALM_HANGING_SIGN.get(), ModBlocks.PALM_WALL_HANGING_SIGN.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        // Gilded Sky wood
        blockWithItem(ModBlocks.GILDED_SKY_PLANKS);
        logBlock(ModBlocks.GILDED_SKY_LOG.get());
        logBlock(ModBlocks.STRIPPED_GILDED_SKY_LOG.get());
        axisBlock(ModBlocks.GILDED_SKY_WOOD.get(), blockTexture(ModBlocks.GILDED_SKY_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_GILDED_SKY_WOOD.get(), blockTexture(ModBlocks.STRIPPED_GILDED_SKY_LOG.get()));
        stairsBlock(ModBlocks.GILDED_SKY_STAIRS.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        slabBlock(ModBlocks.GILDED_SKY_SLAB.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        buttonBlock(ModBlocks.GILDED_SKY_BUTTON.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        pressurePlateBlock(ModBlocks.GILDED_SKY_PRESSURE_PLATE.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        fenceBlock(ModBlocks.GILDED_SKY_FENCE.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        fenceGateBlock(ModBlocks.GILDED_SKY_FENCE_GATE.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        doorBlockState(ModBlocks.GILDED_SKY_DOOR.get(), modLoc("gilded_sky_door_bottom"), modLoc("gilded_sky_door_top"));
        trapdoorBlockState(ModBlocks.GILDED_SKY_TRAPDOOR.get(), modLoc("gilded_sky_trapdoor"));
        signBlockState(ModBlocks.GILDED_SKY_SIGN.get(), ModBlocks.GILDED_SKY_WALL_SIGN.get());
        hangingSignBlockState(ModBlocks.GILDED_SKY_HANGING_SIGN.get(), ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        // Small Mushrooms
        saplingBlock(ModBlocks.BLACK_MUSHROOM.get());
        saplingBlock(ModBlocks.BLUE_MUSHROOM.get());
        saplingBlock(ModBlocks.CYAN_MUSHROOM.get());
        saplingBlock(ModBlocks.GREEN_MUSHROOM.get());
        saplingBlock(ModBlocks.ORANGE_MUSHROOM.get());
        saplingBlock(ModBlocks.PURPLE_MUSHROOM.get());
        saplingBlock(ModBlocks.WHITE_MUSHROOM.get());
        saplingBlock(ModBlocks.YELLOW_MUSHROOM.get());
        saplingBlock(ModBlocks.GLOW_MUSHROOM.get());
        // Mushrooms
        blockWithItemCutout(ModBlocks.BLACK_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.BLUE_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.CYAN_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.GREEN_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.ORANGE_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.PURPLE_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.WHITE_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.YELLOW_MUSHROOM_BLOCK);
        blockWithItemCutout(ModBlocks.GLOW_MUSHROOM_BLOCK);
    }

    // ---- helpers -----------------------------------------------------------------------------

    private ResourceLocation blockTexture(Block block) {
        return TextureMapping.getBlockTexture(block);
    }

    private ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "block/" + path);
    }

    private void blockWithItem(RegistrySupplier<Block> blockRegistryObject) {
        cubeAllBlock(blockRegistryObject.get(), blockTexture(blockRegistryObject.get()));
    }

    // Cutout blocks (leaves/mushroom blocks) additionally need their render layer registered at
    // runtime - see ModEventBusClientEvents#registerRenderLayers - the JSON "render_type" field the
    // old Forge/NeoForge BlockStateProvider.renderType() used to write no longer has a generator to
    // produce it here, so this now matches Fabric's runtime-registration approach instead.
    private void blockWithItemCutout(RegistrySupplier<Block> blockRegistryObject) {
        blockWithItem(blockRegistryObject);
    }

    private void cubeAllBlock(Block block, ResourceLocation texture) {
        ResourceLocation model = ModelTemplates.CUBE_ALL.create(block, new TextureMapping().put(TextureSlot.ALL, texture), models::put);
        simpleBlockState(block, model);
        delegateItemModel(block, model);
    }

    private void cubeBottomTopBlock(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.BOTTOM, bottom).put(TextureSlot.TOP, top);
        ResourceLocation model = ModelTemplates.CUBE_BOTTOM_TOP.create(block, tm, models::put);
        simpleBlockState(block, model);
        delegateItemModel(block, model);
    }

    private void simpleBlockState(Block block, ResourceLocation model) {
        blockStates.put(block, MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, model)));
    }

    private void delegateItemModel(Block block, ResourceLocation blockModel) {
        ResourceLocation itemModelId = ModelLocationUtils.getModelLocation(block.asItem());
        models.put(itemModelId, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", blockModel.toString());
            return json;
        });
        clientItem(block, itemModelId);
    }

    private void logBlock(Block block) {
        ResourceLocation side = blockTexture(block);
        ResourceLocation end = ResourceLocation.fromNamespaceAndPath(side.getNamespace(), side.getPath() + "_top");
        axisBlock(block, side, end);
    }

    private void axisBlock(Block block, ResourceLocation texture) {
        axisBlock(block, texture, texture);
    }

    private void axisBlock(Block block, ResourceLocation side, ResourceLocation end) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.END, end);
        ResourceLocation model = ModelTemplates.CUBE_COLUMN.create(block, tm, models::put);
        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.property(RotatedPillarBlock.AXIS)
                        .select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, model))
                        .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, model).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true))
                        .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, model).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true))));
        delegateItemModel(block, model);
    }

    private void saplingBlock(Block block) {
        ResourceLocation model = ModelTemplates.CROSS.create(block, TextureMapping.cross(block), models::put);
        simpleBlockState(block, model);
        saplingItemModel(block);
    }

    // Palm sapling has its own custom multi-blade geometry on Bedrock, not vanilla's flat
    // crossed-quad shape - references the static converted model at
    // common/src/main/resources/assets/extrabiomes/models/block/palm_sapling.json.
    private void customSaplingBlock(Block block, ResourceLocation model) {
        simpleBlockState(block, model);
        saplingItemModel(block);
    }

    private void saplingItemModel(Block block) {
        ResourceLocation itemModelId = ModelLocationUtils.getModelLocation(block.asItem());
        ResourceLocation texture = blockTexture(block);
        models.put(itemModelId, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", texture.toString());
            json.add("textures", textures);
            return json;
        });
        clientItem(block, itemModelId);
    }

    private static int rot(Direction facing) {
        return switch (facing) {
            case EAST -> 0;
            case SOUTH -> 90;
            case WEST -> 180;
            case NORTH -> 270;
            default -> 0;
        };
    }

    private static VariantProperties.Rotation yRot(int degrees) {
        return switch (((degrees % 360) + 360) % 360) {
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }

    private void stairsBlock(Block block, ResourceLocation texture) {
        stairsBlock(block, texture, texture, texture);
    }

    private void stairsBlock(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.BOTTOM, bottom).put(TextureSlot.TOP, top);
        ResourceLocation straight = ModelTemplates.STAIRS_STRAIGHT.create(block, tm, models::put);
        ResourceLocation inner = ModelTemplates.STAIRS_INNER.create(block, tm, models::put);
        ResourceLocation outer = ModelTemplates.STAIRS_OUTER.create(block, tm, models::put);

        PropertyDispatch.C3<Direction, Half, StairsShape> dispatch = PropertyDispatch.properties(StairBlock.FACING, StairBlock.HALF, StairBlock.SHAPE);
        for (Direction facing : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            for (StairsShape shape : StairsShape.values()) {
                ResourceLocation model = switch (shape) {
                    case STRAIGHT -> straight;
                    case INNER_LEFT, INNER_RIGHT -> inner;
                    case OUTER_LEFT, OUTER_RIGHT -> outer;
                };
                int baseRotation = rot(facing);
                boolean isLeft = shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT;
                boolean isRight = shape == StairsShape.INNER_RIGHT || shape == StairsShape.OUTER_RIGHT;
                int bottomRotation = isLeft ? baseRotation - 90 : baseRotation;
                int topRotation = isRight ? baseRotation + 90 : baseRotation;

                Variant bottomVariant = Variant.variant().with(VariantProperties.MODEL, model);
                if (bottomRotation != 0) bottomVariant = bottomVariant.with(VariantProperties.Y_ROT, yRot(bottomRotation)).with(VariantProperties.UV_LOCK, true);
                dispatch.select(facing, Half.BOTTOM, shape, bottomVariant);

                Variant topVariant = Variant.variant().with(VariantProperties.MODEL, model).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true);
                if (topRotation != 0) topVariant = topVariant.with(VariantProperties.Y_ROT, yRot(topRotation));
                dispatch.select(facing, Half.TOP, shape, topVariant);
            }
        }
        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(dispatch));
        delegateItemModel(block, straight);
    }

    private void slabBlock(Block block, ResourceLocation texture) {
        slabBlock(block, texture, texture, texture);
    }

    private void slabBlock(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.BOTTOM, bottom).put(TextureSlot.TOP, top);
        ResourceLocation bottomModel = ModelTemplates.SLAB_BOTTOM.create(block, tm, models::put);
        ResourceLocation topModel = ModelTemplates.SLAB_TOP.create(block, tm, models::put);
        ResourceLocation doubleModel = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(block, "_double", tm, models::put);

        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.property(SlabBlock.TYPE)
                        .select(SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, bottomModel))
                        .select(SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, topModel))
                        .select(SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, doubleModel))));
        delegateItemModel(block, bottomModel);
    }

    private void fenceBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation post = ModelTemplates.FENCE_POST.create(block, tm, models::put);
        ResourceLocation side = ModelTemplates.FENCE_SIDE.create(block, tm, models::put);
        ResourceLocation inventory = ModelTemplates.FENCE_INVENTORY.create(block, tm, models::put);

        blockStates.put(block, MultiPartGenerator.multiPart(block)
                .with(Variant.variant().with(VariantProperties.MODEL, post))
                .with(Condition.condition().term(CrossCollisionBlock.NORTH, true), Variant.variant().with(VariantProperties.MODEL, side).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(CrossCollisionBlock.EAST, true), Variant.variant().with(VariantProperties.MODEL, side).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(CrossCollisionBlock.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, side).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(CrossCollisionBlock.WEST, true), Variant.variant().with(VariantProperties.MODEL, side).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)));
        delegateItemModel(block, inventory);
    }

    private void fenceGateBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation closed = ModelTemplates.FENCE_GATE_CLOSED.create(block, tm, models::put);
        ResourceLocation open = ModelTemplates.FENCE_GATE_OPEN.create(block, tm, models::put);
        ResourceLocation wallClosed = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(block, tm, models::put);
        ResourceLocation wallOpen = ModelTemplates.FENCE_GATE_WALL_OPEN.create(block, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.properties(FenceGateBlock.FACING, FenceGateBlock.IN_WALL, FenceGateBlock.OPEN)
                        .generate((facing, inWall, isOpen) -> {
                            ResourceLocation model = inWall ? (isOpen ? wallOpen : wallClosed) : (isOpen ? open : closed);
                            Variant v = Variant.variant().with(VariantProperties.MODEL, model).with(VariantProperties.UV_LOCK, true);
                            int y = rot(facing);
                            y = (y + 270) % 360;
                            if (y != 0) v = v.with(VariantProperties.Y_ROT, yRot(y));
                            return v;
                        })));
        delegateItemModel(block, closed);
    }

    private void wallBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.WALL, texture);
        ResourceLocation post = ModelTemplates.WALL_POST.create(block, tm, models::put);
        ResourceLocation low = ModelTemplates.WALL_LOW_SIDE.create(block, tm, models::put);
        ResourceLocation tall = ModelTemplates.WALL_TALL_SIDE.create(block, tm, models::put);
        ResourceLocation inventory = ModelTemplates.WALL_INVENTORY.create(block, tm, models::put);

        blockStates.put(block, MultiPartGenerator.multiPart(block)
                .with(Condition.condition().term(WallBlock.UP, true), Variant.variant().with(VariantProperties.MODEL, post))
                .with(Condition.condition().term(WallBlock.NORTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, low).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(WallBlock.EAST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, low).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(WallBlock.SOUTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, low).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(WallBlock.WEST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, low).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(WallBlock.NORTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tall).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(WallBlock.EAST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tall).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(WallBlock.SOUTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tall).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true))
                .with(Condition.condition().term(WallBlock.WEST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tall).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)));
        delegateItemModel(block, inventory);
    }

    private void buttonBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation unpowered = ModelTemplates.BUTTON.create(block, tm, models::put);
        ResourceLocation powered = ModelTemplates.BUTTON_PRESSED.create(block, tm, models::put);
        ResourceLocation inventory = ModelTemplates.BUTTON_INVENTORY.create(block, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.properties(ButtonBlock.FACE, ButtonBlock.FACING, ButtonBlock.POWERED)
                        .generate((face, facing, powered1) -> {
                            ResourceLocation model = powered1 ? powered : unpowered;
                            Variant v = Variant.variant().with(VariantProperties.MODEL, model);
                            int y = (rot(facing) + 90) % 360;
                            switch (face) {
                                case FLOOR -> {
                                }
                                case WALL -> v = v.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90);
                                case CEILING -> {
                                    v = v.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180);
                                    y = (y + 180) % 360;
                                }
                            }
                            if (y != 0) v = v.with(VariantProperties.Y_ROT, yRot(y));
                            return v.with(VariantProperties.UV_LOCK, true);
                        })));
        delegateItemModel(block, inventory);
    }

    private void pressurePlateBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation up = ModelTemplates.PRESSURE_PLATE_UP.create(block, tm, models::put);
        ResourceLocation down = ModelTemplates.PRESSURE_PLATE_DOWN.create(block, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.property(PressurePlateBlock.POWERED)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, up))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, down))));
        delegateItemModel(block, up);
    }

    private void doorBlockState(Block block, ResourceLocation bottomModel, ResourceLocation topModel) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.BOTTOM, bottomModel).put(TextureSlot.TOP, topModel);
        ModelTemplates.DOOR_BOTTOM_LEFT.create(bottomModel, tm, models::put);
        ModelTemplates.DOOR_TOP_LEFT.create(topModel, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.properties(DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.HALF)
                        .generate((facing, open, hinge, half) -> {
                            ResourceLocation model = half == DoubleBlockHalf.LOWER ? bottomModel : topModel;
                            int y = rot(facing);
                            if (open) {
                                y += 90;
                                if (hinge == DoorHingeSide.RIGHT) y += 180;
                            }
                            Variant v = Variant.variant().with(VariantProperties.MODEL, model);
                            int normalized = ((y % 360) + 360) % 360;
                            if (normalized != 0) v = v.with(VariantProperties.Y_ROT, yRot(normalized));
                            return v.with(VariantProperties.UV_LOCK, true);
                        })));
    }

    // Our trapdoor textures (mystic/sky/palm/gilded_sky) have a directional plank/slat pattern, the
    // same kind vanilla's acacia/spruce/birch trapdoors use - those all ship on the
    // "template_orientable_trapdoor_*" parent.
    private void trapdoorBlockState(Block block, ResourceLocation baseModelName) {
        ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(baseModelName.getNamespace(), baseModelName.getPath() + "_bottom");
        ResourceLocation top = ResourceLocation.fromNamespaceAndPath(baseModelName.getNamespace(), baseModelName.getPath() + "_top");
        ResourceLocation open = ResourceLocation.fromNamespaceAndPath(baseModelName.getNamespace(), baseModelName.getPath() + "_open");

        putTrapdoorModel(bottom, "minecraft:block/template_orientable_trapdoor_bottom", baseModelName);
        putTrapdoorModel(top, "minecraft:block/template_orientable_trapdoor_top", baseModelName);
        putTrapdoorModel(open, "minecraft:block/template_orientable_trapdoor_open", baseModelName);
        delegateItemModel(block, bottom);

        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.properties(TrapDoorBlock.FACING, TrapDoorBlock.OPEN, TrapDoorBlock.HALF)
                        .generate((facing, isOpen, half) -> {
                            ResourceLocation model = isOpen ? open : (half == Half.TOP ? top : bottom);
                            int y = (rot(facing) + 90) % 360;
                            Variant v = Variant.variant().with(VariantProperties.MODEL, model);
                            if (isOpen && half == Half.TOP) {
                                v = v.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180);
                                y = (y + 180) % 360;
                            }
                            if (y != 0) v = v.with(VariantProperties.Y_ROT, yRot(y));
                            return v;
                        })));
    }

    private void putTrapdoorModel(ResourceLocation modelId, String parent, ResourceLocation texture) {
        models.put(modelId, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", parent);
            JsonObject textures = new JsonObject();
            textures.addProperty("texture", texture.toString());
            json.add("textures", textures);
            return json;
        });
    }

    private void signBlockState(Block signBlock, Block wallSignBlock) {
        ResourceLocation air = ResourceLocation.fromNamespaceAndPath("minecraft", "block/air");
        simpleBlockState(signBlock, air);
        simpleBlockState(wallSignBlock, air);
    }

    private void hangingSignBlockState(Block signBlock, Block wallSignBlock, ResourceLocation texture) {
        ResourceLocation modelId = ModelLocationUtils.getModelLocation(signBlock);
        models.put(modelId, () -> {
            JsonObject json = new JsonObject();
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", texture.toString());
            json.add("textures", textures);
            return json;
        });
        simpleBlockState(signBlock, modelId);
        simpleBlockState(wallSignBlock, modelId);
    }

    // References the pre-existing static models under common/src/main/resources/assets/extrabiomes/
    // models/block/{small,medium,large}_<type>.json.
    private void pebbleBlock(Block block, String type, IntegerProperty sizeProperty) {
        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.property(sizeProperty)
                        .select(1, Variant.variant().with(VariantProperties.MODEL, modLoc("small_" + type)))
                        .select(2, Variant.variant().with(VariantProperties.MODEL, modLoc("medium_" + type)))
                        .select(3, Variant.variant().with(VariantProperties.MODEL, modLoc("large_" + type)))));
        delegateItemModel(block, modLoc("small_" + type));
    }

    private void stickPileBlock(Block block) {
        blockStates.put(block, MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.property(RotatedPillarBlock.AXIS)
                        .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, modLoc("stick_pile_x")))
                        .select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, modLoc("stick_pile_y")))
                        .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, modLoc("stick_pile_z")))));
        delegateItemModel(block, modLoc("stick_pile_y"));
    }

    private void fluidBlock(Block block) {
        simpleBlockState(block, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water"));
    }

    private ResourceLocation key(Block block) {
        return Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        blockStates.forEach((block, generator) -> {
            ResourceLocation id = key(block);
            futures.add(DataProvider.saveStable(cache, generator.get(), blockStatePathProvider.json(id)));
        });
        models.forEach((id, supplier) -> futures.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(id))));
        items.forEach((id, supplier) -> futures.add(DataProvider.saveStable(cache, supplier.get(), itemPathProvider.json(id))));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Block States";
    }
}
