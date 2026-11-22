import { BlockTypes, EquipmentSlot, GameMode } from "@minecraft/server";

const SLOT_COUNT = 3;
const SLOT_WIDTH = 1 / SLOT_COUNT;
const ITEM_SPACING = 5 / 16;
const ITEM_DEPTH_FROM_CENTER = 0.27;
const ITEM_HEIGHT = 0.5;
const DISPLAY_FAMILY = "shelf_item";

// Held sprites and held blocks need different poses; each entity's client file lines its kind up on the slot centre.
const SPRITE_DISPLAY = "extrabiomes:shelf_item";
const BLOCK_DISPLAY = "extrabiomes:shelf_block";

// Keyed by the placement trait's cardinal_direction: the way the player faced, so the shelf front points back at them.
const FRONT_BY_PLAYER_FACING = {
    south: { x: 0, z: -1, face: "North", yaw: 180 },
    north: { x: 0, z: 1, face: "South", yaw: 0 },
    east: { x: 1, z: 0, face: "East", yaw: -90 },
    west: { x: -1, z: 0, face: "West", yaw: 90 },
};

function frontOf(block) {
    return FRONT_BY_PLAYER_FACING[block.permutation.getState("minecraft:cardinal_direction")];
}

function slotTag(slot) {
    return `shelf_slot_${slot}`;
}

function displayTypeFor(item) {
    return BlockTypes.get(item.typeId) ? BLOCK_DISPLAY : SPRITE_DISPLAY;
}

function slotPosition(block, front, slot) {
    const sideways = (slot - 1) * ITEM_SPACING;
    const rightX = front.z;
    const rightZ = -front.x;
    return {
        x: block.location.x + 0.5 - front.x * ITEM_DEPTH_FROM_CENTER + rightX * sideways,
        y: block.location.y + ITEM_HEIGHT,
        z: block.location.z + 0.5 - front.z * ITEM_DEPTH_FROM_CENTER + rightZ * sideways,
    };
}

function clickedSlot(front, faceLocation) {
    const lateral = (faceLocation.x - 0.5) * front.z + (faceLocation.z - 0.5) * -front.x;
    return Math.min(SLOT_COUNT - 1, Math.max(0, Math.floor(lateral / SLOT_WIDTH + SLOT_COUNT / 2)));
}

function findDisplays(block, slot) {
    return block.dimension.getEntities({
        families: [DISPLAY_FAMILY],
        tags: [slotTag(slot)],
        location: block.center(),
        maxDistance: 1,
    });
}

function storedItem(display) {
    return display.getComponent("minecraft:inventory").container.getItem(0);
}

// The inventory keeps the exact stack; the mainhand copy exists only so the entity renders the item.
function spawnDisplay(block, front, slot, item) {
    const display = block.dimension.spawnEntity(displayTypeFor(item), slotPosition(block, front, slot));
    display.addTag(slotTag(slot));
    display.setRotation({ x: 0, y: front.yaw });
    display.getComponent("minecraft:inventory").container.setItem(0, item);
    display.runCommand(`replaceitem entity @s slot.weapon.mainhand 0 ${item.typeId} ${item.amount}`);
    return display;
}

function handSlot(player) {
    return player.getComponent("minecraft:equippable").getEquipmentSlot(EquipmentSlot.Mainhand);
}

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const ShelfComponent = {
    onPlayerInteract({ block, player, face, faceLocation }) {
        const front = frontOf(block);
        if (face !== front.face) return;

        const slot = clickedSlot(front, faceLocation);
        const hand = handSlot(player);
        const held = hand.getItem();
        const [existing] = findDisplays(block, slot);
        if (!existing && !held) return;

        const stored = existing ? storedItem(existing) : undefined;
        if (existing) existing.remove();
        if (held) spawnDisplay(block, front, slot, held);
        if (player.getGameMode() !== GameMode.Creative) hand.setItem(stored);
        block.dimension.playSound(held ? "block.chiseled_bookshelf.insert" : "block.chiseled_bookshelf.pickup", block.center());
    },

    onPlayerBreak({ block }) {
        for (let slot = 0; slot < SLOT_COUNT; slot++) {
            for (const display of findDisplays(block, slot)) {
                const item = storedItem(display);
                if (item) block.dimension.spawnItem(item, block.center());
                display.remove();
            }
        }
    },
};
