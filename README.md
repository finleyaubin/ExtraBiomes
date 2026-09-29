# ExtraBiomes worldgen compatibility

Generated 2026-09-29 by the Compat Grid workflow. Each cell boots a real server with the published ExtraBiomes build and one other mod, generates a world, and stops it.

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
| 1.21.1 | neoforge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ❌ feature cycle | ✅ | ✅ |
| 1.21.3 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ |
| 1.21.3 | neoforge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ |
| 1.21.4 | fabric | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ❌ feature cycle | ➖ | ✅ |
| 1.21.4 | neoforge | 3.10.0-beta-8 | ✅ | ✅ | ✅ | ❌ feature cycle | ➖ | ✅ |
| 1.21.5 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ✅ |
| 1.21.5 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ➖ deps missing |
| 1.21.8 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ❌ feature cycle | ➖ | ✅ |
| 1.21.8 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ❌ feature cycle | ➖ | ➖ deps missing |
| 1.21.10 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ deps missing | ➖ | ✅ |
| 1.21.10 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ deps missing | ➖ | ✅ |
| 1.21.11 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ❌ feature cycle | ➖ | ✅ |
| 1.21.11 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ❌ feature cycle | ➖ | ✅ |
| 26.1.2 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ✅ |
| 26.1.2 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ✅ |
| 26.2 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ➖ | ✅ |
| 26.2 | neoforge | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ⚠️ fails without ExtraBiomes too | ➖ | ➖ | ✅ |
| 26.3 | fabric | 3.10.0-beta-8 | ✅ | ❌ feature cycle | ✅ | ➖ | ➖ | ✅ |
| 26.3 | neoforge | 3.10.0-beta-8 | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ➖ | ➖ | ✅ |

## Failures

- **1.20.1 fabric + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:coconino_meadow, extrabiomes:jungle_marsh, extrabiomes:lush_mesa_bryce, extrabiomes:moorlands, minecraft:sunflower_plains`
- **1.20.1 forge + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:coconino_meadow, extrabiomes:jungle_marsh, extrabiomes:lush_mesa_bryce, extrabiomes:moorlands, minecraft:sunflower_plains`
- **1.21.1 fabric + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:coconino_meadow, extrabiomes:jungle_marsh, extrabiomes:lush_mesa_bryce, extrabiomes:moorlands, minecraft:sunflower_plains`
- **1.21.1 neoforge + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:coconino_meadow, extrabiomes:jungle_marsh, extrabiomes:lush_mesa_bryce, extrabiomes:moorlands, minecraft:sunflower_plains`
- **1.21.4 fabric + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:coconino_meadow, extrabiomes:jungle_marsh, extrabiomes:lush_mesa_bryce, extrabiomes:moorlands, minecraft:sunflower_plains`
- **1.21.4 neoforge + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:coconino_meadow, extrabiomes:jungle_marsh, extrabiomes:lush_mesa_bryce, extrabiomes:moorlands, minecraft:sunflower_plains`
- **1.21.5 fabric + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.5 neoforge + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.8 fabric + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.8 fabric + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:forgotten_forest, biomeswevegone:zelkova_forest, extrabiomes:lush_mesa_bryce`
- **1.21.8 neoforge + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.8 neoforge + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:forgotten_forest, biomeswevegone:zelkova_forest, extrabiomes:lush_mesa_bryce`
- **1.21.10 fabric + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.10 neoforge + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.11 fabric + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.11 fabric + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:forgotten_forest, biomeswevegone:zelkova_forest, extrabiomes:lush_mesa_bryce`
- **1.21.11 neoforge + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **1.21.11 neoforge + Oh The Biomes We've Gone** (cycle): `biomes: biomeswevegone:forgotten_forest, biomeswevegone:zelkova_forest, extrabiomes:lush_mesa_bryce`
- **26.1.2 fabric + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **26.1.2 neoforge + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **26.2 fabric + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **26.2 neoforge + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
- **26.3 fabric + Terralith** (cycle): `biomes: extrabiomes:lush_mesa_bryce, terralith:sakura_valley, terralith:warm_river`
