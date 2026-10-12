# ExtraBiomes for Java Edition v3.10.0 Beta 12
Palm trees now decay like every other tree, and saplings and jellyfish no longer break on the versions where they were broken.
# Changes

## Fixes
- Mystic, Sky and Palm saplings now grow into trees, whether bone-mealed or left to grow on their own. They were never growing at all: the tree growers were registered as large (2x2) trees only, so a single sapling had nothing to grow into. (Minecraft 26.3 only; every older version already grew them.)
- Palm leaves now all decay once the trunk is broken. The tips of the wide and large palms were permanent and would float in the air forever. (All versions.)
- The three larger palm trees have a ring of sideways logs around the top of the trunk, where the fronds sprout, and the wide palm has leaves hanging under its crown edge. The few frond tips that hung loose from the rest of the crown are gone. (All versions.)
- Jellyfish no longer drown while swimming. They were missing from the "can breathe under water" tag, so they ran out of air in water like a player would; washed-up jellyfish on land were already safe. (Minecraft 1.20.4 and newer; on 1.20.1 and 1.20.2 jellyfish already breathed underwater.)

## Tests
- New game tests grow every tree from a sapling with bone meal and with random ticks, break the trunk and check that its leaves decay, and check that jellyfish don't drown in water or on land. (All versions.)
