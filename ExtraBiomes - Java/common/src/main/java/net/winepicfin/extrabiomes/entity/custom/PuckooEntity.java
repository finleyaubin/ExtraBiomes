package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.ai.PuckooEatMossyPebbleGoal;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooBaseVariants;
import net.winepicfin.extrabiomes.entity.custom.varents.PuckooKoiMarkings;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.DoubleSupplier;

public class PuckooEntity extends AbstractHorse implements VariantHolder<PuckooBaseVariants> {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(PuckooEntity.class, EntityDataSerializers.INT);

    // Out of 9: 4/9 inherit this parent's variant, 4/9 inherit the other parent's, 1/9 random.
    private static final int VARIANT_ROLL_BOUND = 9;
    private static final int VARIANT_INHERIT_SELF_THRESHOLD = 4;
    private static final int VARIANT_INHERIT_OTHER_THRESHOLD = 8;
    // Out of 5: 2/5 inherit this parent's markings, 2/5 inherit the other parent's, 1/5 random.
    private static final int MARKINGS_ROLL_BOUND = 5;
    private static final int MARKINGS_INHERIT_SELF_THRESHOLD = 2;
    private static final int MARKINGS_INHERIT_OTHER_THRESHOLD = 4;
    // Bedrock's "minecraft:horse.jump_strength" range_min/range_max.
    private static final double BEDROCK_JUMP_STRENGTH_MIN = 0.35;
    private static final double BEDROCK_JUMP_STRENGTH_MAX = 0.45;
    // Vanilla horse-style taming: while ridden and untamed, a periodic chance to gain temper and
    // either buck the rider off or, once temper caps out, tame. Playtesting found puckoos never
    // bucked riders at all, so this is spelled out explicitly rather than assumed from AbstractHorse.
    private static final int BUCK_CHECK_TICK_CHANCE = 10;
    private static final int BUCK_COOLDOWN_TICKS = 20;
    private static final int TEMPER_GAIN_PER_ATTEMPT = 5;
    // Bedrock's actual taming method (temper_mod 4 for mossy_pebble); kept alongside the ride-and-buck path.
    private static final int FEED_TEMPER_GAIN = 4;

    private int buckCooldown;

