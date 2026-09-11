import { BlockPermutation, Direction, system, world } from "@minecraft/server";
import { ModalFormData } from "@minecraft/server-ui";

// Bedrock has no sign block entity and no way to draw text on a custom block, so each
// written sign carries an invisible extrabiomes:sign_text entity whose always-shown name
// tag is the text. The entity is both the renderer and the storage - there is nowhere on
// a block to keep the string otherwise.
const TEXT_ENTITY = "extrabiomes:sign_text";
const LINES = 4;
const WOODS = ["mystic", "palm", "sky", "gilded_sky"];

const SIGN_BLOCKS = new Set();
for (const wood of WOODS) {
    for (const shape of ["sign", "wall_sign", "hanging_sign", "wall_hanging_sign"]) {
        SIGN_BLOCKS.add(`extrabiomes:${wood}_${shape}`);
    }
}

const WALL_VARIANT = new Map();
for (const wood of WOODS) {
    WALL_VARIANT.set(`extrabiomes:${wood}_sign`, `extrabiomes:${wood}_wall_sign`);
    WALL_VARIANT.set(`extrabiomes:${wood}_hanging_sign`, `extrabiomes:${wood}_wall_hanging_sign`);
}

const FACE_TO_CARDINAL = {
    [Direction.North]: "south",
    [Direction.South]: "north",
    [Direction.East]: "west",
    [Direction.West]: "east",
};

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const SignTextComponent = {
    onPlayerInteract(event) {
        const { block, player } = event;
        if (!player?.isValid) return;
        event.cancel = true;
        const existing = readLines(block);
        system.run(() => showEditor(block, player, existing));
    },

    onPlayerDestroy(event) {
        const { block, dimension } = event;
        system.run(() => findTextEntity(dimension, block.location)?.remove());
    },
};

function showEditor(block, player, lines) {
    const form = new ModalFormData().title({ translate: "sign.edit" });
    for (let i = 0; i < LINES; i++) {
        form.textField("", "", { defaultValue: lines[i] ?? "" });
    }
    form.show(player).then((response) => {
        if (response.canceled || !response.formValues) return;
        writeLines(block, response.formValues.map((value) => String(value ?? "")));
    }).catch(() => {});
}

function textAnchor(block) {
    // Name tags draw just above the entity's origin, so sit it below the board's middle.
    const hanging = block.typeId.includes("hanging");
    return { x: block.location.x + 0.5, y: block.location.y + (hanging ? 0.35 : 0.6), z: block.location.z + 0.5 };
}

function findTextEntity(dimension, blockLocation) {
    const centre = { x: blockLocation.x + 0.5, y: blockLocation.y + 0.5, z: blockLocation.z + 0.5 };
    return dimension.getEntities({ type: TEXT_ENTITY, location: centre, maxDistance: 0.9 })[0];
}

function readLines(block) {
    const entity = findTextEntity(block.dimension, block.location);
    return entity?.nameTag ? entity.nameTag.split("\n") : [];
}

function writeLines(block, lines) {
    while (lines.length && lines[lines.length - 1] === "") lines.pop();
    const text = lines.join("\n");
    let entity = findTextEntity(block.dimension, block.location);

    if (!text) {
        entity?.remove();
        return;
    }
    if (!entity) {
        entity = block.dimension.spawnEntity(TEXT_ENTITY, textAnchor(block));
    }
    entity.nameTag = text;
}

// A sign item placed against a vertical face becomes the wall variant, as in vanilla.
// The placement trait alone can't do this - it only knows which way the player faced.
world.afterEvents.playerPlaceBlock.subscribe(({ block, face }) => {
    const wallType = WALL_VARIANT.get(block.typeId);
    const cardinal = FACE_TO_CARDINAL[face];
    if (!wallType || !cardinal) return;
    block.setPermutation(BlockPermutation.resolve(wallType, { "minecraft:cardinal_direction": cardinal }));
});

// A sign whose supporting block is gone drops, and its text entity must go with it.
world.afterEvents.playerBreakBlock.subscribe(({ block, dimension }) => {
    if (SIGN_BLOCKS.has(block.typeId)) return;
    findTextEntity(dimension, block.location)?.remove();
});
