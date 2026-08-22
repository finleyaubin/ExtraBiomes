package net.winepicfin.extrabiomes.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.PhantomHarpyTargeting;

// Forge half of the Bedrock phantom.json override - see PhantomHarpyTargeting for what it ports.
// Goals are injected per-instance on join because vanilla builds a phantom's goals in its
// constructor, before any mod gets a look at it.
@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID)
public class PhantomHarpyTargetHandler {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        Goal goal = PhantomHarpyTargeting.createHarpyTargetGoal(event.getEntity());
        if (goal != null) {
            ((Mob) event.getEntity()).targetSelector.addGoal(PhantomHarpyTargeting.GOAL_PRIORITY, goal);
        }
    }
}
