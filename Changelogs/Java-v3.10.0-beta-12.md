# ExtraBiomes for Java Edition v3.10.0 Beta 12
Palm trees now decay like every other tree, and jellyfish no longer drown while swimming.
# Changes

## Fixes
- Palm leaves now all decay once the trunk is broken. The tips of the wide and large palms were permanent and would float in the air forever.
- The three larger palm trees have a ring of sideways logs around the top of the trunk, where the fronds sprout, and the wide palm has leaves hanging under its crown edge. The few frond tips that hung loose from the rest of the crown are gone.
- Jellyfish no longer drown while swimming. They were missing from the "can breathe under water" tag, so they ran out of air in water like a player would; washed-up jellyfish on land were already safe.

## Tests
- A new game test grows every tree from a sapling with bone meal and with random ticks, then breaks the trunk and checks that its leaves decay.
