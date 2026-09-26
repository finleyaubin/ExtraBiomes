package net.winepicfin.extrabiomes.fabric.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.winepicfin.extrabiomes.commondatagen.TexturePaths;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
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

// Fabric port of forge/datagen/ModBlockStateProvider.java. Forge's BlockStateProvider (and its
// ConfiguredModel/ModelFile/ExistingFileHelper machinery) is a Forge-only convenience API with no
// Fabric equivalent, and vanilla's own net.minecraft.client.data.models.BlockModelGenerators - the class
// Fabric's FabricModelProvider hands you an instance of - only exposes a handful of its per-block
// helper methods as public (createTrivialCube/createTrivialBlock/createAxisAlignedPillarBlock/
// createHangingSign/createGenericCube/createSimpleFlatItemModel); everything else needed here
// (stairs/slabs/fences/gates/doors/trapdoors/buttons/plates/walls) is package-private in that class
// and inaccessible from mod code. So instead of fighting that, this is a standalone DataProvider built
// directly on the *public* low-level model-gen API (ModelTemplates/TextureMapping/
// MultiVariantGenerator/PropertyDispatch/Variant - see net.minecraft.client.data.models.{blockstates,model}) -
// the same public building blocks BlockModelGenerators itself is written on top of.
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
    private final Map<Block, BlockModelDefinitionGenerator> blockStates = new HashMap<>();
    private final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
    private final Map<ResourceLocation, Supplier<JsonElement>> items = new HashMap<>();

    public ModBlockStateProvider(net.fabricmc.fabric.api.datagen.v1.FabricDataOutput output) {
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
        blockWithItem(ModBlocks.NETHER_COAL_ORE);
        blockWithItem(ModBlocks.NETHER_COPPER_ORE);
        blockWithItem(ModBlocks.NETHER_EMERALD_ORE);
        blockWithItem(ModBlocks.NETHER_IRON_ORE);
        blockWithItem(ModBlocks.NETHER_LAPIS_ORE);
        blockWithItem(ModBlocks.NETHER_REDSTONE_ORE);
        blockWithItem(ModBlocks.DENSE_CLOUD_BRICK);
        stairsBlock(ModBlocks.DENSE_CLOUD_BRICK_STAIRS.get(), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()));
        slabBlock(ModBlocks.DENSE_CLOUD_BRICK_SLAB.get(), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()), blockTexture(ModBlocks.DENSE_CLOUD_BRICK.get()));
        blockWithItem(ModBlocks.DENSE_CLOUD);
        fluidBlock(ModBlocks.GOO.get());
        pebbleBlock(ModBlocks.PEBBLE.get(), "pebble", PebbleBlock.SIZE);
        pebbleBlock(ModBlocks.MOSSY_PEBBLE.get(), "mossy_pebble", MossyPebbleBlock.SIZE);
        stickPileBlock(ModBlocks.STICK_PILE.get());
        grassStoneBlock(ModBlocks.GRASS_STONE.get());
        // black sand
        blockWithItem(ModBlocks.BLACK_SAND);
        cubeBottomTopBlock(ModBlocks.BLACK_SANDSTONE.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()), texLoc("black_sandstone_bottom"), texLoc("black_sandstone_top"));
        cubeBottomTopBlock(ModBlocks.CHISELED_BLACK_SANDSTONE.get(), blockTexture(ModBlocks.CHISELED_BLACK_SANDSTONE.get()), texLoc("black_sandstone_top"), texLoc("black_sandstone_top"));
        cubeBottomTopBlock(ModBlocks.CUT_BLACK_SANDSTONE.get(), blockTexture(ModBlocks.CUT_BLACK_SANDSTONE.get()), texLoc("black_sandstone_top"), texLoc("black_sandstone_top"));
        cubeAllBlock(ModBlocks.SMOOTH_BLACK_SANDSTONE.get(), texLoc("black_sandstone_top"));
        stairsBlock(ModBlocks.BLACK_SANDSTONE_STAIRS.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()), texLoc("black_sandstone_bottom"), texLoc("black_sandstone_top"));
        stairsBlock(ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.get(), texLoc("black_sandstone_top"));
        slabBlock(ModBlocks.BLACK_SANDSTONE_SLAB.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()), texLoc("black_sandstone_bottom"), texLoc("black_sandstone_top"));
        slabBlock(ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get(), blockTexture(ModBlocks.CUT_BLACK_SANDSTONE.get()), texLoc("black_sandstone_top"), texLoc("black_sandstone_top"));
        slabBlock(ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get(), texLoc("black_sandstone_top"));
        wallBlock(ModBlocks.BLACK_SANDSTONE_WALL.get(), blockTexture(ModBlocks.BLACK_SANDSTONE.get()));
        // mystic wood
        blockWithItem(ModBlocks.MYSTIC_PLANKS);
        logBlock(ModBlocks.MYSTIC_LOG.get());
        logBlock(ModBlocks.STRIPPED_MYSTIC_LOG.get());
        axisBlock(ModBlocks.MYSTIC_WOOD.get(), blockTexture(ModBlocks.MYSTIC_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_MYSTIC_WOOD.get(), blockTexture(ModBlocks.STRIPPED_MYSTIC_LOG.get()));
        blockWithItem(ModBlocks.MYSTIC_LEAVES);
        saplingBlock(ModBlocks.MYSTIC_SAPLING.get());
        stairsBlock(ModBlocks.MYSTIC_STAIRS.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        slabBlock(ModBlocks.MYSTIC_SLAB.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        buttonBlock(ModBlocks.MYSTIC_BUTTON.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        pressurePlateBlock(ModBlocks.MYSTIC_PRESSURE_PLATE.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        fenceBlock(ModBlocks.MYSTIC_FENCE.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        fenceGateBlock(ModBlocks.MYSTIC_FENCE_GATE.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        doorBlockState(ModBlocks.MYSTIC_DOOR.get(), texLoc("mystic_door_bottom"), texLoc("mystic_door_top"));
        trapdoorBlockState(ModBlocks.MYSTIC_TRAPDOOR.get(), texLoc("mystic_trapdoor"));
        signBlockState(ModBlocks.MYSTIC_SIGN.get(), ModBlocks.MYSTIC_WALL_SIGN.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        hangingSignBlockState(ModBlocks.MYSTIC_HANGING_SIGN.get(), ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        // sky wood
        blockWithItem(ModBlocks.SKY_PLANKS);
        logBlock(ModBlocks.SKY_LOG.get());
        logBlock(ModBlocks.STRIPPED_SKY_LOG.get());
        axisBlock(ModBlocks.SKY_WOOD.get(), blockTexture(ModBlocks.SKY_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_SKY_WOOD.get(), blockTexture(ModBlocks.STRIPPED_SKY_LOG.get()));
        blockWithItem(ModBlocks.SKY_LEAVES);
        saplingBlock(ModBlocks.SKY_SAPLING.get());
        stairsBlock(ModBlocks.SKY_STAIRS.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        slabBlock(ModBlocks.SKY_SLAB.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        buttonBlock(ModBlocks.SKY_BUTTON.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        pressurePlateBlock(ModBlocks.SKY_PRESSURE_PLATE.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        fenceBlock(ModBlocks.SKY_FENCE.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        fenceGateBlock(ModBlocks.SKY_FENCE_GATE.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        doorBlockState(ModBlocks.SKY_DOOR.get(), texLoc("sky_door_bottom"), texLoc("sky_door_top"));
        trapdoorBlockState(ModBlocks.SKY_TRAPDOOR.get(), texLoc("sky_trapdoor"));
        signBlockState(ModBlocks.SKY_SIGN.get(), ModBlocks.SKY_WALL_SIGN.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        hangingSignBlockState(ModBlocks.SKY_HANGING_SIGN.get(), ModBlocks.SKY_WALL_HANGING_SIGN.get(), blockTexture(ModBlocks.SKY_PLANKS.get()));
        // palm wood
        blockWithItem(ModBlocks.PALM_PLANKS);
        logBlock(ModBlocks.PALM_LOG.get());
        logBlock(ModBlocks.STRIPPED_PALM_LOG.get());
        axisBlock(ModBlocks.PALM_WOOD.get(), blockTexture(ModBlocks.PALM_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_PALM_WOOD.get(), blockTexture(ModBlocks.STRIPPED_PALM_LOG.get()));
        blockWithItem(ModBlocks.PALM_LEAVES);
        customSaplingBlock(ModBlocks.PALM_SAPLING.get(), modLoc("palm_sapling"));
        stairsBlock(ModBlocks.PALM_STAIRS.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        slabBlock(ModBlocks.PALM_SLAB.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        buttonBlock(ModBlocks.PALM_BUTTON.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        pressurePlateBlock(ModBlocks.PALM_PRESSURE_PLATE.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        fenceBlock(ModBlocks.PALM_FENCE.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        fenceGateBlock(ModBlocks.PALM_FENCE_GATE.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
        doorBlockState(ModBlocks.PALM_DOOR.get(), texLoc("palm_door_bottom"), texLoc("palm_door_top"));
        trapdoorBlockState(ModBlocks.PALM_TRAPDOOR.get(), texLoc("palm_trapdoor"));
        signBlockState(ModBlocks.PALM_SIGN.get(), ModBlocks.PALM_WALL_SIGN.get(), blockTexture(ModBlocks.PALM_PLANKS.get()));
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
        doorBlockState(ModBlocks.GILDED_SKY_DOOR.get(), texLoc("gilded_sky_door_bottom"), texLoc("gilded_sky_door_top"));
        trapdoorBlockState(ModBlocks.GILDED_SKY_TRAPDOOR.get(), texLoc("gilded_sky_trapdoor"));
        signBlockState(ModBlocks.GILDED_SKY_SIGN.get(), ModBlocks.GILDED_SKY_WALL_SIGN.get(), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
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
        blockWithItem(ModBlocks.BLACK_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.BLUE_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.CYAN_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.GREEN_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.ORANGE_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.PURPLE_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.WHITE_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.YELLOW_MUSHROOM_BLOCK);
        blockWithItem(ModBlocks.GLOW_MUSHROOM_BLOCK);
    }

    // ---- helpers -----------------------------------------------------------------------------

    private ResourceLocation blockTexture(Block block) {
        return texLoc(BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    private ResourceLocation modelOf(ResourceLocation texture) {
        return modLoc(texture.getPath().substring(texture.getPath().lastIndexOf('/') + 1));
    }

    private ResourceLocation texLoc(String stem) {
        return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, TexturePaths.block(stem));
    }

    private ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "block/" + path);
    }

    private void blockWithItem(RegistrySupplier<Block> blockRegistryObject) {
        cubeAllBlock(blockRegistryObject.get(), blockTexture(blockRegistryObject.get()));
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

    private void grassStoneBlock(Block block) {
        TextureMapping common = new TextureMapping().put(TextureSlot.SIDE, texLoc("grass_stone_side")).put(TextureSlot.BOTTOM, texLoc("grass_stone_bottom")).put(TextureSlot.TOP, texLoc("grass_stone_top"));
        TextureMapping egg = new TextureMapping().put(TextureSlot.SIDE, texLoc("grass_stone_side")).put(TextureSlot.BOTTOM, texLoc("grass_stone_bottom")).put(TextureSlot.TOP, texLoc("grass_stone_top_egg"));
        ResourceLocation commonModel = ModelTemplates.CUBE_BOTTOM_TOP.create(block, common, models::put);
        ResourceLocation eggModel = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(block, "_egg", egg, models::put);
        blockStates.put(block, MultiVariantGenerator.dispatch(block, new MultiVariant(WeightedList.of(
                new Weighted<>(new Variant(commonModel), 1200),
                new Weighted<>(new Variant(eggModel), 1)))));
        delegateItemModel(block, commonModel);
    }

    private void simpleBlockState(Block block, ResourceLocation model) {
        blockStates.put(block, MultiVariantGenerator.dispatch(block, plain(model)));
    }

    // Auto-derives an item model that just reuses the block model (matching Forge's
    // simpleBlockWithItem behaviour, which never needed a separate ModItemModelProvider entry for
    // these blocks either).
    private void delegateItemModel(Block block, ResourceLocation blockModel) {
        ResourceLocation itemModelId = ModelLocationUtils.getModelLocation(block.asItem());
        models.put(itemModelId, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", blockModel.toString());
            return json;
        });
        clientItem(block, itemModelId);
    }

    // Passing the same texture for both sides put bark on the cut ends too; use the dedicated "_top" texture.
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
        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(RotatedPillarBlock.AXIS)
                        .select(Direction.Axis.Y, plain(model))
                        .select(Direction.Axis.Z, plain(model).with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.UV_LOCK))
                        .select(Direction.Axis.X, plain(model).with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.UV_LOCK))));
        delegateItemModel(block, model);
    }

    private void saplingBlock(Block block) {
        ResourceLocation model = ModelTemplates.CROSS.create(block, new TextureMapping().put(TextureSlot.CROSS, blockTexture(block)), models::put);
        simpleBlockState(block, model);
        saplingItemModel(block);
    }

    // Palm sapling has its own custom multi-blade geometry on Bedrock (RP/models/blocks/
    // palm_sapling.geo.json), not vanilla's flat crossed-quad shape - references the static
    // converted model at models/block/palm_sapling.json instead of generating a CROSS template.
    private void customSaplingBlock(Block block, ResourceLocation model) {
        simpleBlockState(block, model);
        saplingItemModel(block);
    }

    // Saplings get a flat inventory icon (item/generated + the block's own texture as layer0), not
    // a delegate to the 3D block model - matches vanilla's own sapling items.
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

    private static MultiVariant plain(ResourceLocation model) {
        return BlockModelGenerators.plainVariant(model);
    }

    private static VariantMutator yRot(int degrees) {
        return switch (((degrees % 360) + 360) % 360) {
            case 90 -> BlockModelGenerators.Y_ROT_90;
            case 180 -> BlockModelGenerators.Y_ROT_180;
            case 270 -> BlockModelGenerators.Y_ROT_270;
            default -> BlockModelGenerators.NOP;
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

        PropertyDispatch.C3<MultiVariant, Direction, Half, StairsShape> dispatch = PropertyDispatch.initial(StairBlock.FACING, StairBlock.HALF, StairBlock.SHAPE);
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
                // Top half is visually mirrored (the model is flipped via X_ROT 180), which swaps
                // which physical corner "left"/"right" ends up on - only the right-side corner
                // shapes need the +90 offset; STRAIGHT (and left-side shapes) match the bottom's
                // rotation unmodified (verified against vanilla's own shipped oak_stairs.json).
                int topRotation = isRight ? baseRotation + 90 : baseRotation;

                MultiVariant bottomVariant = plain(model);
                if (bottomRotation != 0) bottomVariant = bottomVariant.with(yRot(bottomRotation)).with(BlockModelGenerators.UV_LOCK);
                dispatch.select(facing, Half.BOTTOM, shape, bottomVariant);

                MultiVariant topVariant = plain(model).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.UV_LOCK).with(yRot(topRotation));
                dispatch.select(facing, Half.TOP, shape, topVariant);
            }
        }
        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(dispatch));
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

        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(SlabBlock.TYPE)
                        .select(SlabType.BOTTOM, plain(bottomModel))
                        .select(SlabType.TOP, plain(topModel))
                        .select(SlabType.DOUBLE, plain(doubleModel))));
        delegateItemModel(block, bottomModel);
    }

    private void fenceBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation post = ModelTemplates.FENCE_POST.create(block, tm, models::put);
        ResourceLocation side = ModelTemplates.FENCE_SIDE.create(block, tm, models::put);
        ResourceLocation inventory = ModelTemplates.FENCE_INVENTORY.create(block, tm, models::put);

        blockStates.put(block, MultiPartGenerator.multiPart(block)
                .with(plain(post))
                .with(new ConditionBuilder().term(CrossCollisionBlock.NORTH, true), plain(side).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(CrossCollisionBlock.EAST, true), plain(side).with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(CrossCollisionBlock.SOUTH, true), plain(side).with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(CrossCollisionBlock.WEST, true), plain(side).with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.UV_LOCK)));
        delegateItemModel(block, inventory);
    }

    private void fenceGateBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation closed = ModelTemplates.FENCE_GATE_CLOSED.create(block, tm, models::put);
        ResourceLocation open = ModelTemplates.FENCE_GATE_OPEN.create(block, tm, models::put);
        ResourceLocation wallClosed = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(block, tm, models::put);
        ResourceLocation wallOpen = ModelTemplates.FENCE_GATE_WALL_OPEN.create(block, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(FenceGateBlock.FACING, FenceGateBlock.IN_WALL, FenceGateBlock.OPEN)
                        .generate((facing, inWall, isOpen) -> {
                            ResourceLocation model = inWall ? (isOpen ? wallOpen : wallClosed) : (isOpen ? open : closed);
                            // Fence gates are offset -90 (not +180) from the stairs table's rot()
                            // convention (verified against vanilla's oak_fence_gate.json).
                            int y = (rot(facing) + 270) % 360;
                            return plain(model).with(BlockModelGenerators.UV_LOCK).with(yRot(y));
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
                .with(new ConditionBuilder().term(WallBlock.UP, true), plain(post))
                .with(new ConditionBuilder().term(WallBlock.NORTH, WallSide.LOW), plain(low).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(WallBlock.EAST, WallSide.LOW), plain(low).with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(WallBlock.SOUTH, WallSide.LOW), plain(low).with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(WallBlock.WEST, WallSide.LOW), plain(low).with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(WallBlock.NORTH, WallSide.TALL), plain(tall).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(WallBlock.EAST, WallSide.TALL), plain(tall).with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(WallBlock.SOUTH, WallSide.TALL), plain(tall).with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.UV_LOCK))
                .with(new ConditionBuilder().term(WallBlock.WEST, WallSide.TALL), plain(tall).with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.UV_LOCK)));
        delegateItemModel(block, inventory);
    }

    private void buttonBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation unpowered = ModelTemplates.BUTTON.create(block, tm, models::put);
        ResourceLocation powered = ModelTemplates.BUTTON_PRESSED.create(block, tm, models::put);
        ResourceLocation inventory = ModelTemplates.BUTTON_INVENTORY.create(block, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(ButtonBlock.FACE, ButtonBlock.FACING, ButtonBlock.POWERED)
                        .generate((face, facing, powered1) -> {
                            ResourceLocation model = powered1 ? powered : unpowered;
                            MultiVariant v = plain(model);
                            // rot(facing) is offset by -90 from the button's own facing convention
                            // (verified against vanilla's oak_button.json); ceiling additionally
                            // flips the model via X_ROT 180, which needs another +180 on Y to match.
                            int y = (rot(facing) + 90) % 360;
                            switch (face) {
                                case FLOOR -> {
                                }
                                case WALL -> v = v.with(BlockModelGenerators.X_ROT_90);
                                case CEILING -> {
                                    v = v.with(BlockModelGenerators.X_ROT_180);
                                    y = (y + 180) % 360;
                                }
                            }
                            return v.with(yRot(y)).with(BlockModelGenerators.UV_LOCK);
                        })));
        delegateItemModel(block, inventory);
    }

    private void pressurePlateBlock(Block block, ResourceLocation texture) {
        TextureMapping tm = new TextureMapping().put(TextureSlot.TEXTURE, texture);
        ResourceLocation up = ModelTemplates.PRESSURE_PLATE_UP.create(block, tm, models::put);
        ResourceLocation down = ModelTemplates.PRESSURE_PLATE_DOWN.create(block, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(PressurePlateBlock.POWERED)
                        .select(false, plain(up))
                        .select(true, plain(down))));
        delegateItemModel(block, up);
    }

    // The model JSON these blockstates reference was never actually generated on Fabric, leaving doors/trapdoors as the missing-model placeholder.
    private void doorBlockState(Block block, ResourceLocation bottomTexture, ResourceLocation topTexture) {
        ResourceLocation bottomModel = modelOf(bottomTexture);
        ResourceLocation topModel = modelOf(topTexture);
        // Uses the closed/left-hinge DOOR_BOTTOM_LEFT/TOP_LEFT template; Y_ROT alone approximates the other facing/open/hinge combos well enough.
        TextureMapping tm = new TextureMapping().put(TextureSlot.BOTTOM, bottomTexture).put(TextureSlot.TOP, topTexture);
        ModelTemplates.DOOR_BOTTOM_LEFT.create(bottomModel, tm, models::put);
        ModelTemplates.DOOR_TOP_LEFT.create(topModel, tm, models::put);

        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.HALF)
                        .generate((facing, open, hinge, half) -> {
                            ResourceLocation model = half == DoubleBlockHalf.LOWER ? bottomModel : topModel;
                            int y = rot(facing);
                            if (open) {
                                // +90 matches vanilla's own left-hinge-open rotation exactly; a
                                // right-hinged door swings to the mirrored side, which vanilla's
                                // separate right-hinge model expresses as +180 from the left-hinge
                                // rotation at the same facing (verified against oak_door.json) - the
                                // old formula collapsed both hinges to the same +90, so a right-hinged
                                // door opened identically to a left-hinged one instead of mirroring.
                                y += 90;
                                if (hinge == DoorHingeSide.RIGHT) y += 180;
                            }
                            return plain(model).with(yRot(y)).with(BlockModelGenerators.UV_LOCK);
                        })));
    }

    // Our trapdoor textures (mystic/sky/palm/gilded_sky) have a directional plank/slat pattern, the
    // same kind vanilla's acacia/spruce/birch trapdoors use - those all ship on the
    // "template_orientable_trapdoor_*" parent (verified against the real shipped
    // birch_trapdoor_bottom.json), which flips the "up" face's V axis and adds the "west"/"east"
    // face rotations needed to keep the slats reading the same way regardless of facing. The plain
    // "template_trapdoor_*" parent is only correct for oak/iron's rotationally-symmetric grid
    // texture - using it here was what made the slats appear to spin between open and closed.
    private void trapdoorBlockState(Block block, ResourceLocation baseTexture) {
        ResourceLocation baseModelName = modelOf(baseTexture);
        ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(baseModelName.getNamespace(), baseModelName.getPath() + "_bottom");
        ResourceLocation top = ResourceLocation.fromNamespaceAndPath(baseModelName.getNamespace(), baseModelName.getPath() + "_top");
        ResourceLocation open = ResourceLocation.fromNamespaceAndPath(baseModelName.getNamespace(), baseModelName.getPath() + "_open");

        putTrapdoorModel(bottom, "minecraft:block/template_orientable_trapdoor_bottom", baseTexture);
        putTrapdoorModel(top, "minecraft:block/template_orientable_trapdoor_top", baseTexture);
        putTrapdoorModel(open, "minecraft:block/template_orientable_trapdoor_open", baseTexture);
        // Matches vanilla's 3D-look trapdoor item icon (no flat sprite texture is checked in).
        delegateItemModel(block, bottom);

        // Orientable trapdoors y-rotate every state (including closed) per facing, and additionally
        // flip the open-on-top state via X_ROT 180 with another +180 on Y - verified field-for-field
        // against vanilla's real birch_trapdoor.json blockstate.
        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(TrapDoorBlock.FACING, TrapDoorBlock.OPEN, TrapDoorBlock.HALF)
                        .generate((facing, isOpen, half) -> {
                            ResourceLocation model = isOpen ? open : (half == Half.TOP ? top : bottom);
                            // rot(facing) is offset by -90 from the trapdoor's own convention
                            // (verified against vanilla's birch_trapdoor.json - e.g. facing=east is
                            // y:90, not y:0), same offset as the plain-template open state used.
                            int y = (rot(facing) + 90) % 360;
                            MultiVariant v = plain(model);
                            if (isOpen && half == Half.TOP) {
                                v = v.with(BlockModelGenerators.X_ROT_180);
                                y = (y + 180) % 360;
                            }
                            return v.with(yRot(y));
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

    // Standing/wall signs render their text via a block entity renderer - the blockstate model is
    // just an invisible placeholder, same as vanilla's own sign blocks. It still needs a "particle"
    // texture key though, or break particles fall back to the missing-texture sprite.
    private void signBlockState(Block signBlock, Block wallSignBlock, ResourceLocation texture) {
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

    // No "minecraft:block/hanging_sign" parent exists; the chain/plank mesh is block-entity-rendered like regular signs.
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
    // models/block/{small,medium,large}_<type>.json - same static assets Forge's pebbleBlock() helper
    // referenced via ModelFile.UncheckedModelFile rather than generating them.
    //
    // PebbleBlock and MossyPebbleBlock each declare their own distinct SIZE IntegerProperty
    // instance (not a shared/inherited one - MossyPebbleBlock doesn't extend PebbleBlock), so the
    // caller must pass the property belonging to the actual block being generated for. Vanilla's
    // MultiVariantGenerator/PropertyDispatch validates the property against the block's own
    // StateDefinition, unlike BlockState#getValue - passing the wrong block's property object (even
    // though both are named "size" with the same value range) throws "Property ... is not defined
    // for block ...".
    private void pebbleBlock(Block block, String type, IntegerProperty sizeProperty) {
        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(sizeProperty)
                        .select(1, plain(modLoc("small_" + type)))
                        .select(2, plain(modLoc("medium_" + type)))
                        .select(3, plain(modLoc("large_" + type)))));
        delegateItemModel(block, modLoc("small_" + type));
    }

    private void stickPileBlock(Block block) {
        blockStates.put(block, MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(RotatedPillarBlock.AXIS)
                        .select(Direction.Axis.X, plain(modLoc("stick_pile_x")))
                        .select(Direction.Axis.Y, plain(modLoc("stick_pile_y")))
                        .select(Direction.Axis.Z, plain(modLoc("stick_pile_z")))));
        delegateItemModel(block, modLoc("stick_pile_y"));
    }

    // Goo fluid's block reuses vanilla's own water still-fluid model (same as Forge's fluidBlock()
    // helper, which pointed at the same existing "minecraft:block/water" model rather than generating
    // one).
    private void fluidBlock(Block block) {
        simpleBlockState(block, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water"));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        blockStates.forEach((block, generator) -> {
            ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block));
            futures.add(DataProvider.saveStable(cache, BlockModelDefinition.CODEC, generator.create(), blockStatePathProvider.json(id)));
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
