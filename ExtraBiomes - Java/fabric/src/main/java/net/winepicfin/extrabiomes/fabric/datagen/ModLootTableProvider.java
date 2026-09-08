package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.winepicfin.extrabiomes.fabric.datagen.loot.ModBlockLootTables;
import net.winepicfin.extrabiomes.fabric.datagen.loot.ModEntityLootTables;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// Same vanilla net.minecraft.data.loot.LootTableProvider Forge uses (forge/datagen/ModLootTableProvider.java) -
// no Forge-specific API involved, so this is a straight port. The Fabric entrypoint registers this via
// Pack.addProvider(Factory<T>), whose factory signature accepts a FabricDataOutput - since
// FabricDataOutput extends PackOutput, a method reference to this create(PackOutput) method satisfies
// that functional interface without any changes.
public class ModLootTableProvider {
    // Takes FabricDataOutput specifically (not the plain PackOutput vanilla's own LootTableProvider
    // constructor accepts) purely to disambiguate Pack.addProvider's overload resolution - a
    // PackOutput-accepting factory matches both vanilla's DataProvider.Factory and Fabric's own
    // Pack.Factory equally well, which javac rejects as ambiguous.
    public static LootTableProvider create(FabricDataOutput out, CompletableFuture<HolderLookup.Provider> registriesFuture){
        return new LootTableProvider(out, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(registries -> new ModBlockLootTables(out, registriesFuture), LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(registries -> new ModEntityLootTables(out, registriesFuture), LootContextParamSets.ENTITY)
        ), registriesFuture);
    }
}
