package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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

    public JellyfishEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
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
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.setVariant(this.random.nextInt(VARIANT_COUNT));
        return super.finalizeSpawn(level, difficulty, type, data, tag);
    }

    @Override
    public void tick() {
        super.tick();
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
        // Milk the jellyfish with a glass bottle -> jellyfish jam.
        if (held.is(Items.GLASS_BOTTLE) && this.isAlive()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack jam = ItemUtils.createFilledResult(held, player, new ItemStack(ModItems.JELLYFISH_JAM_BOTTLE.get()));
            player.setItemInHand(hand, jam);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        // Scoop the jellyfish into an empty jellyfishing net -> full net, remove the jellyfish.
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

    // 1.2x scale in Bedrock; applied in the renderer.
    @Override
    public boolean canBeLeashed(Player player) {
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
