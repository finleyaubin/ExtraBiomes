package net.winepicfin.extrabiomes.datagen.Loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.block.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.DENSE_CLOUD.get());
        this.dropSelf(ModBlocks.DENSE_CLOUD_BRICK.get());

        this.add(ModBlocks.NETHER_DIAMOND_ORE.get(),block -> createOreDrop(ModBlocks.NETHER_DIAMOND_ORE.get(), Items.DIAMOND));

        //~~~~~~~~~~~~~Mystic Wood~~~~~~~~~~\\
        this.dropSelf(ModBlocks.MYSTIC_PLANKS.get());
        this.dropSelf(ModBlocks.MYSTIC_LOG.get());
        this.dropSelf(ModBlocks.MYSTIC_WOOD.get());
        this.dropSelf(ModBlocks.STRIPED_MYSTIC_LOG.get());
        this.dropSelf(ModBlocks.STRIPED_MYSTIC_WOOD.get());
        this.dropSelf(ModBlocks.MYSTIC_STAIRS.get());
        this.dropSelf(ModBlocks.MYSTIC_BUTTON.get());
        this.dropSelf(ModBlocks.MYSTIC_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.MYSTIC_TRAPDOOR.get());
        this.dropSelf(ModBlocks.MYSTIC_FENCE.get());
        this.dropSelf(ModBlocks.MYSTIC_FENCE_GATE.get());
        this.add(ModBlocks.MYSTIC_SLAB.get(), block -> createSlabItemTable(ModBlocks.MYSTIC_SLAB.get()));
        this.add(ModBlocks.MYSTIC_DOOR.get(), block -> createDoorTable(ModBlocks.MYSTIC_DOOR.get()));
        this.dropSelf(ModBlocks.MYSTIC_SAPLING.get());
        this.add(ModBlocks.MYSTIC_LEAVES.get(), (block -> createLeavesDrops(block, ModBlocks.MYSTIC_LEAVES.get(), NORMAL_LEAVES_SAPLING_CHANCES)));
        //~~~~~~~~~~~~~Sky Wood~~~~~~~~~~\\
        this.dropSelf(ModBlocks.SKY_PLANKS.get());
        this.dropSelf(ModBlocks.SKY_LOG.get());
        this.dropSelf(ModBlocks.SKY_WOOD.get());
        this.dropSelf(ModBlocks.STRIPED_SKY_LOG.get());
        this.dropSelf(ModBlocks.STRIPED_SKY_WOOD.get());
        this.dropSelf(ModBlocks.SKY_STAIRS.get());
        this.dropSelf(ModBlocks.SKY_BUTTON.get());
        this.dropSelf(ModBlocks.SKY_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.SKY_TRAPDOOR.get());
        this.dropSelf(ModBlocks.SKY_FENCE.get());
        this.dropSelf(ModBlocks.SKY_FENCE_GATE.get());
        this.add(ModBlocks.SKY_SLAB.get(), block -> createSlabItemTable(ModBlocks.SKY_SLAB.get()));
        this.add(ModBlocks.SKY_DOOR.get(), block -> createDoorTable(ModBlocks.SKY_DOOR.get()));
        this.dropSelf(ModBlocks.SKY_SAPLING.get());
        this.add(ModBlocks.SKY_LEAVES.get(), (block -> createLeavesDrops(block, ModBlocks.SKY_LEAVES.get(), NORMAL_LEAVES_SAPLING_CHANCES)));
    }


    @Override
    protected @NotNull Iterable<Block> getKnownBlocks(){
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
