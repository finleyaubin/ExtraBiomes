package net.winepicfin.extrabiomes.entity.custom;

// No Minecraft imports, deliberately: lets tests read these constants without triggering GiantTortoiseEntity's registry-dependent class-load.
public final class GiantTortoiseTuning {
    public static final double MAX_HEALTH = 70;
    public static final double MOVEMENT_SPEED = 0.3;
    public static final double ATTACK_DAMAGE = 10;
    public static final double ATTACK_KNOCKBACK = 1.5;
    public static final double KNOCKBACK_RESISTANCE = 0.8;
    public static final double FOLLOW_RANGE = 25;
    // Bedrock's top-level "minecraft:damage_sensor" trigger for cause "lightning".
    public static final float LIGHTNING_DAMAGE_MULTIPLIER = 2000;

    private GiantTortoiseTuning() {
    }
}
