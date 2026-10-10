import { world, system, ItemStack, GameMode } from "@minecraft/server";

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

const WORM = "extrabiomes:worm";
const COMPOSTER = "minecraft:composter";
const COMPOSTER_FILL_STATE = "composter_fill_level";
const COMPOSTER_FLOOR_HEIGHT = 2 / 16;
const BREEDING_COOLDOWN_TICKS = 6000;
const COMPOSTER_CHECK_INTERVAL_TICKS = 20;
const DIMENSION_IDS = ["overworld", "nether", "the_end"];
const READY_TICK_PROPERTY = "extrabiomes:breed_ready_tick";
const EXIT_ORDER = [
    { x: 0, y: -1, z: 0 }, { x: 0, y: 0, z: -1 }, { x: 1, y: 0, z: 0 }, { x: 0, y: 0, z: 1 }, { x: -1, y: 0, z: 0 },
];
const EASTER_EGG_PARENTS = ["archie", "ciaran"];

// Right-clicking the top of a composter with a worm puts it inside the composter, like Java.
world.beforeEvents.playerInteractWithBlock.subscribe((event) => {
    const { player, block, blockFace, itemStack, isFirstEvent } = event;
    if (!isFirstEvent || itemStack?.typeId !== WORM || block.typeId !== COMPOSTER || blockFace !== "Up") return;

    event.cancel = true;
    const nameTag = itemStack.nameTag;
    system.run(() => {
        const worm = block.dimension.spawnEntity(WORM, {
            x: block.location.x + 0.5, y: block.location.y + COMPOSTER_FLOOR_HEIGHT, z: block.location.z + 0.5,
        });
        if (nameTag) worm.nameTag = nameTag;
        if (player.getGameMode() !== GameMode.Creative) consumeHeldItem(player);
    });
});

function consumeHeldItem(player) {
    const slot = player.getComponent("minecraft:inventory").container.getSlot(player.selectedSlotIndex);
    if (slot.amount > 1) slot.amount--;
    else slot.setItem(undefined);
}

// Two ready worms inside a composter that holds compost breed: one compost level is used and the baby drops out.
system.runInterval(() => {
    for (const dimensionId of DIMENSION_IDS) {
        const dimension = world.getDimension(dimensionId);
        for (const worm of dimension.getEntities({ type: WORM })) breedInComposter(dimension, worm);
    }
}, COMPOSTER_CHECK_INTERVAL_TICKS);

function isReady(worm) {
    return (worm.getDynamicProperty(READY_TICK_PROPERTY) ?? 0) <= system.currentTick;
}

function blockPositionOf(entity) {
    return { x: Math.floor(entity.location.x), y: Math.floor(entity.location.y), z: Math.floor(entity.location.z) };
}

function breedInComposter(dimension, worm) {
    if (!worm.isValid || !isReady(worm)) return;
    const pos = blockPositionOf(worm);
    const composter = dimension.getBlock(pos);
    if (composter?.typeId !== COMPOSTER) return;
    const compost = composter.permutation.getState(COMPOSTER_FILL_STATE);
    if (compost === 0) return;

    const mate = dimension.getEntities({ type: WORM, location: composter.center(), maxDistance: 1 })
        .find((other) => other.id !== worm.id && isReady(other) && sameBlock(blockPositionOf(other), pos));
    const exit = EXIT_ORDER.map((offset) => composter.offset(offset)).find(isOpen);
    if (!mate || !exit) return;

    composter.setPermutation(composter.permutation.withState(COMPOSTER_FILL_STATE, compost - 1));
    dimension.playSound("block.composter.fill_success", composter.center());
    const readyTick = system.currentTick + BREEDING_COOLDOWN_TICKS;
    worm.setDynamicProperty(READY_TICK_PROPERTY, readyTick);
    mate.setDynamicProperty(READY_TICK_PROPERTY, readyTick);
    for (const parent of [worm, mate]) {
        dimension.spawnParticle("minecraft:heart_particle", { x: parent.location.x, y: parent.location.y + 0.3, z: parent.location.z });
    }

    const baby = dimension.spawnEntity(WORM, { x: exit.x + 0.5, y: exit.y, z: exit.z + 0.5 });
    if (areEasterEggParents(worm, mate)) baby.nameTag = "Pete";
}

function sameBlock(a, b) {
    return a.x === b.x && a.y === b.y && a.z === b.z;
}

function isOpen(block) {
    return block !== undefined && !block.isLiquid && !block.isSolid;
}

function areEasterEggParents(first, second) {
    const names = [first.nameTag, second.nameTag].map((name) => name.toLowerCase());
    return EASTER_EGG_PARENTS.every((parent) => names.includes(parent));
}
