package net.winepicfin.extrabiomes.worldgen.tree.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooKoiMarkings;

public class ModTrunkPlacerTypes {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, ExtraBiomes.MOD_ID);
    public static final RegistryObject<TrunkPlacerType<MysticTrunkPlacer>> MYSTIC_TRUNK_PLACER = TRUNK_PLACER.register("mystic_trunk_placer",() ->new TrunkPlacerType<>(MysticTrunkPlacer.CODEC));

    public static void register(IEventBus eventBus){
        TRUNK_PLACER.register(eventBus);
    }
}
