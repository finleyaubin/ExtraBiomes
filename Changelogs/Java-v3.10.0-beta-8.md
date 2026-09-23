# ExtraBiomes for Java Edition v3.10.0 Beta 8
# Changes

## Blocks
- Custom wood signs (Mystic, Sky, Palm, Gilded Sky): fixed break particles showing the missing-texture sprite instead of the sign's plank texture.
- Added Grass Stone (ported from Bedrock, including its rare egg top texture). It now forms the sea floor of Jellyfish Fields wherever there is no moss. Craftable with short grass over stone.
- Spawn eggs: fixed egg colors being lost on 1.21.4.

## World Generation
- Snow drifts: fixed the smooth, wave-like drifts being mostly buried underground. They now follow the ground's shape instead of floating over dips or sinking into the surface.
- Added 10 new snow drifts to Glacier, Cold Mesa and Taiga Spikes biomes: a cornice ridge, a sastrugi field, a crescent dune, a moraine drift with boulders, a giant swirl, a twisting spire, a breaking wave, a spiral cone, and rare 40-block and 150-block tall spires.
- Snow drifts now include powder snow pockets under the crust, wind-scoured packed and blue ice, and glacial gravel, andesite and calcite.
- The 150-block spire has a reward chest at its summit.

## Visuals
- Regenerated the LabPBR normal/specular maps to include heightmaps for black sand and black sandstone, matching the Bedrock addon's PBR set.
- Updated the palm sapling model.
- Reorganised the block and item textures into subfolders (mushrooms, black sand, clouds, nether ores, grass stone, one folder per wood set, spawn eggs, boats, food, tools, doors and signs). Resource packs that override ExtraBiomes textures will need the new paths.

## Compatibility
- Fixed NeoForge jars shipping without their `data/neoforge/**` generated tags.

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
