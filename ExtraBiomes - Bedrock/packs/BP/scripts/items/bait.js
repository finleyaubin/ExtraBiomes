import { GameMode, ItemStack, system, world } from "@minecraft/server";

const BAIT = "extrabiomes:bait";
const BAIT_PROJECTILE = "extrabiomes:bait_projectile";
const MAX_HEALTH = 300;

// A thrown bait's remaining health rides on the item as durability damage, but the engine
// spawns the projectile itself - there is no owner link to read at spawn time. beforeEvents.itemUse
// fires before the built-in throwable behavior consumes the item, so it's the only reliable
// place left to read the stack's damage - by the custom item component's onUse, the mainhand
// slot is already empty. The spawn handler then claims the recorded throw by proximity, which
// is unambiguous because the projectile appears at that player's eye on the same tick.
// Entries are only ever removed by a successful claim or by going stale - never on a fixed
// timer - so a slow tick can't delete a pending throw before its entitySpawn event arrives.
const pendingThrows = new Map();
const PENDING_TIMEOUT_TICKS = 20;

world.beforeEvents.itemUse.subscribe((event) => {
    const { source, itemStack } = event;
    if (itemStack?.typeId !== BAIT || !source?.isValid) return;
    const durability = itemStack.getComponent("minecraft:durability");
    pendingThrows.set(source.id, {
        player: source,
        health: Math.max(1, MAX_HEALTH - (durability?.damage ?? 0)),
        tick: system.currentTick,
    });
});

world.afterEvents.entitySpawn.subscribe(({ entity }) => {
    if (entity?.typeId !== BAIT_PROJECTILE) return;
    const pending = claimNearestThrow(entity);
    if (!pending) return;
    const health = entity.getComponent("minecraft:health");
    if (health) health.setCurrentValue(pending.health);
});

function claimNearestThrow(projectile) {
    let best = null;
    let bestDistance = Infinity;
    for (const [id, pending] of pendingThrows) {
        if (!pending.player.isValid || system.currentTick - pending.tick > PENDING_TIMEOUT_TICKS) {
            pendingThrows.delete(id);
            continue;
        }
        const distance = distanceSquared(pending.player.location, projectile.location);
        if (distance < bestDistance) {
            best = id;
            bestDistance = distance;
        }
    }
    // 4 blocks - comfortably past eye height and the launch offset, well short of a second thrower.
    if (best === null || bestDistance > 16) return null;
    const pending = pendingThrows.get(best);
    pendingThrows.delete(best);
    return pending;
}

function distanceSquared(a, b) {
    const dx = a.x - b.x, dy = a.y - b.y, dz = a.z - b.z;
    return dx * dx + dy * dy + dz * dz;
}

// Shift-interact with an empty hand to reclaim a bait instead of leaving it to be eaten -
// its remaining health comes back as the returned item's damage bar.
world.beforeEvents.playerInteractWithEntity.subscribe((event) => {
    const { player, target } = event;
    if (target?.typeId !== BAIT_PROJECTILE || !player?.isSneaking || event.itemStack) return;

    event.cancel = true;
    const health = target.getComponent("minecraft:health");
    const remaining = Math.max(1, Math.round(health?.currentValue ?? MAX_HEALTH));
    system.run(() => {
        if (!target.isValid) return;
        target.remove();
        givePickedUpBait(player, remaining);
    });
});

function givePickedUpBait(player, remaining) {
    if (player.getGameMode?.() === GameMode.Creative) return;
    const stack = new ItemStack(BAIT, 1);
    const durability = stack.getComponent("minecraft:durability");
    if (durability) durability.damage = MAX_HEALTH - remaining;

    const inventory = player.getComponent("minecraft:inventory")?.container;
    const leftover = inventory?.addItem(stack);
    if (leftover) player.dimension.spawnItem(leftover, player.location);
}
