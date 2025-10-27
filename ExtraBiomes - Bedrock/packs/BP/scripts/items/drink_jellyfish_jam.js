/** @type {import("@minecraft/server").ItemCustomComponent} */
export const DrinkJellyfishJamComponent = {
    onCompleteUse(event) {
        const { source } = event;
        source.removeEffect("poison");
    }
};