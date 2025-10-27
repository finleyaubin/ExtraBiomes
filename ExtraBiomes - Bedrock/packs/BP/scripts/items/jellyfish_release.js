import { EquipmentSlot, ItemStack} from "@minecraft/server";
/** @type {import("@minecraft/server").ItemCustomComponent} */
export const JellyfishReleaseComponent = {
    onUse(event) {
        const { itemStack, source } = event;
        const equippable = source?.getComponent("minecraft:equippable");
        if (!equippable) return;

        const mainhand = equippable.getEquipmentSlot(EquipmentSlot.Mainhand);

        mainhand.setItem(new ItemStack("extrabiomes:jellyfishing_net_empty"));
    }
};