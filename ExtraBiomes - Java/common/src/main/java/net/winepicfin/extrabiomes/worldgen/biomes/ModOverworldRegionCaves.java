package net.winepicfin.extrabiomes.worldgen.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

/**
 * Vanilla's own overworld with Dripstone Caves swapped for Tallow Hollows, so the share of dripstone
 * caves it replaces is this region's share of the world ({@code Config.caveBiomeWeight}).
 * <p>
 * It is not a box in one of the overlay regions because VanillaParameterOverlayBuilder only knows
 * vanilla through "everything outside this box" placeholders: an underground-only box there ties with
 * Deep Dark Green's cells (losing it on NeoForge) and sits closer to surface cells than any placeholder.
 */
public class ModOverworldRegionCaves extends Region {
    public ModOverworldRegionCaves(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder ->
                builder.replaceBiome(Biomes.DRIPSTONE_CAVES, ModBiomes.TALLOW_HOLLOWS));
    }
}
