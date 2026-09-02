package net.winepicfin.extrabiomes.entity.client.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooBaseVariants;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooKoiMarkings;

public class PuckooRenderState extends LivingEntityRenderState {
    public PuckooBaseVariants variant = PuckooBaseVariants.WHITE;
    public PuckooKoiMarkings markings = PuckooKoiMarkings.BLANK;
    public boolean isSaddled;
}
