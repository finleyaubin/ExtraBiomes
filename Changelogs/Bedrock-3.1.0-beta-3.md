# ExtraBiomes 3.1.0 Beta 3
# Changes
## Boats
<img width="100%" alt="boats" src="https://github.com/user-attachments/assets/7ed02690-cf66-4fe3-b4b7-28a9a4fab3d4" />

- Added chest boats for all mod wood types and gilded boats and chest boats too.

## Blocks
- Sized pebble selection/collision boxes for small, medium and large pebbles instead of one size for all.
- Adjusted the nine small mushrooms' selection boxes to match their model instead of a full block footprint.
- added snow logging for some blocks and the ability to ignore weather for others.
## Structures
### villages
<img width="2560" height="1440" alt="villages" src="https://github.com/user-attachments/assets/b3924b81-a619-4e55-95bc-052b5c0cc468" />

- Villages: added the ability for villages to spawn inMoorlands, Grand Oasis, Deep Dark Forest, Taiga Spikes and Shattered Taiga Spikes.
### Sky City
<img width="2560" height="1440" alt="statue" src="https://github.com/user-attachments/assets/d17bf20c-ea41-4df9-a662-bfd26d1f0810" />

- Sky city now has the new statue piece, matching Java's rarity.
### Jungle Pillars
- Jungle pillars get weathered variants (baked offline, since Bedrock has no processor hook for this), matching Java's look.
## Mobs
- Puckoos now actively seek out and path to nearby mossy pebbles before eating them, and eat them reliably once they arrive.
- Jellyfish no longer spawn on stony shores.

## Items

<img width="100%" alt="archie worm bait" src="https://github.com/user-attachments/assets/43e2cb2f-b609-4883-b1c2-cf70c259ace7" />

- Bait: health raised to 300 (up from 90), matching Java's durability-300 stacking behavior; fixed a health/damage-bar sync bug where a re-thrown bait's health never matched its remaining durability.
- Worms can now be shift-clicked to pick back up as an item, keeping their name, and are placeable from the item to spawn a worm entity.
- Fixed pebble placement not properly decrementing the item stack.

## Bug Fixes
- Fixed goo's flipbook texture pointing at a stale atlas tile after a texture key rename.
- Normalized every JSON file in the pack to consistent 4-space formatting (no functional changes).
## Known issues
- attempted to fix an issue where vines or sculk can grow on the sides of pebbles or mushrooms, however cannot currently find a way to do so.
