package net.winepicfin.extrabiomes;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue BIOMEWEIGHT = BUILDER
            .comment("The Weight of ExtraBiomes biomes, the default value is 5.")
            .defineInRange("Biome Weight", 5, 0, Integer.MAX_VALUE);


    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int biomeWeight;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        biomeWeight = BIOMEWEIGHT.get();

    }
}
