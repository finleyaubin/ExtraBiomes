package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.custom.projectile.DiamondRazorFeatherProjectileEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.RazorFeatherProjectileEntity;

public class DiamondRazorFeatherItem extends RazorFeatherItem {
    public DiamondRazorFeatherItem(Properties properties) {
        super(properties);
    }

    @Override
    protected RazorFeatherProjectileEntity createProjectile(Level level, Player player) {
        return new DiamondRazorFeatherProjectileEntity(level, player);
    }
}
