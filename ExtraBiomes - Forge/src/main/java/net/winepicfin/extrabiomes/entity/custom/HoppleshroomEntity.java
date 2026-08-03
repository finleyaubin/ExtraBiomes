package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.winepicfin.extrabiomes.sound.ModSounds;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ai.HoppleshroomHopGoal;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

// Ported from Bedrock extrabiomes:hoppleshroom — small hopping fungal critter with 12 colour variants.
public class HoppleshroomEntity extends Animal {
    public static final int VARIANT_COUNT = 12;
    private static final int MUSHROOM_GROWTH_CHANCE = 40; // 1-in-40 odds per landing
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(HoppleshroomEntity.class, EntityDataSerializers.INT);

    // Index order matches HoppleshroomRenderer's COLOURS/texture order and getMushroomBlock():
    // black, blue, brown, crimson, cyan, green, orange, purple, red, warped, white, yellow.
    private static final Vector3f[] DUST_COLORS = {
            new Vector3f(0.12F, 0.12F, 0.12F),
            new Vector3f(0.25F, 0.4F, 0.95F),
            new Vector3f(0.5F, 0.35F, 0.2F),
            new Vector3f(0.6F, 0.1F, 0.25F),
            new Vector3f(0.2F, 0.8F, 0.8F),
            new Vector3f(0.25F, 0.7F, 0.25F),
            new Vector3f(0.9F, 0.5F, 0.15F),
            new Vector3f(0.55F, 0.25F, 0.75F),
            new Vector3f(0.8F, 0.15F, 0.15F),
            new Vector3f(0.15F, 0.6F, 0.55F),
            new Vector3f(0.92F, 0.92F, 0.92F),
            new Vector3f(0.9F, 0.85F, 0.2F),
    };

    // Landing squash, driven purely off the onGround transition rather than synced data — both
    // the server and each client run identical physics for this entity, so the squish can be
    // derived locally on each side exactly like vanilla Slime does with its own squish fields.
    public float squish;
    public float oSquish;
    private boolean wasOnGroundLastTick;

    public HoppleshroomEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.JUMP_STRENGTH, 0.5)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new HoppleshroomHopGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    // JUMP_STRENGTH only affects horses by default — read it directly so the attribute set in
    // createAttributes() actually controls how high each hop launches.
    @Override
    protected float getJumpPower() {
        return (float) this.getAttributeValue(Attributes.JUMP_STRENGTH);
    }

    public void hop(double vx, double vz) {
        this.setDeltaMovement(vx, this.getJumpPower(), vz);
        this.hasImpulse = true;
    }

    @Override
    public void tick() {
        super.tick();
        this.oSquish = this.squish;
        boolean onGroundNow = this.onGround();
        if (onGroundNow && !this.wasOnGroundLastTick) {
            this.squish = 1.0F;
            // Bedrock's animation controller triggers its "drop_spores" particle emitter and the
            // jump sound off the same q.is_on_ground transition this mirrors.
            this.playSound(ModSounds.HOPPLESHROOM_JUMP.get(), 0.3F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            if (this.level().isClientSide()) {
                spawnLandingParticles();
            } else {
                maybeGrowMushroom();
            }
        }
        this.squish += (0.0F - this.squish) * 0.15F;
        this.wasOnGroundLastTick = onGroundNow;
    }

    // Ported from Bedrock's spore.particle.json (extrabiomes:spore_dust): an instant disc burst of
    // 8 particles radiating outward from the landing point, tinted to the hoppleshroom's own colour
    // instead of the single fixed tan tint Bedrock used for every variant.
    private void spawnLandingParticles() {
        Vector3f color = DUST_COLORS[Math.floorMod(this.getVariant(), DUST_COLORS.length)];
        DustParticleOptions options = new DustParticleOptions(color, 1.0F);
        for (int i = 0; i < 8; i++) {
            float angle = (float) (i * (Math.PI * 2.0 / 8.0));
            double dx = Mth.cos(angle);
            double dz = Mth.sin(angle);
            this.level().addParticle(options,
                    this.getX() + dx * 0.3, this.getY() + 0.1, this.getZ() + dz * 0.3,
                    dx * 0.05, 0.02, dz * 0.05);
        }
    }

    private void maybeGrowMushroom() {
        if (this.random.nextInt(MUSHROOM_GROWTH_CHANCE) != 0) {
            return;
        }
        BlockPos pos = this.blockPosition();
        Level level = this.level();
        if (!level.getBlockState(pos).canBeReplaced() || !level.getBlockState(pos.below()).isSolid()) {
            return;
        }
        level.setBlockAndUpdate(pos, getMushroomBlock(this.getVariant()).defaultBlockState());
    }

    private static Block getMushroomBlock(int variant) {
        return switch (variant) {
            case 0 -> ModBlocks.BLACK_MUSHROOM.get();
            case 1 -> ModBlocks.BLUE_MUSHROOM.get();
            case 2 -> Blocks.BROWN_MUSHROOM;
            case 3 -> Blocks.CRIMSON_FUNGUS;
            case 4 -> ModBlocks.CYAN_MUSHROOM.get();
            case 5 -> ModBlocks.GREEN_MUSHROOM.get();
            case 6 -> ModBlocks.ORANGE_MUSHROOM.get();
            case 7 -> ModBlocks.PURPLE_MUSHROOM.get();
            case 8 -> Blocks.RED_MUSHROOM;
            case 9 -> Blocks.WARPED_FUNGUS;
            case 10 -> ModBlocks.WHITE_MUSHROOM.get();
            default -> ModBlocks.YELLOW_MUSHROOM.get();
        };
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
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }
}
