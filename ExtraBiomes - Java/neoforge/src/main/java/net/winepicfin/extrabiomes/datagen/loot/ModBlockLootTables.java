package net.winepicfin.extrabiomes.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.commondatagen.loot.ModBlockLootTableEntries;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        ModBlockLootTableEntries.populate(
                this::dropSelf,
                this::add,
                this::createSlabItemTable,
                this::createDoorTable,
                (leavesBlock, saplingBlock) -> createLeavesDrops(leavesBlock, saplingBlock, NORMAL_LEAVES_SAPLING_CHANCES),
                this::createOreDrop,
                this::createSingleItemTable,
                this::createMushroomBlockDrop);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks(){
        return java.util.stream.StreamSupport.stream(ModBlocks.BLOCKS.spliterator(), false).map(RegistrySupplier::get)::iterator;
    }
}
