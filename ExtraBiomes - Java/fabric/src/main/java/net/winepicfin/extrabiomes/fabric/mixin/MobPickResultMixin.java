package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobPickResultMixin {
    @Inject(method = "getPickResult", at = @At("RETURN"), cancellable = true)
    private void extrabiomes$fallBackToModSpawnEgg(CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        Mob self = (Mob) (Object) this;
        SpawnEggItem egg = SpawnEggItem.byId(self.getType());
        if (egg != null) {
            cir.setReturnValue(new ItemStack(egg));
        }
    }
}
