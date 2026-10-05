package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    // no longer exposed as a BlockTags constant in 26.2, but the vanilla tag data still exists
    private static final TagKey<Block> LOGS_THAT_BURN =
            TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("logs_that_burn"));

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ExtraBiomes.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        this.tag(BlockTags.NEEDS_STONE_TOOL).add(
                ModBlocks.NETHER_COPPER_ORE.getKey(),
                ModBlocks.NETHER_IRON_ORE.getKey(),
                ModBlocks.NETHER_LAPIS_ORE.getKey()
        );

        this.tag(BlockTags.NEEDS_IRON_TOOL).add(
                ModBlocks.NETHER_EMERALD_ORE.getKey(),
                ModBlocks.NETHER_REDSTONE_ORE.getKey(),
                ModBlocks.NETHER_DIAMOND_ORE.getKey()
        );

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.DENSE_CLOUD_BRICK.getKey(),
                ModBlocks.DENSE_CLOUD_BRICK_SLAB.getKey(),
                ModBlocks.DENSE_CLOUD_BRICK_STAIRS.getKey(),
                ModBlocks.NETHER_DIAMOND_ORE.getKey(),
                ModBlocks.NETHER_COAL_ORE.getKey(),
                ModBlocks.NETHER_COPPER_ORE.getKey(),
                ModBlocks.NETHER_EMERALD_ORE.getKey(),
                ModBlocks.NETHER_IRON_ORE.getKey(),
                ModBlocks.NETHER_LAPIS_ORE.getKey(),
                ModBlocks.NETHER_REDSTONE_ORE.getKey(),
                ResourceKey.create(Registries.BLOCK, ModBlocks.PEBBLE.getId()),
                ResourceKey.create(Registries.BLOCK, ModBlocks.MOSSY_PEBBLE.getId()),
                ModBlocks.GRASS_STONE.getKey(),
                ModBlocks.BLACK_SANDSTONE.getKey(),
                ModBlocks.CHISELED_BLACK_SANDSTONE.getKey(),
                ModBlocks.CUT_BLACK_SANDSTONE.getKey(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE.getKey(),
                ModBlocks.BLACK_SANDSTONE_SLAB.getKey(),
                ModBlocks.CUT_BLACK_SANDSTONE_SLAB.getKey(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.getKey(),
                ModBlocks.BLACK_SANDSTONE_STAIRS.getKey(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.getKey(),
                ModBlocks.BLACK_SANDSTONE_WALL.getKey()
        );

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                ModBlocks.STICK_PILE.getKey(),
                // mystic wood
                ModBlocks.MYSTIC_PLANKS.getKey(),
                ModBlocks.MYSTIC_STAIRS.getKey(),
                ModBlocks.MYSTIC_SLAB.getKey(),
                ModBlocks.MYSTIC_BUTTON.getKey(),
                ModBlocks.MYSTIC_PRESSURE_PLATE.getKey(),
                ModBlocks.MYSTIC_FENCE.getKey(),
                ModBlocks.MYSTIC_FENCE_GATE.getKey(),
                ModBlocks.MYSTIC_DOOR.getKey(),
                ModBlocks.MYSTIC_TRAPDOOR.getKey(),
                ModBlocks.MYSTIC_SIGN.getKey(),
                ModBlocks.MYSTIC_HANGING_SIGN.getKey(),
                ModBlocks.MYSTIC_WALL_SIGN.getKey(),
                ModBlocks.MYSTIC_WALL_HANGING_SIGN.getKey(),
                // sky wood
                ModBlocks.SKY_PLANKS.getKey(),
                ModBlocks.SKY_STAIRS.getKey(),
                ModBlocks.SKY_SLAB.getKey(),
                ModBlocks.SKY_BUTTON.getKey(),
                ModBlocks.SKY_PRESSURE_PLATE.getKey(),
                ModBlocks.SKY_FENCE.getKey(),
                ModBlocks.SKY_FENCE_GATE.getKey(),
                ModBlocks.SKY_DOOR.getKey(),
                ModBlocks.SKY_TRAPDOOR.getKey(),
                ModBlocks.SKY_SIGN.getKey(),
                ModBlocks.SKY_HANGING_SIGN.getKey(),
                ModBlocks.SKY_WALL_SIGN.getKey(),
                ModBlocks.SKY_WALL_HANGING_SIGN.getKey(),
                // palm wood
                ModBlocks.PALM_PLANKS.getKey(),
                ModBlocks.PALM_STAIRS.getKey(),
                ModBlocks.PALM_SLAB.getKey(),
                ModBlocks.PALM_BUTTON.getKey(),
                ModBlocks.PALM_PRESSURE_PLATE.getKey(),
                ModBlocks.PALM_FENCE.getKey(),
                ModBlocks.PALM_FENCE_GATE.getKey(),
                ModBlocks.PALM_DOOR.getKey(),
                ModBlocks.PALM_TRAPDOOR.getKey(),
                ModBlocks.PALM_SIGN.getKey(),
                ModBlocks.PALM_HANGING_SIGN.getKey(),
                ModBlocks.PALM_WALL_SIGN.getKey(),
                ModBlocks.PALM_WALL_HANGING_SIGN.getKey(),
                // Gilded_sky wood
                ModBlocks.GILDED_SKY_PLANKS.getKey(),
                ModBlocks.GILDED_SKY_STAIRS.getKey(),
                ModBlocks.GILDED_SKY_SLAB.getKey(),
                ModBlocks.GILDED_SKY_BUTTON.getKey(),
                ModBlocks.GILDED_SKY_PRESSURE_PLATE.getKey(),
                ModBlocks.GILDED_SKY_FENCE.getKey(),
                ModBlocks.GILDED_SKY_FENCE_GATE.getKey(),
                ModBlocks.GILDED_SKY_DOOR.getKey(),
                ModBlocks.GILDED_SKY_TRAPDOOR.getKey(),
                ModBlocks.GILDED_SKY_SIGN.getKey(),
                ModBlocks.GILDED_SKY_HANGING_SIGN.getKey(),
                ModBlocks.GILDED_SKY_WALL_SIGN.getKey(),
                ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.getKey()
        );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                ModBlocks.BLACK_SAND.getKey()
        );
        this.tag(BlockTags.MINEABLE_WITH_HOE);
        this.tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(
                ModBlocks.DENSE_CLOUD.getKey()
        );

        this.tag(BlockTags.WOODEN_FENCES).add(
                ModBlocks.MYSTIC_FENCE.getKey(),
                ModBlocks.SKY_FENCE.getKey(),
                ModBlocks.PALM_FENCE.getKey(),
                ModBlocks.GILDED_SKY_FENCE.getKey()
        );

        this.tag(BlockTags.FENCE_GATES).add(
                ModBlocks.MYSTIC_FENCE_GATE.getKey(),
                ModBlocks.SKY_FENCE_GATE.getKey(),
                ModBlocks.PALM_FENCE_GATE.getKey(),
                ModBlocks.GILDED_SKY_FENCE_GATE.getKey()
        );

        this.tag(BlockTags.LOGS).add(
                ModBlocks.MYSTIC_LOG.getKey(),
                ModBlocks.STRIPPED_MYSTIC_LOG.getKey(),
                ModBlocks.MYSTIC_WOOD.getKey(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.getKey(),
                ModBlocks.PALM_LOG.getKey(),
                ModBlocks.STRIPPED_PALM_LOG.getKey(),
                ModBlocks.PALM_WOOD.getKey(),
                ModBlocks.STRIPPED_PALM_WOOD.getKey(),
                ModBlocks.SKY_LOG.getKey(),
                ModBlocks.STRIPPED_SKY_LOG.getKey(),
                ModBlocks.SKY_WOOD.getKey(),
                ModBlocks.STRIPPED_SKY_WOOD.getKey(),
                ModBlocks.GILDED_SKY_LOG.getKey(),
                ModBlocks.GILDED_SKY_WOOD.getKey(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.getKey(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.getKey()
        );
        this.tag(LOGS_THAT_BURN).add(
                ModBlocks.MYSTIC_LOG.getKey(),
                ModBlocks.STRIPPED_MYSTIC_LOG.getKey(),
                ModBlocks.MYSTIC_WOOD.getKey(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.getKey(),
                ModBlocks.PALM_LOG.getKey(),
                ModBlocks.STRIPPED_PALM_LOG.getKey(),
                ModBlocks.PALM_WOOD.getKey(),
                ModBlocks.STRIPPED_PALM_WOOD.getKey(),
                ModBlocks.SKY_LOG.getKey(),
                ModBlocks.STRIPPED_SKY_LOG.getKey(),
                ModBlocks.SKY_WOOD.getKey(),
                ModBlocks.STRIPPED_SKY_WOOD.getKey(),
                ModBlocks.GILDED_SKY_LOG.getKey(),
                ModBlocks.GILDED_SKY_WOOD.getKey(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.getKey(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.getKey()
        );
        this.tag(BlockTags.LEAVES).add(
                ModBlocks.MYSTIC_LEAVES.getKey(),
                ModBlocks.SKY_LEAVES.getKey(),
                ModBlocks.PALM_LEAVES.getKey()
        );
        this.tag(BlockTags.PLANKS).add(
                ModBlocks.MYSTIC_PLANKS.getKey(),
                ModBlocks.SKY_PLANKS.getKey(),
                ModBlocks.PALM_PLANKS.getKey(),
                ModBlocks.GILDED_SKY_PLANKS.getKey()
        );

        this.tag(BlockTags.WALLS).add(
                ModBlocks.BLACK_SANDSTONE_WALL.getKey()
        );
        this.tag(BlockTags.STAIRS).add(
                ModBlocks.BLACK_SANDSTONE_STAIRS.getKey(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.getKey()
        );
        this.tag(BlockTags.SLABS).add(
                ModBlocks.BLACK_SANDSTONE_SLAB.getKey(),
                ModBlocks.CUT_BLACK_SANDSTONE_SLAB.getKey(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.getKey()
        );
    }
}
