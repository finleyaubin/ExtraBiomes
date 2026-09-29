# ExtraBiomes worldgen compatibility (dev branches)

Want another mod added to this grid? [Request it in an issue](https://github.com/finleyaubin/ExtraBiomes/issues). If your mod list hits a feature order cycle that isn't listed here, [Feature Recycler](https://www.curseforge.com/minecraft/mc-mods/feature-recycler) can fix it.

Generated 2026-09-29 by the Compat Grid (dev) workflow. Each cell boots a real server with the jar CI built from the tip of each branch (unreleased code) and one other mod, generates a world, and stops it. A run of only some branches updates just those rows, so the Tested column shows when each row was last run.

✅ works · ❌ feature order cycle · 💥 crash on startup · ⏱ didn't finish · ⚠️ the other mod fails even without ExtraBiomes · ➖ that mod (or a dependency) has no build for this version

| Minecraft | Loader | ExtraBiomes | Tested | ExtraBiomes alone | Terralith | Biomes O' Plenty | Oh The Biomes We've Gone | Regions Unexplored | WWOO | Wilder Wild | Geophilic | Ecologics | Terrestria | Traverse | Nature's Spirit | Dynamic Trees | Tectonic |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1.20.1 | fabric | 1.20.1@7526d00 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ |
| 1.20.1 | forge | 1.20.1@7526d00 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ |
| 1.20.2 | fabric | 1.20.2@48b268f | 2026-09-29 | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.20.2 | forge | 1.20.2@48b268f | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.20.2 | neoforge | 1.20.2@48b268f | 2026-09-29 | ✅ | ✅ | ➖ | ➖ | ➖ | ⚠️ fails without ExtraBiomes too | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.20.4 | fabric | 1.20.4@a6f118f | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ |
| 1.20.4 | neoforge | 1.20.4@a6f118f | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.20.6 | fabric | 1.20.6@7d8d290 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ |
| 1.20.6 | neoforge | 1.20.6@7d8d290 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.1 | fabric | 1.21.1@159f8a0 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 1.21.1 | neoforge | 1.21.1@159f8a0 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ |
| 1.21.3 | fabric | 1.21.3@adfaf1d | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.3 | neoforge | 1.21.3@adfaf1d | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.4 | fabric | 1.21.4@b03dcc3 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.4 | neoforge | 1.21.4@b03dcc3 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.5 | fabric | 1.21.5@944fd82 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.5 | neoforge | 1.21.5@944fd82 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ deps missing | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.8 | fabric | 1.21.8@59e49ca | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.8 | neoforge | 1.21.8@59e49ca | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ deps missing | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.10 | fabric | 1.21.10@f598994 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ deps missing | ➖ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.10 | neoforge | 1.21.10@f598994 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ deps missing | ➖ | ✅ | ➖ | ✅ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 1.21.11 | fabric | 1.21.11@22faf94 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 1.21.11 | neoforge | 1.21.11@22faf94 | 2026-09-29 | ✅ | ✅ | ✅ | ✅ | ➖ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.1.2 | fabric | 26.1.2@2245d2d | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 26.1.2 | neoforge | 26.1.2@2245d2d | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ✅ | ✅ | ➖ | ➖ | ➖ | ✅ | ✅ |
| 26.2 | fabric | 26.2@137a82d | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ |
| 26.2 | neoforge | 26.2@137a82d | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.3 | fabric | 26.3@f68e8d7 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.3 | neoforge | 26.3@f68e8d7 | 2026-09-29 | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ➖ | ➖ | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ⚠️ fails without ExtraBiomes too |
| 26.3 | fabric | Java-Dev@a1ced97 | 2026-09-29 | ✅ | ✅ | ✅ | ➖ | ➖ | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ✅ |
| 26.3 | neoforge | Java-Dev@a1ced97 | 2026-09-29 | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ➖ | ➖ | ✅ | ⚠️ fails without ExtraBiomes too | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ⚠️ fails without ExtraBiomes too |
