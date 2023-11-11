package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooBaseVariants;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooKoiMarkings;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PuckooEntity extends AbstractHorse implements VariantHolder<PuckooBaseVariants> {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(PuckooEntity.class, EntityDataSerializers.INT);
    public PuckooEntity(EntityType<? extends AbstractHorse> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
        this.createInventory();
    }




    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide()){

        }
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    public void addAdditionalSaveData(CompoundTag p_30716_) {
        super.addAdditionalSaveData(p_30716_);
        p_30716_.putInt("Variant", this.getTypeVariant());

    }
    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f=Math.min(pPartialTick*6f,1f);
        }else {
            f=0f;
        }
        this.walkAnimation.update(f,0.2f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new BreedGoal(this,1.15));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.20, Ingredient.of(ModItems.mossy_pebble.get()), false));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this,1.0));
    }

    protected void randomizeAttributes(RandomSource p_218815_) {
        this.getAttribute(Attributes.MOVEMENT_SPEED);

    }
    public static AttributeSupplier.Builder createAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH,6)
                .add(Attributes.MOVEMENT_SPEED,0.35)
                .add(Attributes.FOLLOW_RANGE, 24);
    }
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob p_146744_) {
        return ModEntities.PUCKOO.get().create(level);
    }

    @Override
    public boolean isFood(ItemStack p_27600_) {
        return p_27600_.is(ModItems.mossy_pebble.get());
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    private void setTypeVariant(int p_30737_) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, p_30737_);
    }
    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }


    @Override
    public void setVariant(PuckooBaseVariants p_262689_) {
        this.setTypeVariant(p_262689_.getId() & 255 | this.getTypeVariant() & -256);
    }
    private void setVariantAndMarkings(PuckooBaseVariants p_30700_, PuckooKoiMarkings p_30701_) {
        this.setTypeVariant(p_30700_.getId() & 255 | p_30701_.getId() << 8 & '\uff00');

    }
    public PuckooKoiMarkings getMarkings() {
        return PuckooKoiMarkings.byId((this.getTypeVariant() & '\uff00') >> 8);
    }
    @Override
    public @NotNull PuckooBaseVariants getVariant() {
        return PuckooBaseVariants.byId(this.getTypeVariant() & 255);
    }
    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30703_, DifficultyInstance p_30704_, MobSpawnType p_30705_, @javax.annotation.Nullable SpawnGroupData p_30706_, @javax.annotation.Nullable CompoundTag p_30707_) {
        RandomSource randomsource = p_30703_.getRandom();
            PuckooBaseVariants puckooBaseVariants= Util.getRandom(PuckooBaseVariants.values(), randomsource);
        this.setVariantAndMarkings(puckooBaseVariants, Util.getRandom(PuckooKoiMarkings.values(), randomsource));
        return super.finalizeSpawn(p_30703_, p_30704_, p_30705_, p_30706_, p_30707_);
    }
}
