import { system } from "@minecraft/server";

/** @type {import("@minecraft/server").BlockCustomComponent} */
const XpRewardComponent = {
  onPlayerBreak(event) {
    const { dimension, block } = event;
    // Instead of "run_command", we now spawn loot, XP, or entities programmatically
    dimension.runCommand(`summon xp_orb ${block.location.x} ${block.location.y} ${block.location.z}`);
  }
};

system.beforeEvents.startup.subscribe(({ blockComponentRegistry }) => {
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:xp_reward_component",
    XpRewardComponent
  );
});