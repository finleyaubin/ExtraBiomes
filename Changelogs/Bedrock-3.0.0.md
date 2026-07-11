This is a massive release: the entire Bedrock add-on has been rebuilt for the **1.20.100+ / 1.21 world generation format**, and the Sky City has been completely reworked from a noise-based hack into a proper jigsaw structure. Alongside the rewrite, one new biome and dozens of bug fixes have landed.

### Overview
Every biome, block, wood type, and structure has been migrated to Mojang's modern world generation and item/block format. Expect world generation to feel similar overall, but with far more stable biomes, working wood sets, and a Sky City that no longer desyncs from its clouds.

### New Biomes
- **Deep Dark Green** — a Deep Dark sub-biome carrying the jungle tag, enabling Ancient City / underground jungle combos.
- **The Grand Oasis** - A desert biome with red sand stypes on the ground and palm trees.

### Sky City — Full Rework
- Replaced the old noise-based floating island system with a **jigsaw structure set**, as oposed too the old noise based generation sustem.
- Every city piece now has matching cloud structures attached via jigsaw sockets, with 3 cloud variants per piece for visual variety.
- Sky City now only spawns above hills/mountains; islands are bigger, clouds bushier, and new path shapes were added.
- Spawn point always starts at the fountain (guaranteed reachable); fountain water now flows to ground level with cave vines underneath.
- Dense clouds now give you a soft landing.
- Removed all legacy sky city structures, feature rules, and orphaned "leveller" entity/structures.

### World Generation
- All 24+ biomes migrated to the new format and generating correctly again.
- removed the  old Desert Hills.
- Floating Jungles and cold mesa plateau's now spawns on mountain peaks.
- Moorlands now generates dry grass, tall grass, and mud.
- Ice spikes hardcoded as structures to generate in Taiga Spikes biomes.
- Refreshed visuals added missing client-side biome colors across the board.
- Fixed Netherlands ore distributions, grass, and colors; fixed boulder/stick-pile/oasis-puddle placement scatter.

### New Blocks / Items
- Completed **Gilded Sky wood set** (stripped variants, full plank chain, fence gate recipe).
- **The Netherlands gold ore** is now a real block with its own texture/identifier/loot table; fixed nether ore mining speeds.
- All textures namespaced `extrabiomes_` to avoid cross-addon collisions.
- Palm, Mystic, and Sky wood sets reworked and functional, including waterlogging.
- Palm saplings have a custom model and grow correctly; palm/mushroom blocks break on water contact.
- Palm leaves decay correctly and persist after placement.
- Fixed dense cloud brick slabs/stairs, mushroom loot tables, pebbles, and stick pile rotation.

### Mobs / Entities
- Fixed hoppleshroom (renamed from "Hopping Spore") spawning and added a glow color variant.
- Giant tortoises now grow up and breed correctly.
- Fixed piranha "dead" state, worm rain-transformation, jellyfishing nets/jam, and throwable items (pebbles/bait).
- Fixed razor feather items.
- Dogs restored to current animation/personality format.

### Scripting / Data Fixes
- Fixed wood-type extraction bug in door/stripping scripts, copy-paste bug in stairs script, leaf-decay state handling, item menu categories, duplicate windmill identifier, and inverted lapis ore rolls.
- Migrated all legacy `/run` command events to the modern command-queue format.

### Behind the Scenes
- CI/CD now auto-builds the `.mcaddon` and publishes to CurseForge, with version numbers derived from git release tags (including beta handling).
