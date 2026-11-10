package net.winepicfin.extrabiomes.fabric.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import net.winepicfin.extrabiomes.client.color.LarchLeavesTint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fabric API 0.92 has no ColorResolverRegistry (Forge's RegisterColorHandlersEvent.ColorResolvers equivalent), and getBlockTint fails for a resolver without a cache.
@Mixin(ClientLevel.class)
public abstract class ClientLevelTintCacheMixin {
    @Shadow
    @Final
    private Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches;

    @Shadow
    public abstract int calculateBlockTint(BlockPos pos, ColorResolver resolver);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void extrabiomes$addWarmthTintCache(CallbackInfo ci) {
        this.tintCaches.put(LarchLeavesTint.WARMTH_RESOLVER, new BlockTintCache(pos -> this.calculateBlockTint(pos, LarchLeavesTint.WARMTH_RESOLVER)));
    }
}
