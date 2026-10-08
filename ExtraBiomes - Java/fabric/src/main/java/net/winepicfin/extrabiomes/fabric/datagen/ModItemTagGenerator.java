package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.commondatagen.ModTagContents;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// Fabric port of forge/datagen/ModItemTagGenerator.java, using Fabric API's
// FabricTagProvider.ItemTagProvider (equivalent of Forge's ItemTagsProvider convenience class). Takes
// the sibling ModBlockTagGenerator so ItemTagProvider.copy(...) is available if ever needed, matching
// how Forge's version was constructed with the block tag provider's contentsGetter().
public class ModItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {

    public ModItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, ModBlockTagGenerator blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //this.tag(ItemTags.TRIMMABLE_ARMOR).add(ModItems.FROG_HELMET.get()); does now work with the gecolib model

        this.tag(ItemTags.WOODEN_FENCES).add(keys(
                ModBlocks.MYSTIC_FENCE.get().asItem(),
                ModBlocks.SKY_FENCE.get().asItem(),
                ModBlocks.PALM_FENCE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE.get().asItem()
        ));

        this.tag(ItemTags.FENCE_GATES).add(keys(
                ModBlocks.MYSTIC_FENCE_GATE.get().asItem(),
                ModBlocks.SKY_FENCE_GATE.get().asItem(),
                ModBlocks.PALM_FENCE_GATE.get().asItem(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get().asItem()
        ));

        this.tag(ItemTags.LOGS).add(keys(
                ModBlocks.MYSTIC_LOG.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem(),
                ModBlocks.MYSTIC_WOOD.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem(),
                ModBlocks.SKY_LOG.get().asItem(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem(),
                ModBlocks.SKY_WOOD.get().asItem(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem(),
                ModBlocks.GILDED_SKY_LOG.get().asItem(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.get().asItem(),
                ModBlocks.GILDED_SKY_WOOD.get().asItem(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.get().asItem(),
                ModBlocks.PALM_LOG.get().asItem(),
                ModBlocks.STRIPPED_PALM_LOG.get().asItem(),
                ModBlocks.PALM_WOOD.get().asItem(),
                ModBlocks.STRIPPED_PALM_WOOD.get().asItem()
        ));
        this.tag(ItemTags.LOGS_THAT_BURN).add(keys(
                ModBlocks.MYSTIC_LOG.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem(),
                ModBlocks.MYSTIC_WOOD.get().asItem(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem(),
                ModBlocks.SKY_LOG.get().asItem(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem(),
                ModBlocks.SKY_WOOD.get().asItem(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem(),
                ModBlocks.GILDED_SKY_LOG.get().asItem(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.get().asItem(),
                ModBlocks.GILDED_SKY_WOOD.get().asItem(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.get().asItem(),
                ModBlocks.PALM_LOG.get().asItem(),
                ModBlocks.STRIPPED_PALM_LOG.get().asItem(),
                ModBlocks.PALM_WOOD.get().asItem(),
                ModBlocks.STRIPPED_PALM_WOOD.get().asItem()
        ));
        this.tag(ItemTags.LEAVES).add(keys(
                ModBlocks.MYSTIC_LEAVES.get().asItem(),
                ModBlocks.SKY_LEAVES.get().asItem(),
                ModBlocks.PALM_LEAVES.get().asItem()
        ));
        this.tag(ItemTags.PLANKS).add(keys(
                ModBlocks.MYSTIC_PLANKS.get().asItem(),
                ModBlocks.SKY_PLANKS.get().asItem(),
                ModBlocks.PALM_PLANKS.get().asItem(),
                ModBlocks.GILDED_SKY_PLANKS.get().asItem()
        ));

        this.tag(ItemTags.BOATS).add(keys(ModItems.BOAT_ITEMS.stream().map(RegistrySupplier::get).toArray(net.minecraft.world.item.Item[]::new)));
        this.tag(ItemTags.CHEST_BOATS).add(keys(ModItems.CHEST_BOAT_ITEMS.stream().map(RegistrySupplier::get).toArray(net.minecraft.world.item.Item[]::new)));

        this.tag(ItemTags.WALLS).add(itemKeys(ModTagContents.WALLS));
        this.tag(ITEM_STAIRS).add(itemKeys(ModTagContents.STONE_STAIRS));
        this.tag(ITEM_SLABS).add(itemKeys(ModTagContents.STONE_SLABS));
        this.tag(ItemTags.WOODEN_STAIRS).add(itemKeys(ModTagContents.WOODEN_STAIRS));
        this.tag(ItemTags.WOODEN_SLABS).add(itemKeys(ModTagContents.WOODEN_SLABS));
        this.tag(ItemTags.WOODEN_BUTTONS).add(itemKeys(ModTagContents.WOODEN_BUTTONS));
        this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(itemKeys(ModTagContents.WOODEN_PRESSURE_PLATES));
        this.tag(ItemTags.WOODEN_DOORS).add(itemKeys(ModTagContents.WOODEN_DOORS));
        this.tag(ItemTags.WOODEN_TRAPDOORS).add(itemKeys(ModTagContents.WOODEN_TRAPDOORS));
        this.tag(ItemTags.SIGNS).add(ModTagContents.SIGN_ITEMS.stream().map(RegistrySupplier::get).map(item -> item.builtInRegistryHolder().key()).toArray(ResourceKey[]::new));
        this.tag(ItemTags.HANGING_SIGNS).add(ModTagContents.HANGING_SIGN_ITEMS.stream().map(RegistrySupplier::get).map(item -> item.builtInRegistryHolder().key()).toArray(ResourceKey[]::new));
        this.tag(ItemTags.SAPLINGS).add(itemKeys(ModTagContents.SAPLINGS));
        this.tag(ItemTags.SAND).add(itemKeys(ModTagContents.SAND));

        this.tag(ConventionalItemTags.STRIPPED_LOGS).add(itemKeys(ModTagContents.STRIPPED_LOGS));
        this.tag(ConventionalItemTags.STRIPPED_WOODS).add(itemKeys(ModTagContents.STRIPPED_WOODS));
        this.tag(ConventionalItemTags.ORES).add(itemKeys(ModTagContents.ORES));
    }

    @SuppressWarnings("unchecked")
    private static ResourceKey<net.minecraft.world.item.Item>[] itemKeys(List<RegistrySupplier<Block>> blocks) {
        return blocks.stream().map(b -> b.get().asItem().builtInRegistryHolder().key()).toArray(ResourceKey[]::new);
    }

    // ItemTags has no STAIRS/SLABS constants in 26.x, but the vanilla tags still exist (block tags of the same name have constants).
    private static final TagKey<net.minecraft.world.item.Item> ITEM_STAIRS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("stairs"));
    private static final TagKey<net.minecraft.world.item.Item> ITEM_SLABS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("slabs"));

    // TagAppender.add() takes ResourceKey<Item>, not Item, so this maps each item to its registry key.
    @SafeVarargs
    private static ResourceKey<net.minecraft.world.item.Item>[] keys(net.minecraft.world.item.Item... items) {
        ResourceKey<net.minecraft.world.item.Item>[] result = new ResourceKey[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = items[i].builtInRegistryHolder().key();
        }
        return result;
    }
}
