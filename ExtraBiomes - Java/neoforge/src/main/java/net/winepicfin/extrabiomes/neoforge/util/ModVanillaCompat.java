package net.winepicfin.extrabiomes.neoforge.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.*;
import net.winepicfin.extrabiomes.block.ModBlocks;

import java.lang.reflect.Method;

public class ModVanillaCompat {
    // accesstransformer.cfg is correctly registered via loom.neoForge.accessTransformer(...)
    // (confirmed: it does trigger a real AT-processing pass - see neoforge/build.gradle) but none
    // of its 3 entries actually widen access on the Architectury Loom NeoForge merged jar, for
    // reasons not fully root-caused this session (same SRG ids apply cleanly on forge/'s own
    // merged jar). Reflection is a safe, reliable fallback for a private instance method called
    // only during mod setup (not a hot path), and avoids blocking the rest of the neoforge port
    // on an unresolved toolchain quirk. See ModSpawnCaps.java and WolfFrogHatLayer.java for the
    // other two AT entries' equivalent workaround.
    private static final Method SET_FLAMMABLE;

    static {
        try {
            SET_FLAMMABLE = FireBlock.class.getDeclaredMethod("setFlammable", Block.class, int.class, int.class);
            SET_FLAMMABLE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void register(){
        registerFlammable(ModBlocks.STICK_PILE.get(),50,50);
        // Mystic Wood
        registerFlammable(ModBlocks.MYSTIC_PLANKS.get(),5,5);
        registerFlammable(ModBlocks.MYSTIC_LOG.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_WOOD.get(),5,20);
        registerFlammable(ModBlocks.STRIPPED_MYSTIC_LOG.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_LEAVES.get(),30,60);
        registerFlammable(ModBlocks.STRIPPED_PALM_WOOD.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_SAPLING.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_STAIRS.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_SLAB.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_BUTTON.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_PRESSURE_PLATE.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_FENCE.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_FENCE_GATE.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_DOOR.get(),5,20);
        registerFlammable(ModBlocks.MYSTIC_TRAPDOOR.get(),5,20);
        // Palm Wood
        registerFlammable(ModBlocks.PALM_PLANKS.get(),5,5);
        registerFlammable(ModBlocks.PALM_LOG.get(),5,20);
        registerFlammable(ModBlocks.PALM_WOOD.get(),5,20);
        registerFlammable(ModBlocks.STRIPPED_PALM_LOG.get(),5,20);
        registerFlammable(ModBlocks.PALM_LEAVES.get(),30,60);
        registerFlammable(ModBlocks.STRIPPED_PALM_WOOD.get(),5,20);
        registerFlammable(ModBlocks.PALM_SAPLING.get(),5,20);
        registerFlammable(ModBlocks.PALM_STAIRS.get(),5,20);
        registerFlammable(ModBlocks.PALM_SLAB.get(),5,20);
        registerFlammable(ModBlocks.PALM_BUTTON.get(),5,20);
        registerFlammable(ModBlocks.PALM_PRESSURE_PLATE.get(),5,20);
        registerFlammable(ModBlocks.PALM_FENCE.get(),5,20);
        registerFlammable(ModBlocks.PALM_FENCE_GATE.get(),5,20);
        registerFlammable(ModBlocks.PALM_DOOR.get(),5,20);
        registerFlammable(ModBlocks.PALM_TRAPDOOR.get(),5,20);
        // Sky Wood
        registerFlammable(ModBlocks.SKY_PLANKS.get(),5,5);
        registerFlammable(ModBlocks.SKY_LOG.get(),5,20);
        registerFlammable(ModBlocks.SKY_WOOD.get(),5,20);
        registerFlammable(ModBlocks.STRIPPED_SKY_LOG.get(),5,20);
        registerFlammable(ModBlocks.SKY_LEAVES.get(),30,60);
        registerFlammable(ModBlocks.STRIPPED_PALM_WOOD.get(),5,20);
        registerFlammable(ModBlocks.SKY_SAPLING.get(),5,20);
        registerFlammable(ModBlocks.SKY_STAIRS.get(),5,20);
        registerFlammable(ModBlocks.SKY_SLAB.get(),5,20);
        registerFlammable(ModBlocks.SKY_BUTTON.get(),5,20);
        registerFlammable(ModBlocks.SKY_PRESSURE_PLATE.get(),5,20);
        registerFlammable(ModBlocks.SKY_FENCE.get(),5,20);
        registerFlammable(ModBlocks.SKY_FENCE_GATE.get(),5,20);
        registerFlammable(ModBlocks.SKY_DOOR.get(),5,20);
        registerFlammable(ModBlocks.SKY_TRAPDOOR.get(),5,20);
        // Gilded Sky Wood
        registerFlammable(ModBlocks.GILDED_SKY_PLANKS.get(),5,5);
        registerFlammable(ModBlocks.GILDED_SKY_LOG.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_WOOD.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_STAIRS.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_SLAB.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_BUTTON.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_PRESSURE_PLATE.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_FENCE.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_FENCE_GATE.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_DOOR.get(),5,20);
        registerFlammable(ModBlocks.GILDED_SKY_TRAPDOOR.get(),5,20);
    }

    public static void registerFlammable(Block block, int probability, int flammability)
    {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        try {
            SET_FLAMMABLE.invoke(fireblock, block, probability, flammability);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
