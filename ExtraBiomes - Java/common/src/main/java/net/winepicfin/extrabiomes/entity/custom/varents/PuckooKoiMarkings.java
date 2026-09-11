package net.winepicfin.extrabiomes.entity.custom.varents;

import com.mojang.serialization.Codec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum PuckooKoiMarkings implements StringRepresentable {
    BLANK(0, "blank"),
    FULL_ORANGE(1, "full_orange"),
    RED(2, "red"),
    SEMI_ORANGE(3, "semi_orange");

    public static final Codec<PuckooKoiMarkings> CODEC = StringRepresentable.fromEnum(PuckooKoiMarkings::values);
    private static final IntFunction<PuckooKoiMarkings> BY_ID = ByIdMap.continuous(PuckooKoiMarkings::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    private final int id;
    private final String name;

    PuckooKoiMarkings(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public static PuckooKoiMarkings byId(int id) {
        return BY_ID.apply(id);
    }

    public String getSerializedName() {
        return this.name;
    }
}
