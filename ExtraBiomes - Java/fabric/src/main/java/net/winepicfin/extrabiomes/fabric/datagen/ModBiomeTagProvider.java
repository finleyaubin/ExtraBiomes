package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.winepicfin.extrabiomes.util.ModTags;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

import java.util.concurrent.CompletableFuture;

// Fabric port of forge/datagen/ModBiomeTagProvider.java. Unlike BlockTagsProvider/ItemTagsProvider,
// vanilla's own net.minecraft.data.tags.BiomeTagsProvider has no Forge-added ExistingFileHelper/modid
// constructor params (confirmed via javap against the unpatched Fabric-side Minecraft jar - Forge's
// version of this same import is actually a source-patched vanilla class with an extra ctor overload,
// not a separate Forge subclass), so this ports with only the constructor changing to the 2-arg vanilla
// signature. There's also no FabricTagProvider.BiomeTagProvider wrapper - Fabric API's biome
// tag-provider convenience only covers Block/Item/Fluid/Enchantment/EntityType/GameEvent - so this
// extends the vanilla class directly, same as Forge does.
public class ModBiomeTagProvider extends BiomeTagsProvider {
    public ModBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    // Ports each ExtraBiomes biome's Bedrock "minecraft:tags" component onto its real vanilla
    // BiomeTags equivalent, where one exists (see the source Bedrock jsons under
    // ExtraBiomes - Bedrock/packs/BP/biomes/*.biome.json for the original tag lists). Bedrock tags with
    // no vanilla concept (cave/rare/mutated/spawns_cold_variant_farm_animals/etc.) have no Java target
    // and are intentionally left unmapped. TheNetherlands(_Mutated) carries bedrock's "nether"/
    // "nether_wastes" tags but is deliberately generated in the Overworld (see its own class), so
    // IS_NETHER is intentionally NOT applied here.
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        super.addTags(provider);

        this.tag(BiomeTags.IS_OVERWORLD)
                .add(ModBiomes.CHARRED_FOREST, ModBiomes.COLD_MESA, ModBiomes.COLD_MESA_BRYCE, ModBiomes.COLD_MESA_PLATEAU,
                        ModBiomes.DEEP_DARK_FOREST, ModBiomes.DEEP_DARK_GREEN, ModBiomes.DESERT_BRYCE, ModBiomes.FLOATING_JUNGLE,
                        ModBiomes.FUNGLE_JUNGLE, ModBiomes.FUTURE_DESERT, ModBiomes.GLACIER, ModBiomes.GRAND_OASIS,
                        ModBiomes.JELLYFISH_FIELDS, ModBiomes.JUNGLE_MARSH, ModBiomes.JUNGLE_PILLARS, ModBiomes.LUSH_MESA,
                        ModBiomes.LUSH_MESA_BRYCE, ModBiomes.MOORLANDS, ModBiomes.MYSTIC_FOREST, ModBiomes.SHATTERED_SWAMP,
                        ModBiomes.SHATTERED_TAIGA_SPIKES, ModBiomes.THE_NETHERLANDS, ModBiomes.THE_NETHERLANDS_MUTATED,
                        ModBiomes.TAIGA_SPIKES, ModBiomes.TROPICAL_ISLAND);

        this.tag(BiomeTags.IS_FOREST).add(ModBiomes.CHARRED_FOREST, ModBiomes.DEEP_DARK_FOREST);
        this.tag(BiomeTags.IS_JUNGLE).add(ModBiomes.DEEP_DARK_GREEN, ModBiomes.FLOATING_JUNGLE, ModBiomes.FUNGLE_JUNGLE,
                ModBiomes.JUNGLE_MARSH, ModBiomes.JUNGLE_PILLARS, ModBiomes.LUSH_MESA, ModBiomes.LUSH_MESA_BRYCE);
        this.tag(BiomeTags.IS_TAIGA).add(ModBiomes.DEEP_DARK_FOREST, ModBiomes.SHATTERED_TAIGA_SPIKES, ModBiomes.TAIGA_SPIKES);
        this.tag(BiomeTags.IS_BADLANDS).add(ModBiomes.COLD_MESA, ModBiomes.COLD_MESA_BRYCE, ModBiomes.COLD_MESA_PLATEAU,
                ModBiomes.LUSH_MESA, ModBiomes.LUSH_MESA_BRYCE);
        this.tag(BiomeTags.IS_OCEAN).add(ModBiomes.JELLYFISH_FIELDS, ModBiomes.TROPICAL_ISLAND);
        this.tag(BiomeTags.IS_BEACH).add(ModBiomes.GRAND_OASIS, ModBiomes.TROPICAL_ISLAND);
        this.tag(BiomeTags.IS_RIVER).add(ModBiomes.MOORLANDS);
        this.tag(BiomeTags.IS_HILL).add(ModBiomes.GRAND_OASIS);
        this.tag(BiomeTags.IS_MOUNTAIN).add(ModBiomes.GRAND_OASIS);

