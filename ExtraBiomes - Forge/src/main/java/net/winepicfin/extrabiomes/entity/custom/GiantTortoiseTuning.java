package net.winepicfin.extrabiomes.entity.custom;

// Bedrock source values from ExtraBiomes - Bedrock/packs/BP/entities/giant_tortoise.json
// ("minecraft:adult" component group). Deliberately has no Minecraft imports so tests can read
// these constants without triggering GiantTortoiseEntity's own class-load side effects (its
// SynchedEntityData.defineId(...) call needs a bootstrapped registry that plain JUnit can't
// provide).
public final class GiantTortoiseTuning {
    public static final double MAX_HEALTH = 70;
    public static final double MOVEMENT_SPEED = 0.3;
    public static final double ATTACK_DAMAGE = 10;
    public static final double ATTACK_KNOCKBACK = 1.5;
    public static final double KNOCKBACK_RESISTANCE = 0.8;
    public static final double FOLLOW_RANGE = 25;

    private GiantTortoiseTuning() {
    }
}
