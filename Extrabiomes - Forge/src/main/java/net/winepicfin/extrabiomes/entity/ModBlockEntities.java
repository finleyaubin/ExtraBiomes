package net.winepicfin.extrabiomes.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.block.custom.ModHangingSignBlock;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>>BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExtraBiomes.MOD_ID);
    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>>MOD_SIGN=BLOCK_ENTITIES.register("mod_sign",()->BlockEntityType.Builder.of(ModSignBlockEntity::new,ModBlocks.MYSTIC_SIGN.get(),ModBlocks.MYSTIC_WALL_SIGN.get()).build(null));
    public static final RegistryObject<BlockEntityType<ModHangingSignBlockEntity>>MOD__HANGING_SIGN=BLOCK_ENTITIES.register("mod_sign",()->BlockEntityType.Builder.of(ModHangingSignBlockEntity::new,ModBlocks.MYSTIC_HANGING_SIGN.get(),ModBlocks.MYSTIC_WALL_HANGING_SIGN.get()).build(null));
}
