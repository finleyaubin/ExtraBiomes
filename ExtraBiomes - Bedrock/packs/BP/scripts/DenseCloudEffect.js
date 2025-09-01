
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const DenseCloudEffect = {
  onEntityFallOn(event) {
        event.entity?.addEffect("resistance", 10, {
        amplifier: 10,
        showParticles: false
    });
  }
};