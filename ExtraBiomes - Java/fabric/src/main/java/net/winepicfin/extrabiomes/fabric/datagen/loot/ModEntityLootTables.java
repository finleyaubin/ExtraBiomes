package net.winepicfin.extrabiomes.fabric.datagen.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.winepicfin.extrabiomes.commondatagen.loot.ModEntityLootTableEntries;

import java.util.function.BiConsumer;

// Fabric wiring for the shared entity loot table entries in
// net.winepicfin.extrabiomes.commondatagen.loot.ModEntityLootTableEntries (common) - see that class's
// javadoc for why the entries live there instead of here directly. Unlike Forge, plain vanilla's
// EntityLootSubProvider.generate(BiConsumer) hardcodes a completeness check against the ENTIRE
// BuiltInRegistries.ENTITY_TYPE registry (throws "Missing loottable 'minecraft:entities/allay' for
// 'minecraft:allay'" - and every other vanilla/other-mod entity - since nothing here generates
// tables for entities that aren't ours), the same problem ModBlockLootTables hit. Fabric API has no
// entity-specific scoped provider (unlike FabricBlockLootTableProvider for blocks), so this instead
// extends the generic SimpleFabricLootTableProvider and implements the raw BiConsumer callback
// directly, sidestepping EntityLootSubProvider (and its completeness check) entirely.
public class ModEntityLootTables extends SimpleFabricLootTableProvider {
    public ModEntityLootTables(FabricDataOutput output) {
        super(output, LootContextParamSets.ENTITY);
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        ModEntityLootTableEntries.populate((entityType, builder) -> consumer.accept(entityType.getDefaultLootTable(), builder));
    }
}
