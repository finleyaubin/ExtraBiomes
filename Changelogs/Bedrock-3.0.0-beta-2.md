# ExtraBiomes 3.0.0 Beta 2
---
## Sky City Rework

- Removed the old noise-based sky island system (feature-based cloud placer, leveller rule, and the sky_city_placer.js runtime hack) — sky city placement is now driven entirely by a worldgen structure set, so the city and clouds can no longer desync
- Added new sky city structures and removed the old ones
- Sky city structures now generate successfully (previously untested)
- Made clouds bushier
- Explored experimental jigsaw placement for the sky city (still untested)
- Updated sky city block and structure loading

## Mobs

- Fixed the dog model/animation issue (turned out to be a launcher problem, not an ExtraBiomes bug) and re-added the dog model on the latest format version
- Added new dog personality features (wearable hats coming soon)
- Fixed razor feather items

## Blocks & Items

- Implemented a custom jungle moss feature to fix vanilla moss becoming unavailable for use in addons
- Fixed jellyfishing nets and jellyfish jam
- Fixed throwable items (pebbles and bait)
- Moved all item/block textures to the extrabiomes namespace to prevent compatibility issues with other addons
- Fixed several item menu category issues

## Biomes

- Added client-side biome colours for every biome, including the Moorlands and Shattered Swamp

## Codebase & Misc

- Migrated all legacy run-command events to the new queue-command format
- Cleaned up formatting in Main.js
- Fixed a typo in the Forge directory name
- Overhauled the README with a fuller description, mob showcase, and download links

## CI/CD

- Multiple updates to the GitHub Actions workflow for building the .mcaddon package

⚠️ Known Issues

- Razor feather entities don't yet drop items when they land

---
Full changelog: Bedrock-V3.0.0-beta1...Bedrock-V3.0.0-beta-2


**Full Changelog**: https://github.com/finleyaubin/ExtraBiomes/compare/Bedrock-V3.0.0-beta1...Bedrock-V3.0.0-beta-2
