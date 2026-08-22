package net.winepicfin.extrabiomes.fabric.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// Hand-written vanilla FlowingFluid subclass, modeled on WaterFluid (no source-conversion,
// doesn't harden against lava like water does) - ForgeFlowingFluid (used by forge/'s ModFluids)
// has no Fabric equivalent, this is the loader-specific replacement. Tuning values match
// forge/.../ModFluids.GOO_PROPERTIES (slopeFindDistance(1), levelDecreasePerBlock(5)) and
// forge/.../ModFluidTypes.GOO_FLUID_TYPE (viscosity(40) ~= a slow tick delay).
//
// canDrown/canPushEntity/canSwim (Forge's FluidType.Properties) have no per-fluid vanilla
// hook to override on Fabric - swimming, drowning and fluid-push physics are all hardcoded in
// Entity/LivingEntity to check membership in the vanilla FluidTags.WATER tag, not a queryable
// property. fabric/src/main/resources/data/minecraft/tags/fluid/water.json adds both Goo
// fluids to that tag to match Forge's canDrown(true)/canPushEntity(true)/canSwim(true) as
// closely as vanilla allows. The one property that can't be matched this way is
// canExtinguish(false): vanilla ties fire-extinguishing to the same FluidTags.WATER check
// (Entity.clearFire() via isInWaterRainOrBubble()), so on Fabric, unlike Forge, standing in
// Goo also puts out fire - a known, unavoidable divergence without a mixin into that method.
public abstract class GooFluid extends FlowingFluid {
    public static final ResourceLocation STILL_TEXTURE = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo_still");
    public static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo_flow");
    public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo");

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_GOO.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_GOO.get();
    }

    @Override
    public Item getBucket() {
        return ModItems.BUCKET_OF_GOO.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 20;
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 1;
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 5;
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.SOURCE_GOO.get() || fluid == ModFluids.FLOWING_GOO.get();
    }

    @Override
    protected boolean canConvertToSource(net.minecraft.world.level.Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        var blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        net.minecraft.world.level.block.Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    @Nullable
    @Override
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_HONEY;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.GOO.get().defaultBlockState().setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Source extends GooFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends GooFluid {
        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
