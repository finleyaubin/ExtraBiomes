# ExtraBiomes worldgen compatibility

Generated 2026-09-28 by the Compat Grid workflow. Each cell boots a real server with the published ExtraBiomes build and one other mod, generates a world, and stops it.

✅ works · ❌ feature order cycle · 💥 crash on startup · ⏱ didn't finish · ⚠️ the other mod fails even without ExtraBiomes · ➖ that mod (or a dependency) has no build for this version

| Minecraft | Loader | ExtraBiomes | ExtraBiomes alone | Terralith | Biomes O' Plenty | Oh The Biomes We've Gone | Regions Unexplored | WWOO |
|---|---|---|---|---|---|---|---|---|
| 1.20.1 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ❌ feature cycle | ✅ | ✅ |
| 1.20.1 | forge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ❌ feature cycle | ✅ | ✅ |
| 1.20.2 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ |
| 1.20.2 | forge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ |
| 1.20.2 | neoforge | 3.10.0-beta-8 | ✅ | ✅ | ➖ | ➖ | ➖ | ⚠️ fails without ExtraBiomes too |
| 1.20.4 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ |
| 1.20.4 | neoforge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ |
| 1.20.6 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.20.6 | neoforge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ |
| 1.21.1 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ❌ feature cycle | ✅ | ✅ |
| 1.21.3 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ |
| 1.21.3 | neoforge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ |
| 1.21.4 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ❌ feature cycle | ➖ | ✅ |
| 1.21.5 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ✅ |
| 1.21.5 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ➖ deps missing |
| 1.21.8 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ❌ feature cycle | ➖ | ✅ |
| 1.21.8 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ❌ feature cycle | ➖ | ➖ deps missing |
| 1.21.10 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ deps missing | ➖ | ✅ |
| 1.21.10 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ deps missing | ➖ | ✅ |
| 1.21.11 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ❌ feature cycle | ➖ | ✅ |
| 26.1.2 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ✅ |
| 26.1.2 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ✅ |
| 26.2 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ➖ | ✅ |
| 26.3 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ✅ |
| 26.3 | neoforge | 3.10.0-beta-8 | 💥 crash | 💥 crash | ⚠️ fails without ExtraBiomes too | ➖ | ➖ | 💥 crash |

## Failures

- **1.20.1 fabric + Oh The Biomes We've Gone** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / minecraft:sunflower_plains]=net.minecraft.class_1959@3a66ac76}, Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:coconino_meadow]=net.minecraft.class_1959@4a41783f}, Reference{ResourceKey[mi`
- **1.20.1 forge + Oh The Biomes We've Gone** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / minecraft:sunflower_plains]=net.minecraft.world.level.biome.Biome@628547b9}, Reference{ResourceKey[minecraft:worldgen/biome / extrabiomes:lush_mesa_bryce]=net.minecraft.world.level.biome.Biome@6d2bda08}, R`
- **1.21.1 fabric + Oh The Biomes We've Gone** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / minecraft:sunflower_plains]=net.minecraft.class_1959@792a0b08}, Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:coconino_meadow]=net.minecraft.class_1959@32eaecaa}, Reference{ResourceKey[mi`
- **1.21.4 fabric + Oh The Biomes We've Gone** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / minecraft:sunflower_plains]=net.minecraft.class_1959@66b5ac2b}, Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:coconino_meadow]=net.minecraft.class_1959@516bad93}, Reference{ResourceKey[mi`
- **1.21.5 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.class_1959@142e939c}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.class_1959@51e50c77}, Reference{ResourceKey[minecraft:world`
- **1.21.5 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@5e75aec3}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@7cc8b8a5}, Reference{R`
- **1.21.8 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.class_1959@67b3a8ba}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.class_1959@640ef093}, Reference{ResourceKey[minecraft:world`
- **1.21.8 fabric + Oh The Biomes We've Gone** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:forgotten_forest]=net.minecraft.class_1959@2873fcf7}, Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:zelkova_forest]=net.minecraft.class_1959@a740b6b}, Reference{ResourceKey`
- **1.21.8 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@4a042dc7}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@111ae42}, Reference{Re`
- **1.21.8 neoforge + Oh The Biomes We've Gone** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / extrabiomes:lush_mesa_bryce]=net.minecraft.world.level.biome.Biome@2b7b29a4}, Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:forgotten_forest]=net.minecraft.world.level.biome.Biome@7e0a0e8`
- **1.21.10 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.class_1959@9695390}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.class_1959@24851cbc}, Reference{ResourceKey[minecraft:worldg`
- **1.21.10 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@244e7a02}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@40984b9b}, Reference{R`
- **1.21.11 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.class_1959@27694f7c}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.class_1959@6b221aa4}, Reference{ResourceKey[minecraft:world`
- **1.21.11 fabric + Oh The Biomes We've Gone** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:forgotten_forest]=net.minecraft.class_1959@5b1ba9c9}, Reference{ResourceKey[minecraft:worldgen/biome / biomeswevegone:zelkova_forest]=net.minecraft.class_1959@36bf74ed}, Reference{ResourceKe`
- **26.1.2 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@4aec9336}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@1168cdac}, Reference{R`
- **26.1.2 neoforge + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@29dc5751}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@3b22aafc}, Reference{R`
- **26.2 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@7b1ba99a}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@6519efb4}, Reference{R`
- **26.3 fabric + Terralith** (cycle): `Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:sakura_valley]=net.minecraft.world.level.biome.Biome@2b485ffd}, Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=net.minecraft.world.level.biome.Biome@6275dbe5}, Reference{R`
- **26.3 neoforge + ExtraBiomes alone** (crash): `Caused by: java.lang.ClassNotFoundException: net.neoforged.neoforge.registries.DataPackRegistryEvent$NewRegistry`
- **26.3 neoforge + Terralith** (crash): `Caused by: java.lang.ClassNotFoundException: net.neoforged.neoforge.registries.DataPackRegistryEvent$NewRegistry`
- **26.3 neoforge + WWOO** (crash): `Caused by: java.lang.ClassNotFoundException: net.neoforged.neoforge.registries.DataPackRegistryEvent$NewRegistry`
