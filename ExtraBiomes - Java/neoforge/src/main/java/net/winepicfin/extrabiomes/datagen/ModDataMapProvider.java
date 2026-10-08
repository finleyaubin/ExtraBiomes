package net.winepicfin.extrabiomes.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Transformable;
import net.winepicfin.extrabiomes.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        var transformables = builder(NeoForgeDataMaps.TRANSFORMABLES);
        strip(transformables, ModBlocks.MYSTIC_LOG, ModBlocks.STRIPPED_MYSTIC_LOG);
        strip(transformables, ModBlocks.MYSTIC_WOOD, ModBlocks.STRIPPED_MYSTIC_WOOD);
        strip(transformables, ModBlocks.SKY_LOG, ModBlocks.STRIPPED_SKY_LOG);
        strip(transformables, ModBlocks.SKY_WOOD, ModBlocks.STRIPPED_SKY_WOOD);
        strip(transformables, ModBlocks.GILDED_SKY_LOG, ModBlocks.STRIPPED_GILDED_SKY_LOG);
        strip(transformables, ModBlocks.GILDED_SKY_WOOD, ModBlocks.STRIPPED_GILDED_SKY_WOOD);
        strip(transformables, ModBlocks.PALM_LOG, ModBlocks.STRIPPED_PALM_LOG);
        strip(transformables, ModBlocks.PALM_WOOD, ModBlocks.STRIPPED_PALM_WOOD);
    }

    private static void strip(DataMapProvider.Builder<Transformable, Block> builder, RegistrySupplier<Block> log, RegistrySupplier<Block> stripped) {
        builder.add(log.getKey(), Transformable.stripping(log.get(), stripped.get()), false);
    }
}
