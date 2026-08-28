package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.winepicfin.extrabiomes.item.custom.ExtraBiomesSpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Fabric has no equivalent of Forge's IForgeEntity#getPickedResult fallback (which tries
// ForgeSpawnEggItem.fromEntityType() when vanilla's SpawnEggItem.byId() lookup misses), so pick-block
// on any of this mod's mobs would otherwise return nothing at all - see ExtraBiomesSpawnEggItem's own
// ALL/byType() comment for why that vanilla lookup misses in the first place.
@Mixin(Mob.class)
public abstract class MobPickResultMixin {
    @Inject(method = "getPickResult", at = @At("RETURN"), cancellable = true)
    private void extrabiomes$fallBackToModSpawnEgg(CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        Mob self = (Mob) (Object) this;
        ExtraBiomesSpawnEggItem egg = ExtraBiomesSpawnEggItem.byType(self.getType());
        if (egg != null) {
            cir.setReturnValue(new ItemStack(egg));
        }
    }
}
