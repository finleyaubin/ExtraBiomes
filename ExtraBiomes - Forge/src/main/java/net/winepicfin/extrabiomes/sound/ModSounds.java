package net.winepicfin.extrabiomes.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExtraBiomes.MOD_ID);

    // Ported straight from Bedrock's mob.hoppleshroom.jump sound definition.
    public static final RegistryObject<SoundEvent> HOPPLESHROOM_JUMP = registerSound("mob.hoppleshroom.jump");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
