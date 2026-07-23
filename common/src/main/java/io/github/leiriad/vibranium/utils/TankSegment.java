package io.github.leiriad.vibranium.utils;

import net.minecraft.util.StringRepresentable;

public enum TankSegment implements StringRepresentable {
    SINGLE("single"),
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    private final String name;

    TankSegment(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}