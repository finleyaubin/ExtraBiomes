package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// Ported from Bedrock extrabiomes:jellyfish — drifting jelly that poisons and damages nearby mobs on contact.
public class JellyfishEntity extends WaterAnimal {
    public static final int VARIANT_COUNT = 2;
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(JellyfishEntity.class, EntityDataSerializers.INT);

    private float grayAmount;
    private float scaleY = 1.0F;

    public JellyfishEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, JellyfishTuning.MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, JellyfishTuning.MOVEMENT_SPEED);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0, 20));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    // Ported from Bedrock's pre_animation gray_amount/scale_y molang lerps (jellyfish.entity.json).
    public float getGrayAmount() {
        return this.grayAmount;
    }

    public float getBodyScaleY() {
        return this.scaleY;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("Variant"));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type,
                                        @Nullable SpawnGroupData data) {
        this.setVariant(this.random.nextInt(VARIANT_COUNT));
        return super.finalizeSpawn(level, difficulty, type, data);
    }

    @Override
    public void tick() {
        super.tick();

        boolean inWater = this.isInWater();
        this.grayAmount = Mth.clamp(this.grayAmount + Mth.clamp((inWater ? 0.0F : 1.0F) - this.grayAmount, -JellyfishTuning.GRAY_STEP_PER_TICK, JellyfishTuning.GRAY_STEP_PER_TICK), 0.0F, 1.0F);
        this.scaleY = Mth.clamp(this.scaleY + Mth.clamp((inWater ? 1.0F : 0.1F) - this.scaleY, -JellyfishTuning.SCALE_Y_STEP_PER_TICK, JellyfishTuning.SCALE_Y_STEP_PER_TICK), 0.1F, 1.0F);

        if (!this.level().isClientSide && this.tickCount % 10 == 0) {
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D));
            for (LivingEntity target : targets) {
                if (target == this) {
                    continue;
                }
                if (target instanceof Player || target instanceof Enemy) {
                    target.hurt(this.damageSources().mobAttack(this), 2.0F);
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0), this);
                }
            }
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.GLASS_BOTTLE) && this.isAlive()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack jam = ItemUtils.createFilledResult(held, player, new ItemStack(ModItems.JELLYFISH_JAM_BOTTLE.get()));
            player.setItemInHand(hand, jam);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (held.is(ModItems.JELLYFISHING_NET_EMPTY.get())) {
            if (!this.level().isClientSide) {
                player.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0F, 1.0F);
                ItemStack fullNet = ItemUtils.createFilledResult(held, player, new ItemStack(ModItems.JELLYFISHING_NET_FULL.get()));
                player.setItemInHand(hand, fullNet);
                this.playSound(SoundEvents.GENERIC_SPLASH, 1.0F, 1.0F);
                this.discard();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
}
