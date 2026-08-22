package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

// Fabric equivalent of forge/datagen/ModWorldGenProvider.java's *file-writing* half. Forge's
// DatapackBuiltinEntriesProvider both registers this mod's dynamic registry entries (biomes, features,
// carvers, noise) AND writes them to JSON in one class. On Fabric those two jobs are split: the
// registration half lives in FabricDataGenerators.buildRegistry(RegistrySetBuilder) (mirroring
// ModWorldGenProvider.BUILDER), and this class is only the writer, using Fabric API's
// FabricDynamicRegistryProvider (entries.addAll(...) filters by this mod's namespace automatically, the
// same effect as Forge's Set.of(ExtraBiomes.MOD_ID) target-namespaces argument). Forge's registry entry
// for ForgeRegistries.Keys.BIOME_MODIFIERS is intentionally NOT mirrored here - Fabric has no
// datapack-driven biome modifier registry; the equivalent logic already runs as code via
// net.winepicfin.extrabiomes.fabric.worldgen.FabricBiomeModifiers, registered directly from the mod
// entrypoint (see ExtraBiomesFabric), not through datagen.
public class ModDynamicRegistryProvider extends FabricDynamicRegistryProvider {
    public ModDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_CARVER));
        entries.addAll(registries.lookupOrThrow(Registries.NOISE));
        entries.addAll(registries.lookupOrThrow(Registries.BIOME));
    }

    @Override
    public String getName() {
        return "ExtraBiomes Dynamic Registries";
    }
}
