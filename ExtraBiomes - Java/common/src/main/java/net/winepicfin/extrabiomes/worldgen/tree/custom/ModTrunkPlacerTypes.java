package net.winepicfin.extrabiomes.worldgen.tree.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;

public class ModTrunkPlacerTypes {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.TRUNK_PLACER_TYPE);
    public static final RegistrySupplier<TrunkPlacerType<MysticTrunkPlacer>> MYSTIC_TRUNK_PLACER = TRUNK_PLACER.register("mystic_trunk_placer",() ->ExtraBiomesExpectPlatform.createTrunkPlacerType(MysticTrunkPlacer.CODEC));

    public static void register() {
        TRUNK_PLACER.register();
    }
}
