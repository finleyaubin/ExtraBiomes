# ExtraBiomes for Java Edition v3.10.0 Beta 12
A small beta update this time with a couple of bug fixes :)
# Changes

## Fixes
### Trees
<img width="100%" alt="Palm tree" src="https://github.com/user-attachments/assets/648b09bd-18e3-4b04-8c6f-c14ff2067af9" />

- Adjusted Palm tree's leaf placement too allow leaf decay too happen properly, the tree's now have a cross of logs at the top, and no leaves remain floating.
- Fixed a bug with saplings not growing on 26.3 (and added a regression test to catch this in the future)
### Mobs 
<img width="100%" alt="Jellyfish"  src="https://github.com/user-attachments/assets/e95fa1db-a385-4aa9-8685-3cbee10705ab" />

- Allowed jellyfish to breath underwater again... when I let them wash up on beaches and breath on the land, It would Appear I made it so they couldn't breath underwater, and they would drown lol

## Regression Tests
- Added a new gametest to catch the tree bug and jellyfish bug again in the future :)
