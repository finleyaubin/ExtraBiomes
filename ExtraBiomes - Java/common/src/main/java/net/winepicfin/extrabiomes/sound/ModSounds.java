package net.winepicfin.extrabiomes.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.SOUND_EVENT);

    // Ported straight from Bedrock's mob.hoppleshroom.jump sound definition.
    public static final RegistrySupplier<SoundEvent> HOPPLESHROOM_JUMP = registerSound("mob.hoppleshroom.jump");
    // Ported from Bedrock's mob.puckoo.ambient/mob.puckoo.cry sound definitions.
    public static final RegistrySupplier<SoundEvent> PUCKOO_AMBIENT = registerSound("mob.puckoo.ambient");
    public static final RegistrySupplier<SoundEvent> PUCKOO_CRY = registerSound("mob.puckoo.cry");

    private static RegistrySupplier<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(ExtraBiomes.MOD_ID, name)));
    }

    public static void register() {
        SOUND_EVENTS.register();
    }
}
