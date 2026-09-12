# ExtraBiomes 3.1.0 Beta 3
# Changes
## Boats
- Added chest boats for all mod wood types and gilded boats and chest boats too.

## Blocks
- Villages: added the ability for villages to spawn inMoorlands, Grand Oasis, Deep Dark Forest, Taiga Spikes and Shattered Taiga Spikes.
- Sized pebble selection/collision boxes for small, medium and large pebbles instead of one size for all.
- Adjusted the nine small mushrooms' selection boxes to match their model instead of a full block footprint.
- Sky city now has the new statue piece, matching Java's rarity.
- Jungle pillars get weathered variants (baked offline, since Bedrock has no processor hook for this), matching Java's look.

## Mobs
- Puckoos now actively seek out and path to nearby mossy pebbles before eating them, and eat them reliably once they arrive.
- Jellyfish no longer spawn on stony shores.

## Items
- Bait: health raised to 300 (up from 90), matching Java's durability-300 stacking behavior; fixed a health/damage-bar sync bug where a re-thrown bait's health never matched its remaining durability.
- Worms can now be shift-clicked to pick back up as an item, keeping their name, and are placeable from the item to spawn a worm entity.
- Fixed pebble placement not properly decrementing the item stack.

## Bug Fixes
- Fixed goo's flipbook texture pointing at a stale atlas tile after a texture key rename.
- Normalized every JSON file in the pack to consistent 4-space formatting (no functional changes).
