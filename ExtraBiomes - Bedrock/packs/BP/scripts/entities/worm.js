import { world, system, ItemStack } from "@minecraft/server";

// Shift-interact with an empty hand to pick a worm back up, keeping its name.
world.beforeEvents.playerInteractWithEntity.subscribe((event) => {
    const { player, target } = event;
    if (target?.typeId !== "extrabiomes:worm" || !player?.isSneaking || event.itemStack) return;

    event.cancel = true;
    const nameTag = target.nameTag;
    const inventory = player.getComponent("minecraft:inventory")?.container;
    if (!inventory) return;

    system.run(() => {
        if (!target.isValid) return;
        target.remove();
        givePickedUpWorm(player, inventory, nameTag);
    });
});

function givePickedUpWorm(player, inventory, nameTag) {
    const stack = new ItemStack("extrabiomes:worm", 1);
    if (nameTag) stack.nameTag = nameTag;
    const leftover = inventory.addItem(stack);
    if (leftover) player.dimension.spawnItem(leftover, player.location);
}
