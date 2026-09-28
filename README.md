# ExtraBiomes worldgen compatibility

Generated 2026-09-28 by the Compat Grid workflow. Each cell boots a real server with the published ExtraBiomes build and one other mod, generates a world, and stops it.

✅ works · ❌ feature order cycle · 💥 crash on startup · ⏱ didn't finish · ⚠️ the other mod fails even without ExtraBiomes · ➖ that mod (or a dependency) has no build for this version

| Minecraft | Loader | ExtraBiomes | ExtraBiomes alone | Terralith | Biomes O' Plenty | Oh The Biomes We've Gone | Regions Unexplored | WWOO |
|---|---|---|---|---|---|---|---|---|
| 1.20.6 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.3 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ |
| 1.21.5 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ➖ deps missing |
| 1.21.10 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ deps missing | ➖ | ✅ |
| 1.21.10 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ deps missing | ➖ | ✅ |
| 26.1.2 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ✅ |
| 26.1.2 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ✅ |
| 26.2 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ➖ | ✅ |
| 26.2 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ➖ | ✅ |
| 26.3 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ✅ |
| 26.3 | neoforge | 3.10.0-beta-8 | 💥 crash | 💥 crash | ⚠️ fails without ExtraBiomes too | ➖ | ➖ | 💥 crash |

## Failures

- **1.21.5 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@1385ac44}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@b14f46}, Reference{Res`
- **1.21.10 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.class_1959@5fd16def}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.class_1959@6b86dcc2}, Reference{ResourceKey[minecraft:world`
- **1.21.10 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@752ea66f}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@7e7a743f}, Reference{R`
- **26.1.2 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@2bf2572c}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@7d4e14b3}, Reference{R`
- **26.1.2 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@23112cf7}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@6e11d03d}, Reference{R`
- **26.2 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@602c9957}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@6d410a12}, Reference{R`
- **26.2 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@26613abe}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@a121c63}, Reference{Re`
- **26.3 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@7c777f59}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@2a95b9de}, Reference{R`
- **26.3 neoforge + ExtraBiomes alone** (crash): `Caused by: java.lang.ClassNotFoundException: net.neoforged.neoforge.registries.DataPackRegistryEvent$NewRegistry`
- **26.3 neoforge + Terralith** (crash): `Caused by: java.lang.ClassNotFoundException: net.neoforged.neoforge.registries.DataPackRegistryEvent$NewRegistry`
- **26.3 neoforge + WWOO** (crash): `Caused by: java.lang.ClassNotFoundException: net.neoforged.neoforge.registries.DataPackRegistryEvent$NewRegistry`