        this.tag(BiomeTags.HAS_TRAIL_RUINS).add(ModBiomes.DEEP_DARK_FOREST);
        this.tag(BiomeTags.MINESHAFT_BLOCKING).add(ModBiomes.DEEP_DARK_GREEN);
        this.tag(BiomeTags.WITHOUT_PATROL_SPAWNS).add(ModBiomes.FUNGLE_JUNGLE);

        this.tag(BiomeTags.SPAWNS_COLD_VARIANT_FROGS).add(ModBiomes.DEEP_DARK_GREEN, ModBiomes.COLD_MESA, ModBiomes.COLD_MESA_BRYCE,
                ModBiomes.COLD_MESA_PLATEAU, ModBiomes.GLACIER, ModBiomes.SHATTERED_TAIGA_SPIKES, ModBiomes.TAIGA_SPIKES, ModBiomes.VOLCANIC_MOSS_TUNDRA);
        this.tag(BiomeTags.SPAWNS_WARM_VARIANT_FROGS).add(ModBiomes.CHARRED_FOREST, ModBiomes.DESERT_BRYCE, ModBiomes.FUTURE_DESERT,
                ModBiomes.GRAND_OASIS, ModBiomes.JELLYFISH_FIELDS, ModBiomes.TROPICAL_ISLAND);
        this.tag(BiomeTags.SPAWNS_SNOW_FOXES).add(ModBiomes.COLD_MESA, ModBiomes.COLD_MESA_BRYCE, ModBiomes.COLD_MESA_PLATEAU,
                ModBiomes.GLACIER, ModBiomes.SHATTERED_TAIGA_SPIKES);
        this.tag(BiomeTags.SPAWNS_WHITE_RABBITS).add(ModBiomes.COLD_MESA, ModBiomes.COLD_MESA_BRYCE, ModBiomes.COLD_MESA_PLATEAU,
                ModBiomes.GLACIER, ModBiomes.SHATTERED_TAIGA_SPIKES, ModBiomes.TAIGA_SPIKES);

        // Mob-spawn biome groupings (see FabricBiomeModifiers). Piranha/Treefrog/Hoppleshroom read
        // the shared cross-loader convention tags (c:swamp, c:mushroom - Forge's own
        // ModBiomeTagProvider mirrors these onto forge:is_swamp/is_mushroom) directly rather than a
        // mod-local tag, so a third-party mod's swamp/mushroom biome is picked up automatically
        // without needing to know ExtraBiomes exists. We also contribute our own biomes into those
        // same convention tags, the same courtesy other mods extend to us.
        this.tag(ConventionalBiomeTags.SWAMP).add(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, ModBiomes.SHATTERED_SWAMP);
        this.tag(ConventionalBiomeTags.MUSHROOM).add(Biomes.MUSHROOM_FIELDS);

        this.tag(ModTags.Biomes.IS_WETLAND).addTag(ConventionalBiomeTags.SWAMP).add(ModBiomes.MOORLANDS);
        this.tag(ModTags.Biomes.SPAWNS_HOPPLESHROOM).addTag(ConventionalBiomeTags.MUSHROOM).add(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST);
        this.tag(ModTags.Biomes.SPAWNS_JELLYFISH).add(ModBiomes.JELLYFISH_FIELDS);
    }
}
