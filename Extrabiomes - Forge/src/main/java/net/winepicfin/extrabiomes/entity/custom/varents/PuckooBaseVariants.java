package net.winepicfin.extrabiomes.entity.custom.varents;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.horse.Variant;

public enum PuckooBaseVariants implements StringRepresentable {
    WHITE(0, "white"),
    BROWN(1, "brown"),
    PINK(2, "pink"),
    YELLOW(3, "yellow");

    public static final Codec<PuckooBaseVariants> CODEC = StringRepresentable.fromEnum(PuckooBaseVariants::values);
    private static final IntFunction<PuckooBaseVariants> BY_ID = ByIdMap.continuous(PuckooBaseVariants::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    private final int id;
    private final String name;

    PuckooBaseVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public static PuckooBaseVariants byId(int id) {
        return BY_ID.apply(id);
    }

    public String getSerializedName() {
        return this.name;
    }
}
