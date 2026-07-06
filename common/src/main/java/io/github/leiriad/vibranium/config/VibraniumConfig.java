package io.github.leiriad.vibranium.config;

import java.util.ArrayList;
import java.util.List;

public class VibraniumConfig {
    // Unique IDs used by the Mixin to target the correct dimension placement
    public static final int OVERWORLD_SALT = 12345678;
    public static final int END_SALT = 87654321;

    // Overworld configuration with its default values
    public DimensionConfig meteoritesOverworld = new DimensionConfig(
            defaultOverworldStructures(),
            new PlacementConfig("minecraft:random_spread", 4, 2, OVERWORLD_SALT)
    );

    // End configuration with its default values
    public DimensionConfig meteoritesEnd = new DimensionConfig(
            defaultEndStructures(),
            new PlacementConfig("minecraft:random_spread", 4, 2, END_SALT)
    );

    // Sub-class for dimension-specific settings
    public static class DimensionConfig {
        public List<StructureWeight> structures;
        public PlacementConfig placement;

        public DimensionConfig(List<StructureWeight> structures, PlacementConfig placement) {
            this.structures = structures;
            this.placement = placement;
        }
    }

    // Sub-class for structure weights
    public static class StructureWeight {
        public String structure;
        public int weight;

        public StructureWeight(String structure, int weight) {
            this.structure = structure;
            this.weight = weight;
        }
    }

    // Sub-class for placement settings
    public static class PlacementConfig {
        public String type;
        public int spacing;
        public int separation;
        public long salt;

        public PlacementConfig(String type, int spacing, int separation, long salt) {
            this.type = type;
            this.spacing = spacing;
            this.separation = separation;
            this.salt = salt;
        }
    }

    // Helpers to populate default values programmatically
    private static List<StructureWeight> defaultOverworldStructures() {
        List<StructureWeight> list = new ArrayList<>();
        list.add(new StructureWeight("vibranium:meteorite_small", 10));
        list.add(new StructureWeight("vibranium:meteorite_medium", 5));
        list.add(new StructureWeight("vibranium:meteorite_big", 2));
        list.add(new StructureWeight("vibranium:meteorite_huge", 1));
        return list;
    }

    private static List<StructureWeight> defaultEndStructures() {
        List<StructureWeight> list = new ArrayList<>();
        list.add(new StructureWeight("vibranium:meteorite_small", 2));
        list.add(new StructureWeight("vibranium:meteorite_medium", 20));
        return list;
    }
}
