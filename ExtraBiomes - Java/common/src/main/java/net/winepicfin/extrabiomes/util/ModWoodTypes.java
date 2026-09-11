package net.winepicfin.extrabiomes.util;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;

public class ModWoodTypes {
    public static final WoodType MYSTIC = ExtraBiomesExpectPlatform.registerWoodType(new WoodType(ExtraBiomes.MOD_ID+":mystic", BlockSetType.OAK));
    public static final WoodType PALM = ExtraBiomesExpectPlatform.registerWoodType(new WoodType(ExtraBiomes.MOD_ID+":palm", BlockSetType.OAK));
    public static final WoodType SKY = ExtraBiomesExpectPlatform.registerWoodType(new WoodType(ExtraBiomes.MOD_ID+":sky", BlockSetType.OAK));
    public static final WoodType GILDED_SKY = ExtraBiomesExpectPlatform.registerWoodType(new WoodType(ExtraBiomes.MOD_ID+":gilded_sky", BlockSetType.OAK));
}
