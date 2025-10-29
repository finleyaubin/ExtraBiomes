import { ItemStack } from "@minecraft/server";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const SkyCityBlockUpdateComponent = {
onRandomTick(event) {
  const {dimension} = event;
  dimension.runCommand("structure load \"extrabiomes:sky_city/sky_start\" ~-5 222 ~-5")
  dimension.runCommand("fill ~80 ~ ~80 ~-80 ~ ~-80 air 0 replace extrabiomes:sky_city_block")
  dimension.runCommand("fill ~80 ~ ~80 ~-80 ~ ~-80 air 0 replace extrabiomes:dense_cloud")
  dimension.runCommand("fill ~80 ~1 ~80 ~-80 ~1 ~-80 air 0 replace extrabiomes:dense_cloud")
  dimension.runCommand("fill ~80 ~2 ~80 ~-80 ~2 ~-80 air 0 replace extrabiomes:dense_cloud")
  dimension.runCommand("fill ~80 ~3 ~80 ~-80 ~3 ~-80 air 0 replace extrabiomes:dense_cloud")
  dimension.runCommand("fill ~80 ~4 ~80 ~-80 ~4 ~-80 air 0 replace extrabiomes:dense_cloud")
 }
}