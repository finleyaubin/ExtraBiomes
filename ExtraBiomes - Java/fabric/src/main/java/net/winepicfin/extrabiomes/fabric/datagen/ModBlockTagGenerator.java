package net.winepicfin.extrabiomes.fabric.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.commondatagen.ModTagContents;
import java.util.List;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.winepicfin.extrabiomes.block.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Fabric port of forge/datagen/ModBlockTagGenerator.java. Fabric API's FabricTagProvider.BlockTagProvider
// is the direct equivalent of Forge's net.minecraftforge.common.data.BlockTagsProvider convenience class
// (both just wrap vanilla TagsProvider<Block> with a computed "blocks" tag directory). Forge's
// Tags.Blocks.NEEDS_WOOD_TOOL (a Forge-only common convention tag) has no Fabric Convention Tags
// equivalent wired up elsewhere in this mod, so it's dropped here rather than guessing at one.
public class ModBlockTagGenerator extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        this.tag(BlockTags.NEEDS_STONE_TOOL).add(keys(
                ModBlocks.NETHER_COPPER_ORE.get(),
                ModBlocks.NETHER_IRON_ORE.get(),
                ModBlocks.NETHER_LAPIS_ORE.get()
        ));

        this.tag(BlockTags.NEEDS_IRON_TOOL).add(keys(
                ModBlocks.NETHER_EMERALD_ORE.get(),
                ModBlocks.NETHER_REDSTONE_ORE.get(),
                ModBlocks.NETHER_DIAMOND_ORE.get()
        ));

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(keys(
                ModBlocks.DENSE_CLOUD_BRICK.get(),
                ModBlocks.DENSE_CLOUD_BRICK_SLAB.get(),
                ModBlocks.DENSE_CLOUD_BRICK_STAIRS.get(),
                ModBlocks.NETHER_DIAMOND_ORE.get(),
                ModBlocks.NETHER_COAL_ORE.get(),
                ModBlocks.NETHER_COPPER_ORE.get(),
                ModBlocks.NETHER_EMERALD_ORE.get(),
                ModBlocks.NETHER_IRON_ORE.get(),
                ModBlocks.NETHER_LAPIS_ORE.get(),
                ModBlocks.NETHER_REDSTONE_ORE.get(),
                ModBlocks.PEBBLE.get(),
                ModBlocks.MOSSY_PEBBLE.get(),
                ModBlocks.GRASS_STONE.get(),
                ModBlocks.BLACK_SANDSTONE.get(),
                ModBlocks.CHISELED_BLACK_SANDSTONE.get(),
                ModBlocks.CUT_BLACK_SANDSTONE.get(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE.get(),
                ModBlocks.BLACK_SANDSTONE_SLAB.get(),
                ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get(),
                ModBlocks.BLACK_SANDSTONE_STAIRS.get(),
                ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.get(),
                ModBlocks.BLACK_SANDSTONE_WALL.get()
        ));

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(keys(
                ModBlocks.STICK_PILE.get(),
                // mystic wood
                ModBlocks.MYSTIC_PLANKS.get(),
                ModBlocks.MYSTIC_STAIRS.get(),
                ModBlocks.MYSTIC_SLAB.get(),
                ModBlocks.MYSTIC_BUTTON.get(),
                ModBlocks.MYSTIC_PRESSURE_PLATE.get(),
                ModBlocks.MYSTIC_FENCE.get(),
                ModBlocks.MYSTIC_FENCE_GATE.get(),
                ModBlocks.MYSTIC_DOOR.get(),
                ModBlocks.MYSTIC_TRAPDOOR.get(),
                ModBlocks.MYSTIC_SIGN.get(),
                ModBlocks.MYSTIC_HANGING_SIGN.get(),
                ModBlocks.MYSTIC_WALL_SIGN.get(),
                ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(),
                // sky wood
                ModBlocks.SKY_PLANKS.get(),
                ModBlocks.SKY_STAIRS.get(),
                ModBlocks.SKY_SLAB.get(),
                ModBlocks.SKY_BUTTON.get(),
                ModBlocks.SKY_PRESSURE_PLATE.get(),
                ModBlocks.SKY_FENCE.get(),
                ModBlocks.SKY_FENCE_GATE.get(),
                ModBlocks.SKY_DOOR.get(),
                ModBlocks.SKY_TRAPDOOR.get(),
                ModBlocks.SKY_SIGN.get(),
                ModBlocks.SKY_HANGING_SIGN.get(),
                ModBlocks.SKY_WALL_SIGN.get(),
                ModBlocks.SKY_WALL_HANGING_SIGN.get(),
                // palm wood
                ModBlocks.PALM_PLANKS.get(),
                ModBlocks.PALM_STAIRS.get(),
                ModBlocks.PALM_SLAB.get(),
                ModBlocks.PALM_BUTTON.get(),
                ModBlocks.PALM_PRESSURE_PLATE.get(),
                ModBlocks.PALM_FENCE.get(),
                ModBlocks.PALM_FENCE_GATE.get(),
                ModBlocks.PALM_DOOR.get(),
                ModBlocks.PALM_TRAPDOOR.get(),
                ModBlocks.PALM_SIGN.get(),
                ModBlocks.PALM_HANGING_SIGN.get(),
                ModBlocks.PALM_WALL_SIGN.get(),
                ModBlocks.PALM_WALL_HANGING_SIGN.get(),
                // Gilded_sky wood
                ModBlocks.GILDED_SKY_PLANKS.get(),
                ModBlocks.GILDED_SKY_STAIRS.get(),
                ModBlocks.GILDED_SKY_SLAB.get(),
                ModBlocks.GILDED_SKY_BUTTON.get(),
                ModBlocks.GILDED_SKY_PRESSURE_PLATE.get(),
                ModBlocks.GILDED_SKY_FENCE.get(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get(),
                ModBlocks.GILDED_SKY_DOOR.get(),
                ModBlocks.GILDED_SKY_TRAPDOOR.get(),
                ModBlocks.GILDED_SKY_SIGN.get(),
                ModBlocks.GILDED_SKY_HANGING_SIGN.get(),
                ModBlocks.GILDED_SKY_WALL_SIGN.get(),
                ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.get()
        ));
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(keys(
                ModBlocks.BLACK_SAND.get()
        ));
        this.tag(BlockTags.MINEABLE_WITH_HOE);

        this.tag(BlockTags.FENCES).add(keys(
                ModBlocks.MYSTIC_FENCE.get(),
                ModBlocks.SKY_FENCE.get(),
                ModBlocks.PALM_FENCE.get(),
                ModBlocks.GILDED_SKY_FENCE.get()
        ));

        this.tag(BlockTags.FENCE_GATES).add(keys(
                ModBlocks.MYSTIC_FENCE_GATE.get(),
                ModBlocks.SKY_FENCE_GATE.get(),
                ModBlocks.PALM_FENCE_GATE.get(),
                ModBlocks.GILDED_SKY_FENCE_GATE.get()
        ));

        this.tag(BlockTags.LOGS).add(keys(
                ModBlocks.MYSTIC_LOG.get(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get(),
                ModBlocks.MYSTIC_WOOD.get(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get(),
                ModBlocks.PALM_LOG.get(),
                ModBlocks.STRIPPED_PALM_LOG.get(),
                ModBlocks.PALM_WOOD.get(),
                ModBlocks.STRIPPED_PALM_WOOD.get(),
                ModBlocks.SKY_LOG.get(),
                ModBlocks.STRIPPED_SKY_LOG.get(),
                ModBlocks.SKY_WOOD.get(),
                ModBlocks.STRIPPED_SKY_WOOD.get(),
                ModBlocks.GILDED_SKY_LOG.get(),
                ModBlocks.GILDED_SKY_WOOD.get(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.get(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.get()
        ));
        this.tag(BlockTags.LOGS_THAT_BURN).add(keys(
                ModBlocks.MYSTIC_LOG.get(),
                ModBlocks.STRIPPED_MYSTIC_LOG.get(),
                ModBlocks.MYSTIC_WOOD.get(),
                ModBlocks.STRIPPED_MYSTIC_WOOD.get(),
                ModBlocks.PALM_LOG.get(),
                ModBlocks.STRIPPED_PALM_LOG.get(),
                ModBlocks.PALM_WOOD.get(),
                ModBlocks.STRIPPED_PALM_WOOD.get(),
                ModBlocks.SKY_LOG.get(),
                ModBlocks.STRIPPED_SKY_LOG.get(),
                ModBlocks.SKY_WOOD.get(),
                ModBlocks.STRIPPED_SKY_WOOD.get(),
                ModBlocks.GILDED_SKY_LOG.get(),
                ModBlocks.GILDED_SKY_WOOD.get(),
                ModBlocks.STRIPPED_GILDED_SKY_LOG.get(),
                ModBlocks.STRIPPED_GILDED_SKY_WOOD.get()
        ));
        this.tag(BlockTags.LEAVES).add(keys(
                ModBlocks.MYSTIC_LEAVES.get(),
                ModBlocks.SKY_LEAVES.get(),
                ModBlocks.PALM_LEAVES.get()
        ));
        this.tag(BlockTags.PLANKS).add(keys(
                ModBlocks.MYSTIC_PLANKS.get(),
                ModBlocks.SKY_PLANKS.get(),
                ModBlocks.PALM_PLANKS.get(),
                ModBlocks.GILDED_SKY_PLANKS.get()
        ));

        this.tag(BlockTags.WALLS).add(keys(ModTagContents.WALLS));
        this.tag(BlockTags.STAIRS).add(keys(ModTagContents.STONE_STAIRS));
        this.tag(BlockTags.SLABS).add(keys(ModTagContents.STONE_SLABS));

        // The vanilla stairs/slabs/doors/trapdoors/buttons/pressure_plates/signs tags already nest
        // these wooden_* tags, so filling the wooden_* ones is enough for the parent tags too.
        this.tag(BlockTags.WOODEN_STAIRS).add(keys(ModTagContents.WOODEN_STAIRS));
        this.tag(BlockTags.WOODEN_SLABS).add(keys(ModTagContents.WOODEN_SLABS));
        this.tag(BlockTags.WOODEN_BUTTONS).add(keys(ModTagContents.WOODEN_BUTTONS));
        this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(keys(ModTagContents.WOODEN_PRESSURE_PLATES));
        this.tag(BlockTags.WOODEN_DOORS).add(keys(ModTagContents.WOODEN_DOORS));
        this.tag(BlockTags.WOODEN_TRAPDOORS).add(keys(ModTagContents.WOODEN_TRAPDOORS));
        this.tag(BlockTags.STANDING_SIGNS).add(keys(ModTagContents.STANDING_SIGNS));
        this.tag(BlockTags.WALL_SIGNS).add(keys(ModTagContents.WALL_SIGNS));
        this.tag(BlockTags.CEILING_HANGING_SIGNS).add(keys(ModTagContents.CEILING_HANGING_SIGNS));
        this.tag(BlockTags.WALL_HANGING_SIGNS).add(keys(ModTagContents.WALL_HANGING_SIGNS));
        this.tag(BlockTags.SAPLINGS).add(keys(ModTagContents.SAPLINGS));
        this.tag(BlockTags.SAND).add(keys(ModTagContents.SAND));

        this.tag(STRIPPED_LOGS).add(keys(ModTagContents.STRIPPED_LOGS));
        this.tag(STRIPPED_WOODS).add(keys(ModTagContents.STRIPPED_WOODS));
        this.tag(ORES).add(keys(ModTagContents.ORES));

        // Vanilla's #minecraft:terracotta (part of overworld_carver_replaceables) covers every
        // plain/colored terracotta block, which is why cave carvers cut through ModSurfaceRules'
        // regular-terracotta bands fine but leave the glazed-terracotta bands standing untouched -
        // glazed terracotta isn't in that tag at all. Adding the four colors used there fixes it.
        this.tag(BlockTags.OVERWORLD_CARVER_REPLACEABLES).add(keys(
                Blocks.WHITE_GLAZED_TERRACOTTA,
                Blocks.ORANGE_GLAZED_TERRACOTTA,
                Blocks.RED_GLAZED_TERRACOTTA,
                Blocks.BLACK_GLAZED_TERRACOTTA,
                // The Netherlands is netherrack down to bedrock (see ModSurfaceRules).
                Blocks.NETHERRACK
        ));


    }

    private static final TagKey<Block> STRIPPED_LOGS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "stripped_logs"));
    private static final TagKey<Block> STRIPPED_WOODS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "stripped_woods"));
    private static final TagKey<Block> ORES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores"));

    private static net.minecraft.resources.ResourceKey<Block>[] keys(List<RegistrySupplier<Block>> blocks) {
        return keys(blocks.stream().map(RegistrySupplier::get).toArray(Block[]::new));
    }

    @SafeVarargs
    private static net.minecraft.resources.ResourceKey<Block>[] keys(Block... blocks) {
        net.minecraft.resources.ResourceKey<Block>[] result = new net.minecraft.resources.ResourceKey[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            result[i] = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(blocks[i]).orElseThrow();
        }
        return result;
    }
}
