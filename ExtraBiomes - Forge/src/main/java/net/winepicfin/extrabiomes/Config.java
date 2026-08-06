package net.winepicfin.extrabiomes;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.winepicfin.extrabiomes.worldgen.biomes.ModTerrablender;

@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue BIOMEWEIGHT = BUILDER
            .comment("The Weight of ExtraBiomes biomes, the default value is 15")
            .defineInRange("Biome Weight", 15, 0, Integer.MAX_VALUE);

    // On the gametest server, a low weight makes narrow/rare biomes impractically hard to find
    // within a reasonable search radius (see BiomeGenerationGameTests), so force it high there
    // regardless of the configured value - real players never see this override.
    private static final int GAMETEST_BIOME_WEIGHT = 100;

    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int biomeWeight;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        biomeWeight = ForgeGameTestHooks.isGametestServer() ? GAMETEST_BIOME_WEIGHT : BIOMEWEIGHT.get();
        System.out.println(biomeWeight+" is the biome weight On load");
        ModTerrablender.registerBiomes();

    }

}
