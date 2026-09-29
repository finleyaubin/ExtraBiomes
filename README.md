# ExtraBiomes worldgen compatibility (dev branches)

Want another mod added to this grid? [Request it in an issue](https://github.com/finleyaubin/ExtraBiomes/issues). If your mod list hits a feature order cycle that isn't listed here, [Feature Recycler](https://www.curseforge.com/minecraft/mc-mods/feature-recycler) can fix it.

Generated 2026-09-29 by the Compat Grid (dev) workflow. Each cell boots a real server with the jar CI built from the tip of each branch (unreleased code) and one other mod, generates a world, and stops it.

✅ works · ❌ feature order cycle · 💥 crash on startup · ⏱ didn't finish · ⚠️ the other mod fails even without ExtraBiomes · ➖ that mod (or a dependency) has no build for this version

| Minecraft | Loader | ExtraBiomes | ExtraBiomes alone | Terralith | Biomes O' Plenty | Oh The Biomes We've Gone | Regions Unexplored | WWOO | Wilder Wild | Geophilic | Ecologics | Terrestria | Traverse | Nature's Spirit | Dynamic Trees | Tectonic |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1.20.1 | fabric | 1.20.1@943985c | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ feature cycle | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ |
| 1.20.1 | forge | 1.20.1@943985c | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ | ❌ feature cycle | ✅ |
| 1.20.2 | fabric | 1.20.2@138a0b0 | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.20.2 | forge | 1.20.2@138a0b0 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.20.2 | neoforge | 1.20.2@138a0b0 | ✅ | ✅ | ➖ | ➖ | ➖ | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.20.4 | fabric | 1.20.4@e9fdd99 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ |
| 1.20.4 | neoforge | 1.20.4@e9fdd99 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.20.6 | fabric | 1.20.6@5e9e2db | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ |
| 1.20.6 | neoforge | 1.20.6@5e9e2db | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.1 | fabric | 1.21.1@04a6d61 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ feature cycle | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ feature cycle | ✅ |
| 1.21.1 | neoforge | 1.21.1@04a6d61 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ | ❌ feature cycle | ✅ |
| 1.21.3 | fabric | 1.21.3@62af78b | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.3 | neoforge | 1.21.3@62af78b | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.4 | fabric | 1.21.4@68cfc2c | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.4 | neoforge | 1.21.4@68cfc2c | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.5 | fabric | 1.21.5@ccbd167 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.5 | neoforge | 1.21.5@ccbd167 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ deps missing | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.8 | fabric | 1.21.8@b9d17dc | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.8 | neoforge | 1.21.8@b9d17dc | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ deps missing | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.10 | fabric | 1.21.10@2a0712f | ✅ | ✅ | ✅ | ➖ deps missing | ➖ | ✅ | ❌ feature cycle | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.10 | neoforge | 1.21.10@2a0712f | ✅ | ✅ | ✅ | ➖ deps missing | ➖ | ✅ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.11 | fabric | 1.21.11@bfd9183 | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ❌ feature cycle | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.11 | neoforge | 1.21.11@bfd9183 | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.1.2 | fabric | 26.1.2@171ccd3 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ❌ feature cycle | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 26.1.2 | neoforge | 26.1.2@171ccd3 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ➖ | ❌ feature cycle | ✅ |
| 26.2 | fabric | 26.2@01e4941 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 26.2 | neoforge | 26.2@01e4941 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.3 | fabric | 26.3@004fad3 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.3 | neoforge | 26.3@004fad3 | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ➖ | ➖ | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ⚠️ fails without ExtraBiomes too |
| 26.3 | fabric | Java-Dev@01261ab | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.3 | neoforge | Java-Dev@01261ab | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ➖ | ➖ | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ⚠️ fails without ExtraBiomes too |

## Failures

- **1.20.1 fabric (1.20.1) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.20.1 forge (1.20.1) + Dynamic Trees** (cycle): `biomes: extrabiomes:fungle_jungle, minecraft:mushroom_fields`
- **1.20.2 fabric (1.20.2) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.20.4 fabric (1.20.4) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.20.6 fabric (1.20.6) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.21.1 fabric (1.21.1) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.21.1 fabric (1.21.1) + Dynamic Trees** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:dark_forest`
- **1.21.1 neoforge (1.21.1) + Dynamic Trees** (cycle): `biomes: extrabiomes:fungle_jungle, minecraft:mushroom_fields`
- **1.21.3 fabric (1.21.3) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.21.4 fabric (1.21.4) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.21.5 fabric (1.21.5) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.21.8 fabric (1.21.8) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.21.10 fabric (1.21.10) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **1.21.11 fabric (1.21.11) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **26.1.2 fabric (26.1.2) + Wilder Wild** (cycle): `biomes: extrabiomes:jungle_marsh, minecraft:bamboo_jungle, minecraft:dark_forest`
- **26.1.2 neoforge (26.1.2) + Dynamic Trees** (cycle): `biomes: extrabiomes:fungle_jungle, minecraft:mushroom_fields`
