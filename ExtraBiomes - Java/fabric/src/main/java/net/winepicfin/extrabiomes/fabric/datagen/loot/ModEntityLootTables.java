package net.winepicfin.extrabiomes.fabric.datagen.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.winepicfin.extrabiomes.commondatagen.loot.ModEntityLootTableEntries;

import java.util.concurrent.CompletableFuture;

// Fabric wiring for the shared entity loot table entries in
// net.winepicfin.extrabiomes.commondatagen.loot.ModEntityLootTableEntries (common) - see that class's
// javadoc for why the entries live there instead of here directly. FabricEntityLootSubProvider scopes
// EntityLootSubProvider's completeness check to this mod's own entities (same fix
// FabricBlockLootSubProvider applies for blocks), so unlike before this can extend it directly instead
// of sidestepping EntityLootSubProvider with a raw BiConsumer callback.
public class ModEntityLootTables extends FabricEntityLootSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;

    public ModEntityLootTables(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
        this.registriesFuture = registriesFuture;
    }

    @Override
    public void generate() {
        ModEntityLootTableEntries.populate(registriesFuture.join(), this::add);
    }
}
