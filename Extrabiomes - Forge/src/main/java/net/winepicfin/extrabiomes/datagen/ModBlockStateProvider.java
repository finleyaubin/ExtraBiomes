package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;

import java.util.Objects;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper){
        super(output, ExtraBiomes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels(){
        blockWithItem(ModBlocks.NETHER_DIAMOND_ORE);
        blockWithItem(ModBlocks.DENSE_CLOUD_BRICK);
        blockWithItem(ModBlocks.DENSE_CLOUD);
        //~~~~~~~~~mystic wood~~~~~~~~\\
        blockWithItem(ModBlocks.MYSTIC_PLANKS);
        logBlock((RotatedPillarBlock) ModBlocks.MYSTIC_LOG.get());
        logBlock((RotatedPillarBlock) ModBlocks.STRIPED_MYSTIC_LOG.get());
        axisBlock(((RotatedPillarBlock) ModBlocks.MYSTIC_WOOD.get()), blockTexture(ModBlocks.MYSTIC_LOG.get()),blockTexture(ModBlocks.MYSTIC_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPED_MYSTIC_WOOD.get()), blockTexture(ModBlocks.STRIPED_MYSTIC_LOG.get()),blockTexture(ModBlocks.STRIPED_MYSTIC_LOG.get()));
        blockWithItem(ModBlocks.MYSTIC_LEAVES);
        saplingBlock(ModBlocks.MYSTIC_SAPLING);
        simpleBlockItem(ModBlocks.MYSTIC_LOG.get(),models().withExistingParent("extrabiomes:mystic_log", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.MYSTIC_WOOD.get(),models().withExistingParent("extrabiomes:mystic_wood", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.STRIPED_MYSTIC_LOG.get(),models().withExistingParent("extrabiomes:striped_mystic_log", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.STRIPED_MYSTIC_WOOD.get(),models().withExistingParent("extrabiomes:striped_mystic_wood", "minecraft:block/cube_column"));
        stairsBlock(((StairBlock) ModBlocks.MYSTIC_STAIRS.get()), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.MYSTIC_SLAB.get()), blockTexture(ModBlocks.MYSTIC_PLANKS.get()), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.MYSTIC_BUTTON.get()), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.MYSTIC_PRESSURE_PLATE.get()), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.MYSTIC_FENCE.get()), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.MYSTIC_FENCE_GATE.get()), blockTexture(ModBlocks.MYSTIC_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock) ModBlocks.MYSTIC_DOOR.get()), modLoc("block/mystic_door_bottom"),modLoc("block/mystic_door_top"),"cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.MYSTIC_TRAPDOOR.get()), modLoc("block/mystic_trapdoor"),true,"cutout");
        //~~~~~~~~~sky wood~~~~~~~~\\
        blockWithItem(ModBlocks.SKY_PLANKS);
        logBlock((RotatedPillarBlock) ModBlocks.SKY_LOG.get());
        logBlock((RotatedPillarBlock) ModBlocks.STRIPED_SKY_LOG.get());
        axisBlock(((RotatedPillarBlock) ModBlocks.SKY_WOOD.get()), blockTexture(ModBlocks.SKY_LOG.get()),blockTexture(ModBlocks.SKY_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPED_SKY_WOOD.get()), blockTexture(ModBlocks.STRIPED_SKY_LOG.get()),blockTexture(ModBlocks.STRIPED_SKY_LOG.get()));
        blockWithItem(ModBlocks.SKY_LEAVES);
        saplingBlock(ModBlocks.SKY_SAPLING);
        simpleBlockItem(ModBlocks.SKY_LOG.get(),models().withExistingParent("extrabiomes:sky_log", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.SKY_WOOD.get(),models().withExistingParent("extrabiomes:sky_wood", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.STRIPED_SKY_LOG.get(),models().withExistingParent("extrabiomes:striped_sky_log", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.STRIPED_SKY_WOOD.get(),models().withExistingParent("extrabiomes:striped_sky_wood", "minecraft:block/cube_column"));
        stairsBlock(((StairBlock) ModBlocks.SKY_STAIRS.get()), blockTexture(ModBlocks.SKY_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.SKY_SLAB.get()), blockTexture(ModBlocks.SKY_PLANKS.get()), blockTexture(ModBlocks.SKY_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.SKY_BUTTON.get()), blockTexture(ModBlocks.SKY_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.SKY_PRESSURE_PLATE.get()), blockTexture(ModBlocks.SKY_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.SKY_FENCE.get()), blockTexture(ModBlocks.SKY_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.SKY_FENCE_GATE.get()), blockTexture(ModBlocks.SKY_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock) ModBlocks.SKY_DOOR.get()), modLoc("block/sky_door_bottom"),modLoc("block/sky_door_top"),"cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.SKY_TRAPDOOR.get()), modLoc("block/sky_trapdoor"),true,"cutout");
        //~~~~~~~~~palm wood~~~~~~~~\\
        blockWithItem(ModBlocks.PALM_PLANKS);
        logBlock((RotatedPillarBlock) ModBlocks.PALM_LOG.get());
        logBlock((RotatedPillarBlock) ModBlocks.STRIPED_PALM_LOG.get());
        axisBlock(((RotatedPillarBlock) ModBlocks.PALM_WOOD.get()), blockTexture(ModBlocks.PALM_LOG.get()),blockTexture(ModBlocks.PALM_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPED_PALM_WOOD.get()), blockTexture(ModBlocks.STRIPED_PALM_LOG.get()),blockTexture(ModBlocks.STRIPED_PALM_LOG.get()));
        blockWithItem(ModBlocks.PALM_LEAVES);
        saplingBlock(ModBlocks.PALM_SAPLING);
        simpleBlockItem(ModBlocks.PALM_LOG.get(),models().withExistingParent("extrabiomes:palm_log", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.PALM_WOOD.get(),models().withExistingParent("extrabiomes:palm_wood", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.STRIPED_PALM_LOG.get(),models().withExistingParent("extrabiomes:striped_palm_log", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.STRIPED_PALM_WOOD.get(),models().withExistingParent("extrabiomes:striped_palm_wood", "minecraft:block/cube_column"));
        stairsBlock(((StairBlock) ModBlocks.PALM_STAIRS.get()), blockTexture(ModBlocks.PALM_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.PALM_SLAB.get()), blockTexture(ModBlocks.PALM_PLANKS.get()), blockTexture(ModBlocks.PALM_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.PALM_BUTTON.get()), blockTexture(ModBlocks.PALM_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.PALM_PRESSURE_PLATE.get()), blockTexture(ModBlocks.PALM_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.PALM_FENCE.get()), blockTexture(ModBlocks.PALM_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.PALM_FENCE_GATE.get()), blockTexture(ModBlocks.PALM_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock) ModBlocks.PALM_DOOR.get()), modLoc("block/palm_door_bottom"),modLoc("block/palm_door_top"),"cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.PALM_TRAPDOOR.get()), modLoc("block/palm_trapdoor"),true,"cutout");
        //~~~~~~~~~GILDED_SKY wood~~~~~~~~\\
        blockWithItem(ModBlocks.GILDED_SKY_PLANKS);
        logBlock((RotatedPillarBlock) ModBlocks.GILDED_SKY_LOG.get());
        axisBlock(((RotatedPillarBlock) ModBlocks.GILDED_SKY_WOOD.get()), blockTexture(ModBlocks.GILDED_SKY_LOG.get()),blockTexture(ModBlocks.GILDED_SKY_LOG.get()));
        simpleBlockItem(ModBlocks.GILDED_SKY_LOG.get(),models().withExistingParent("extrabiomes:gilded_sky_log", "minecraft:block/cube_column"));
        simpleBlockItem(ModBlocks.GILDED_SKY_WOOD.get(),models().withExistingParent("extrabiomes:gilded_sky_wood", "minecraft:block/cube_column"));
        stairsBlock(((StairBlock) ModBlocks.GILDED_SKY_STAIRS.get()), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.GILDED_SKY_SLAB.get()), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.GILDED_SKY_BUTTON.get()), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.GILDED_SKY_PRESSURE_PLATE.get()), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.GILDED_SKY_FENCE.get()), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.GILDED_SKY_FENCE_GATE.get()), blockTexture(ModBlocks.GILDED_SKY_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock) ModBlocks.GILDED_SKY_DOOR.get()), modLoc("block/gilded_sky_door_bottom"),modLoc("block/gilded_sky_door_top"),"cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.GILDED_SKY_TRAPDOOR.get()), modLoc("block/gilded_sky_trapdoor"),true,"cutout");
    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(),cubeAll(blockRegistryObject.get()));
    }

    private void saplingBlock(RegistryObject<Block> blockRegistryObject){
        simpleBlock(blockRegistryObject.get(),models().cross(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get())).getPath(),blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }
}
