package net.winepicfin.extrabiomes.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>>BLOCK_ENTITIES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<ModSignBlockEntity>>MOD_SIGN=BLOCK_ENTITIES.register("mod_sign",()->ExtraBiomesExpectPlatform.createBlockEntityType(ModSignBlockEntity::new,
            ModBlocks.MYSTIC_SIGN.get(),ModBlocks.MYSTIC_WALL_SIGN.get(),
            ModBlocks.PALM_SIGN.get(),ModBlocks.PALM_WALL_SIGN.get(),
            ModBlocks.SKY_SIGN.get(),ModBlocks.SKY_WALL_SIGN.get(),
            ModBlocks.GILDED_SKY_SIGN.get(),ModBlocks.GILDED_SKY_WALL_SIGN.get()));
    public static final RegistrySupplier<BlockEntityType<ModHangingSignBlockEntity>>MOD_HANGING_SIGN=BLOCK_ENTITIES.register("mod_hanging_sign",()->
            ExtraBiomesExpectPlatform.createBlockEntityType(ModHangingSignBlockEntity::new,
                    ModBlocks.MYSTIC_HANGING_SIGN.get(),ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(),
                    ModBlocks.PALM_HANGING_SIGN.get(),ModBlocks.PALM_WALL_HANGING_SIGN.get(),
                    ModBlocks.SKY_HANGING_SIGN.get(),ModBlocks.SKY_WALL_HANGING_SIGN.get(),
                    ModBlocks.GILDED_SKY_HANGING_SIGN.get(),ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.get()));
    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
