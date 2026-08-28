package net.winepicfin.extrabiomes.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.Optional;

// No vanilla trigger fits "a mob strayed away from you while chasing something you threw", so this is
// a small custom one - fired from PiranhaBaitGoal once a chasing piranha clears the lure-away distance
// from the player who threw the bait.
public class BaitLureTrigger extends SimpleCriterionTrigger<BaitLureTrigger.TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation(ExtraBiomes.MOD_ID, "lured_piranha_with_bait");

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, Optional<ContextAwarePredicate> playerPredicate, DeserializationContext context) {
        return new TriggerInstance(playerPredicate);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(Optional<ContextAwarePredicate> playerPredicate) {
            super(playerPredicate);
        }

        public static AbstractCriterionTriggerInstance luredPiranhaWithBait() {
            return new TriggerInstance(Optional.empty());
        }
    }
}
