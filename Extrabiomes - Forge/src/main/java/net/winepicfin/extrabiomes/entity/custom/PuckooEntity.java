package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PuckooEntity extends Animal {
    public PuckooEntity(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
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

    public static AttributeSupplier.Builder createAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH,6)
                .add(Attributes.MOVEMENT_SPEED,0.35)
                .add(Attributes.FOLLOW_RANGE, 24);
    }
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob p_146744_) {
        return ModEntities.PUCKOO.get().create(level);
    }

    @Override
    public boolean isFood(ItemStack p_27600_) {
        return p_27600_.is(ModItems.mossy_pebble.get());
    }
}
