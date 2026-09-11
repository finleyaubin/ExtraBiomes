package net.winepicfin.extrabiomes.worldgen;

/**
 * Raised natural-spawn cap for {@code MobCategory.WATER_AMBIENT}, the category
 * {@code ModEntities.PIRANHA} sits in.
 * <p>
 * Vanilla caps a category at {@code getMaxInstancesPerChunk() * spawnableChunkCount / 289} living
 * mobs (see {@code NaturalSpawner$SpawnState.canSpawnForCategory}), and WATER_AMBIENT's own value
 * is 20. That cap - not the spawn weight, and not the herd size - is the real ceiling on how busy
 * jungle water gets, because piranha is the only water spawn vanilla jungles have and a weight
 * only breaks ties between entries within one category. Bedrock's piranha spawn_rules ask for a
 * {@code density_limit.surface} of 30, so 32 lands just above what Bedrock itself allows.
 * <p>
 * There is no datapack or vanilla API for this in 1.20.1 - the value is a {@code private final}
 * field on the {@code MobCategory} enum, read only by {@code getMaxInstancesPerChunk()} - so each
 * loader widens that field its own way (Forge via META-INF/accesstransformer.cfg, Fabric via the
 * MobCategoryAccessor mixin) and then calls {@link #raisedWaterAmbientCap(int)} from its
 * entrypoint. Both loaders end up running the same decision, just reached through different
 * access mechanisms.
 * <p>
 * <b>This is global, not piranha-specific.</b> WATER_AMBIENT is shared with vanilla's schooling
 * fish (cod, salmon, tropical fish, pufferfish), so oceans and rivers everywhere get
 * proportionally denser too. That is the intended trade: the cap is a per-category knob and there
 * is no per-entity equivalent.
 */
public final class MobSpawnCapTuning {
    public static final int WATER_AMBIENT_MAX_INSTANCES_PER_CHUNK = 32;

    /**
     * Returns the cap to apply given whatever the category currently reports. Only ever raises it,
     * so a mod that already set it higher keeps its value instead of being silently stomped -
     * whichever of us loads last, the result is the same.
     */
    public static int raisedWaterAmbientCap(int currentCap) {
        return Math.max(currentCap, WATER_AMBIENT_MAX_INSTANCES_PER_CHUNK);
    }

    private MobSpawnCapTuning() {
    }
}
