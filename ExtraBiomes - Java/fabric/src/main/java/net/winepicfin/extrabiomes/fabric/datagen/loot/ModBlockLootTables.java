package net.winepicfin.extrabiomes.fabric.datagen.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.winepicfin.extrabiomes.commondatagen.loot.ModBlockLootTableEntries;

// Fabric wiring for the shared block loot table entries in
// net.winepicfin.extrabiomes.commondatagen.loot.ModBlockLootTableEntries (common) - see that class's
// javadoc for why the entries live there instead of here directly. Unlike Forge (whose patched
// BlockLootSubProvider takes a getKnownBlocks() override to scope its "did every block get a loot
// table" completeness check to this mod's own blocks), plain vanilla's BlockLootSubProvider
// hardcodes that check against the ENTIRE BuiltInRegistries.BLOCK registry - extending it directly
// on Fabric throws "Missing loottable 'minecraft:blocks/stone' for 'minecraft:stone'" (and for every
// other vanilla/other-mod block) since nothing here generates tables for blocks that aren't ours.
// Fabric API's own FabricBlockLootTableProvider is the documented fix: it overrides that same
// completeness check to scope by FabricDataOutput#getModId() instead of the full registry.
public class ModBlockLootTables extends FabricBlockLootTableProvider {
    public ModBlockLootTables(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
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
}
