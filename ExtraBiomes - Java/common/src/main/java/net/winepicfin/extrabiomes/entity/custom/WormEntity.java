package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.Nullable;

// Ported from Bedrock extrabiomes:worm — a tiny passive ground critter.
public class WormEntity extends Animal {
    // Earthworm-style behavior: rare on a clear day, common in rain, thickest during a thunderstorm.
    private static final float SPAWN_CHANCE_CLEAR = 0.15F;
    private static final float SPAWN_CHANCE_RAIN = 0.75F;
    private static final float SPAWN_CHANCE_THUNDER = 1.0F;

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
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }
}
