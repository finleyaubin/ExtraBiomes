package net.winepicfin.extrabiomes.util;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModWoodTypes {
    public static final WoodType MYSTIC = WoodType.register(new WoodType(ExtraBiomes.MOD_ID+":mystic", BlockSetType.OAK));
    public static final WoodType PINE = WoodType.register(new WoodType(ExtraBiomes.MOD_ID+":pine", BlockSetType.OAK));
    public static final WoodType SKY = WoodType.register(new WoodType(ExtraBiomes.MOD_ID+":sky", BlockSetType.OAK));
    public static final WoodType GILDED_SKY = WoodType.register(new WoodType(ExtraBiomes.MOD_ID+":gilded_sky", BlockSetType.OAK));
}
