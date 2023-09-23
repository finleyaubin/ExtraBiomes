package net.winepicfin.extrabiomes.datagen.Loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
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
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks(){
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
