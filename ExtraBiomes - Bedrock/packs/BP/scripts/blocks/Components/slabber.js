import { BlockPermutation, EquipmentSlot, GameMode } from "@minecraft/server";

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const SlabberComponent = {
    onPlayerInteract(event) {
        const { block, player, face } = event;

        const equippable = player?.getComponent("minecraft:equippable");
        if (!equippable) return;


        const mainhand = equippable.getEquipmentSlot(EquipmentSlot.Mainhand);
        if (!mainhand?.hasItem?.()) return;

        const held = mainhand.getItem ? mainhand.getItem() : mainhand;
        if (!held) return;

        var slabId;
        const type = block.typeId.split(":")[1].split("_")[0];
        if (type === "dense") {
            slabId = 'extrabiomes:dense_cloud_brick_slab';
        }
        else if (type === "gilded") {
            slabId = 'extrabiomes:gilded_sky_slab';
        }
        else if (type === "black") {
            slabId = 'extrabiomes:black_sandstone_slab';
        }
        else if (type === "cut") {
            slabId = 'extrabiomes:cut_black_sandstone_slab';
        }
        else if (type === "smooth") {
            slabId = 'extrabiomes:smooth_black_sandstone_slab';
        }
        else {
            slabId = `extrabiomes:${type}_slab`;
        }


        if (held.typeId !== slabId) return;

        const perm = block.permutation;
        const isDouble = perm.getState("extrabiomes:is_double");
        if (isDouble) return;

        const half = perm.getState("minecraft:vertical_half");

        const shouldDouble =
            (half === "bottom" && face === "Up") ||
            (half === "top" && face === "Down");

        if (!shouldDouble) return;

        const newPerm = BlockPermutation.resolve(slabId, { "extrabiomes:is_double": true });
        block.setPermutation(newPerm);


        if (player.getGameMode?.() !== GameMode.Creative) {
            if (typeof mainhand.amount === "number") {
                if (mainhand.amount > 1) mainhand.amount--;
                else mainhand.setItem?.(undefined);
            } else if (held) {
                if (held.amount > 1) {
                    held.amount--;
                    mainhand.setItem?.(held);
                } else {
                    mainhand.setItem?.(undefined);
                }
            }
        }

        event.cancel = true;
    }
};