    public PuckooEntity(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
        this.createInventory();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getTypeVariant());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setTypeVariant(tag.getInt("Variant"));
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float walkAmount;
        if (this.getPose() == Pose.STANDING) {
            walkAmount = Math.min(pPartialTick * 6f, 1f);
        } else {
            walkAmount = 0f;
        }
        this.walkAnimation.update(walkAmount, 0.2f);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        if (this.isTamed() || this.level().isClientSide) {
            return;
        }
        if (this.buckCooldown > 0) {
            this.buckCooldown--;
            return;
        }
        if (this.random.nextInt(BUCK_CHECK_TICK_CHANCE) != 0) {
            return;
        }
        this.buckCooldown = BUCK_COOLDOWN_TICKS;
        this.modifyTemper(TEMPER_GAIN_PER_ATTEMPT);
        if (this.getTemper() >= this.getMaxTemper()) {
            this.setTamed(true);
            this.spawnTamingParticles(true);
        } else {
            this.spawnTamingParticles(false);
            player.stopRiding();
        }
    }

    // AbstractHorse#mobInteract never routes to feeding, so without this a mossy pebble just mounted it.
    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isTamed() && !this.isBaby() && !this.isVehicle() && stack.is(ModItems.MOSSY_PEBBLE.get())) {
            if (!this.level().isClientSide) {
                stack.shrink(1);
                this.modifyTemper(FEED_TEMPER_GAIN);
                if (this.getTemper() >= this.getMaxTemper()) {
                    this.setTamed(true);
                    this.setOwnerUUID(player.getUUID());
                    this.spawnTamingParticles(true);
                } else {
                    this.spawnTamingParticles(false);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.15));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.20, Ingredient.of(ModItems.MOSSY_PEBBLE.get()), false));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        // Bedrock runs eat_block at the same priority as random_stroll; here it goes just above so
        // idle wandering doesn't keep pre-empting a puckoo already heading for a pebble.
        this.goalSelector.addGoal(5, new PuckooEatMossyPebbleGoal(this));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0));
    }

    protected void randomizeAttributes(RandomSource random) {
        // MAX_HEALTH deliberately not randomized here: Bedrock's puckoo health is a flat 6, not a range.
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(generateSpeed(random::nextDouble));
        Objects.requireNonNull(this.getAttribute(Attributes.JUMP_STRENGTH)).setBaseValue(generateBedrockJumpStrength(random::nextDouble));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                // AbstractHorse#getCustomJump reads JUMP_STRENGTH, but this builds off
                // createLivingAttributes() rather than createBaseHorseAttributes(), so the
                // attribute has to be declared here or a ridden puckoo can't jump at all.
                .add(Attributes.JUMP_STRENGTH, BEDROCK_JUMP_STRENGTH_MIN)
                .add(Attributes.FOLLOW_RANGE, 24);
    }

    // Bedrock's flat range, not AbstractHorse#generateJumpStrength's wider 0.4-1.0 curve.
    static double generateBedrockJumpStrength(DoubleSupplier random) {
        return BEDROCK_JUMP_STRENGTH_MIN
                + random.getAsDouble() * (BEDROCK_JUMP_STRENGTH_MAX - BEDROCK_JUMP_STRENGTH_MIN);
    }

    // Bedrock's "minecraft:damage_sensor" trigger for cause "fall" is deals_damage: false — a
    // puckoo is outright fall-immune, unlike the reduced-but-real fall damage AbstractHorse gives
    // it by default. Matters most when one is being ridden and power-jumped off a cliff.
    @Override
    public boolean causeFallDamage(float distance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob partner) {
        PuckooEntity other = (PuckooEntity) partner;
        PuckooEntity baby = ModEntities.PUCKOO.get().create(level);
        if (baby != null) {
            PuckooBaseVariants variant = pickInheritedVariant(other);
            PuckooKoiMarkings markings = pickInheritedMarkings(other);
            baby.setVariantAndMarkings(variant, markings);
        }
        return baby;
    }

    private PuckooBaseVariants pickInheritedVariant(PuckooEntity other) {
        int roll = this.random.nextInt(VARIANT_ROLL_BOUND);
        if (roll < VARIANT_INHERIT_SELF_THRESHOLD) {
            return this.getVariant();
        } else if (roll < VARIANT_INHERIT_OTHER_THRESHOLD) {
            return other.getVariant();
        } else {
            return Util.getRandom(PuckooBaseVariants.values(), this.random);
        }
    }

    private PuckooKoiMarkings pickInheritedMarkings(PuckooEntity other) {
        int roll = this.random.nextInt(MARKINGS_ROLL_BOUND);
        if (roll < MARKINGS_INHERIT_SELF_THRESHOLD) {
            return this.getMarkings();
        } else if (roll < MARKINGS_INHERIT_OTHER_THRESHOLD) {
            return other.getMarkings();
        } else {
            return Util.getRandom(PuckooKoiMarkings.values(), this.random);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.MOSSY_PEBBLE.get());
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return net.winepicfin.extrabiomes.sound.ModSounds.PUCKOO_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource damageSource) {
        return net.winepicfin.extrabiomes.sound.ModSounds.PUCKOO_CRY.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return net.winepicfin.extrabiomes.sound.ModSounds.PUCKOO_CRY.get();
    }

    private void setTypeVariant(int typeVariant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, typeVariant);
    }

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    @Override
    public void setVariant(PuckooBaseVariants variant) {
        this.setTypeVariant(variant.getId() & 255 | this.getTypeVariant() & -256);
    }

    // Type variant packs both traits into one synced int: low byte is the base variant id,
    // high byte is the koi markings id.
    private void setVariantAndMarkings(PuckooBaseVariants variant, PuckooKoiMarkings markings) {
        this.setTypeVariant(variant.getId() & 255 | markings.getId() << 8 & '\uff00');
    }

    public PuckooKoiMarkings getMarkings() {
        return PuckooKoiMarkings.byId((this.getTypeVariant() & '\uff00') >> 8);
    }

    @Override
    public @NotNull PuckooBaseVariants getVariant() {
        return PuckooBaseVariants.byId(this.getTypeVariant() & 255);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @javax.annotation.Nullable SpawnGroupData spawnGroupData, @javax.annotation.Nullable CompoundTag tag) {
        RandomSource random = level.getRandom();
        PuckooBaseVariants variant = Util.getRandom(PuckooBaseVariants.values(), random);
        this.setVariantAndMarkings(variant, Util.getRandom(PuckooKoiMarkings.values(), random));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);
    }
}
