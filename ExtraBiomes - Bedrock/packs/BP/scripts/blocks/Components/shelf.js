import { BlockTypes, EquipmentSlot, GameMode } from "@minecraft/server";

const SLOT_COUNT = 3;
const SLOT_WIDTH = 1 / SLOT_COUNT;
const FRONT_FACE_DEPTH = 3 / 16;
const ITEM_DEPTH = 0.27;
// The held-item pose draws the item this far in front of the holding entity.
const HELD_ITEM_REACH = 0.165;
// Held blocks are drawn corner-first, so their entity turns this far to show a flat face.
const BLOCK_TURN = 45;
const DISPLAY_TYPE = "extrabiomes:shelf_item";
const DISPLAY_FAMILY = "shelf_item";

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

function slotTag(block, slot) {
    const { x, y, z } = block.location;
    return `shelf_${x}_${y}_${z}_${slot}`;
}

// Lateral is measured along the viewer's right, so slot 0 is the viewer's left.
function lateralOffset(front, x, z) {
    return x * front.z - z * front.x;
}

// Casts the player's view onto the shelf front rather than trusting faceLocation, which came out mirrored.
function clickedSlot(block, front, player) {
    const eye = player.getHeadLocation();
    const view = player.getViewDirection();
    const centre = block.center();
    const toFace = (centre.x - eye.x - front.x * FRONT_FACE_DEPTH) * front.x + (centre.z - eye.z - front.z * FRONT_FACE_DEPTH) * front.z;
    const along = toFace / (view.x * front.x + view.z * front.z);
    const lateral = lateralOffset(front, eye.x + view.x * along - centre.x, eye.z + view.z * along - centre.z);
    return Math.min(SLOT_COUNT - 1, Math.max(0, Math.floor(lateral / SLOT_WIDTH + SLOT_COUNT / 2)));
}

function slotCentre(block, front, slot) {
    const sideways = (slot - 1) * SLOT_WIDTH;
    return {
        x: block.location.x + 0.5 - front.x * ITEM_DEPTH + front.z * sideways,
        y: block.location.y + 0.5,
        z: block.location.z + 0.5 - front.z * ITEM_DEPTH - front.x * sideways,
    };
}

function findDisplays(block, slot) {
    return block.dimension.getEntities({
        families: [DISPLAY_FAMILY],
        tags: [slotTag(block, slot)],
        location: block.center(),
        maxDistance: 2,
    });
}

function storedItem(display) {
    return display.getComponent("minecraft:inventory").container.getItem(0);
}

// The inventory keeps the exact stack; the mainhand copy exists only so the entity renders the item.
function spawnDisplay(block, front, slot, item) {
    const yaw = front.yaw + (BlockTypes.get(item.typeId) ? BLOCK_TURN : 0);
    const radians = (yaw * Math.PI) / 180;
    const target = slotCentre(block, front, slot);
    const holder = { x: target.x + Math.sin(radians) * HELD_ITEM_REACH, y: target.y, z: target.z - Math.cos(radians) * HELD_ITEM_REACH };
    const display = block.dimension.spawnEntity(DISPLAY_TYPE, holder);
    display.addTag(slotTag(block, slot));
    display.setRotation({ x: 0, y: yaw });
    display.getComponent("minecraft:inventory").container.setItem(0, item);
    display.runCommand(`replaceitem entity @s slot.weapon.mainhand 0 ${item.typeId} ${item.amount}`);
    return display;
}

function handSlot(player) {
    return player.getComponent("minecraft:equippable").getEquipmentSlot(EquipmentSlot.Mainhand);
}

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const ShelfComponent = {
    onPlayerInteract({ block, player, face }) {
        const front = frontOf(block);
        if (face !== front.face) return;

        const slot = clickedSlot(block, front, player);
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
