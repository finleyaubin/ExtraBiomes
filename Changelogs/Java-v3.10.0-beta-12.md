# ExtraBiomes for Java Edition v3.10.0 Beta 12
Mystic, Sky and Palm saplings finally grow into trees, and palm trees now decay like every other tree.
# Changes

## Fixes
- Mystic, Sky and Palm saplings now grow into trees, whether bone-mealed or left to grow on their own. They were never growing at all: the tree growers were registered as large (2x2) trees only, so a single sapling had nothing to grow into.
- Palm leaves now all decay once the trunk is broken. The tips of the wide and large palms were permanent and would float in the air forever.
- The three larger palm trees have a ring of sideways logs around the top of the trunk, where the fronds sprout, and the wide palm has leaves hanging under its crown edge. The few frond tips that hung loose from the rest of the crown are gone.
- Jellyfish no longer drown while swimming. They were missing from the "can breathe under water" tag, so they ran out of air in water like a player would; washed-up jellyfish on land were already safe.

## Tests
- A new game test grows every tree from a sapling with bone meal and with random ticks, then breaks the trunk and checks that its leaves decay.
