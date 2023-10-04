package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fluids.IFluidBlock;
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

    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(),cubeAll(blockRegistryObject.get()));
    }

    private void saplingBlock(RegistryObject<Block> blockRegistryObject){
        simpleBlock(blockRegistryObject.get(),models().cross(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get())).getPath(),blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }
}
