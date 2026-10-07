package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.commondatagen.ModTagContents;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    // ItemTags has no STAIRS/SLABS constants in 26.x, but the vanilla tags still exist (block tags of the same name have constants).
    private static final TagKey<Item> ITEM_STAIRS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("stairs"));
    private static final TagKey<Item> ITEM_SLABS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("slabs"));

    public ModItemTagGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, ExtraBiomes.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //this.tag(ItemTags.TRIMMABLE_ARMOR).add(ModItems.FROG_HELMET.get()); does now work with the gecolib model

        this.tag(ItemTags.WOODEN_FENCES).add(
                ModBlocks.MYSTIC_FENCE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_FENCE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_FENCE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_FENCE.get().asItem().builtInRegistryHolder().key()
        );

        this.tag(ItemTags.FENCE_GATES).add(
                ModBlocks.MYSTIC_FENCE_GATE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_FENCE_GATE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_FENCE_GATE.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get().asItem().builtInRegistryHolder().key()
        );

        this.tag(ItemTags.LOGS).add(
                ModBlocks.MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_PALM_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_PALM_WOOD.get().asItem().builtInRegistryHolder().key()
        );
        this.tag(ItemTags.LOGS_THAT_BURN).add(
                ModBlocks.MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_PALM_LOG.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_WOOD.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.STRIPPED_PALM_WOOD.get().asItem().builtInRegistryHolder().key()
        );
        this.tag(ItemTags.LEAVES).add(
                ModBlocks.MYSTIC_LEAVES.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_LEAVES.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_LEAVES.get().asItem().builtInRegistryHolder().key()
        );
        this.tag(ItemTags.PLANKS).add(
                ModBlocks.MYSTIC_PLANKS.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.SKY_PLANKS.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.PALM_PLANKS.get().asItem().builtInRegistryHolder().key(),
                ModBlocks.GILDED_SKY_PLANKS.get().asItem().builtInRegistryHolder().key()
        );

        this.tag(ItemTags.BOATS).add(ModItems.BOAT_ITEMS.stream().map(RegistrySupplier::getKey).toArray(ResourceKey[]::new));
        this.tag(ItemTags.CHEST_BOATS).add(ModItems.CHEST_BOAT_ITEMS.stream().map(RegistrySupplier::getKey).toArray(ResourceKey[]::new));

        this.tag(ItemTags.WALLS).add(itemKeys(ModTagContents.WALLS));
        this.tag(ITEM_STAIRS).add(itemKeys(ModTagContents.STONE_STAIRS));
        this.tag(ITEM_SLABS).add(itemKeys(ModTagContents.STONE_SLABS));
        this.tag(ItemTags.WOODEN_STAIRS).add(itemKeys(ModTagContents.WOODEN_STAIRS));
        this.tag(ItemTags.WOODEN_SLABS).add(itemKeys(ModTagContents.WOODEN_SLABS));
        this.tag(ItemTags.WOODEN_BUTTONS).add(itemKeys(ModTagContents.WOODEN_BUTTONS));
        this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(itemKeys(ModTagContents.WOODEN_PRESSURE_PLATES));
        this.tag(ItemTags.WOODEN_DOORS).add(itemKeys(ModTagContents.WOODEN_DOORS));
        this.tag(ItemTags.WOODEN_TRAPDOORS).add(itemKeys(ModTagContents.WOODEN_TRAPDOORS));
        this.tag(ItemTags.SIGNS).add(ModTagContents.SIGN_ITEMS.stream().map(RegistrySupplier::getKey).toArray(ResourceKey[]::new));
        this.tag(ItemTags.HANGING_SIGNS).add(ModTagContents.HANGING_SIGN_ITEMS.stream().map(RegistrySupplier::getKey).toArray(ResourceKey[]::new));
        this.tag(ItemTags.SAPLINGS).add(itemKeys(ModTagContents.SAPLINGS));
        this.tag(ItemTags.SAND).add(itemKeys(ModTagContents.SAND));

        this.tag(Tags.Items.STRIPPED_LOGS).add(itemKeys(ModTagContents.STRIPPED_LOGS));
        this.tag(Tags.Items.STRIPPED_WOODS).add(itemKeys(ModTagContents.STRIPPED_WOODS));
        this.tag(Tags.Items.ORES).add(itemKeys(ModTagContents.ORES));
    }

    @SuppressWarnings("unchecked")
    private static ResourceKey<Item>[] itemKeys(List<RegistrySupplier<Block>> blocks) {
        return blocks.stream().map(b -> b.get().asItem().builtInRegistryHolder().key()).toArray(ResourceKey[]::new);
    }
}
