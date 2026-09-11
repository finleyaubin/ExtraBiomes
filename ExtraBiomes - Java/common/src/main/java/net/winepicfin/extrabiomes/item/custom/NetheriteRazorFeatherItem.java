package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.custom.projectile.NetheriteRazorFeatherProjectileEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.RazorFeatherProjectileEntity;

public class NetheriteRazorFeatherItem extends RazorFeatherItem {
    public NetheriteRazorFeatherItem(Properties properties) {
        super(properties);
    }

    @Override
    protected RazorFeatherProjectileEntity createProjectile(Level level, Player player) {
        return new NetheriteRazorFeatherProjectileEntity(level, player);
    }
}
