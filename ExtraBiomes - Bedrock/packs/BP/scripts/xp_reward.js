/** @type {import("@minecraft/server").BlockCustomComponent} */
export const XpRewardComponent = {
  onPlayerBreak(event) {
    const { dimension, block } = event;
    // Instead of "run_command", we now spawn loot, XP, or entities programmatically
    dimension.runCommand(`summon xp_orb ${block.location.x} ${block.location.y} ${block.location.z}`);
  }
};