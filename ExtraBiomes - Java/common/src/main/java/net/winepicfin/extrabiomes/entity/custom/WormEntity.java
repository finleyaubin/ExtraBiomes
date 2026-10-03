package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

// Ported from Bedrock extrabiomes:worm — a tiny passive ground critter.
public class WormEntity extends Animal {
    // Earthworm-style behavior: rare on a clear day, common in rain, thickest during a thunderstorm.
    private static final float SPAWN_CHANCE_CLEAR = 0.15F;
    private static final float SPAWN_CHANCE_RAIN = 0.75F;
    private static final float SPAWN_CHANCE_THUNDER = 1.0F;
    private static final int BREEDING_COOLDOWN_TICKS = 6000;
    private static final int COMPOSTER_CHECK_INTERVAL_TICKS = 20;
    private static final List<Direction> EXIT_ORDER = List.of(Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public WormEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static boolean checkWormSpawnRules(EntityType<WormEntity> type, ServerLevelAccessor level,
                                               MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        ServerLevel serverLevel = level.getLevel();
        float chance = serverLevel.isThundering() ? SPAWN_CHANCE_THUNDER
                : serverLevel.isRaining() ? SPAWN_CHANCE_RAIN
                : SPAWN_CHANCE_CLEAR;
        return random.nextFloat() < chance;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 2)
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.tickCount % COMPOSTER_CHECK_INTERVAL_TICKS == 0) {
            breedInComposter();
        }
    }

    private void breedInComposter() {
        BlockPos composterPos = this.blockPosition();
        BlockState composter = this.level().getBlockState(composterPos);
        if (!composter.is(Blocks.COMPOSTER) || this.getAge() != 0) {
            return;
        }
        int compost = composter.getValue(ComposterBlock.LEVEL);
        if (compost == 0) {
            return;
        }
        Optional<WormEntity> mate = findMateIn(composterPos);
        Optional<BlockPos> exit = findExitFrom(composterPos);
        if (mate.isEmpty() || exit.isEmpty()) {
            return;
        }
        WormEntity baby = ModEntities.WORM.get().create(this.level());
        if (baby == null) {
            return;
        }
        this.level().setBlock(composterPos, composter.setValue(ComposterBlock.LEVEL, compost - 1), 3);
        this.level().levelEvent(1500, composterPos, 1);
        this.setAge(BREEDING_COOLDOWN_TICKS);
        mate.get().setAge(BREEDING_COOLDOWN_TICKS);
        baby.moveTo(exit.get().getX() + 0.5, exit.get().getY(), exit.get().getZ() + 0.5, this.random.nextFloat() * 360.0F, 0.0F);
        this.level().addFreshEntity(baby);
    }

    private Optional<WormEntity> findMateIn(BlockPos pos) {
        List<WormEntity> worms = this.level().getEntitiesOfClass(WormEntity.class, new AABB(pos),
                worm -> worm != this && worm.getAge() == 0 && worm.blockPosition().equals(pos));
        return worms.stream().findFirst();
    }

    private Optional<BlockPos> findExitFrom(BlockPos pos) {
        return EXIT_ORDER.stream()
                .map(pos::relative)
                .filter(this::isOpen)
                .findFirst();
    }

    private boolean isOpen(BlockPos pos) {
        return this.level().getBlockState(pos).getCollisionShape(this.level(), pos).isEmpty()
                && this.level().getFluidState(pos).isEmpty();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND) {
            if (!this.level().isClientSide) {
                ItemStack wormItem = new ItemStack(ModItems.WORM.get());
                if (this.hasCustomName()) {
                    wormItem.setHoverName(this.getCustomName());
                }
                if (!player.getInventory().add(wormItem)) {
                    player.drop(wormItem, false);
                }
                this.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F) * 2.0F);
                this.discard();
            }
            return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }
}
