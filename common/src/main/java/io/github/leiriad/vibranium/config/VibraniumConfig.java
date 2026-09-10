package io.github.leiriad.vibranium.config;

import java.util.ArrayList;
import java.util.List;

public class VibraniumConfig {

    // Global Gameplay Settings
    public ClientConfig client = new ClientConfig();
    public ArmorConfig armor = new ArmorConfig();
    public ToolsConfig tools = new ToolsConfig();
    public WorldGenConfig worldGen = new WorldGenConfig();

    /* ========================================================================
     * CLIENT CONFIGURATION
     * ======================================================================== */
    public static class ClientConfig {
        public String hudAlignment = "CENTER"; // Possible values: "CENTER", "LEFT", "RIGHT"
    }

    /* ========================================================================
     * ARMOR CONFIGURATION
     * ======================================================================== */
    public static class ArmorConfig {
        // Charge gain rates
        public float chargeDamageMultiplier = 1.5F;

        // Helmet settings (Tactical Vision)
        public float helmetVisionCost = 15.0F;
        public double helmetVisionRadius = 16.0;
        public int helmetVisionDurationTicks = 200; // 10 seconds

        // Chestplate settings (Kinetic Release / Shockwave)
        public float chestplateDischargeCost = 50.0F;
        public float chestplateDischargeRadius = 8.0F;

        // Leggings & Boots settings
        public float leggingsSpeedBoostCost = 10.0F;
        public float bootsImpactAbsorptionCost = 5.0F;
    }

    /* ========================================================================
     * TOOLS CONFIGURATION
     * ======================================================================== */
    public static class ToolsConfig {
        // Pickaxe settings
        public float pickaxeBurstCostPerBlock = 2.0F;
        public float pickaxeSonarCost = 20.0F;
        public int pickaxeSonarRadius = 12;
        public int pickaxeSonarDurationTicks = 200;

        // Axe settings
        public float axeBurstBlockCost = 2.0F;
        public float axeCleaveCost = 25.0F;
        public int axeCleaveDistance = 6;
        public float axeCleaveDamage = 7.0F;

        // Shovel
        public float shovelBurstBlockCost = 2.0F;
        public float shovelPathCost = 0.5F;

        //Hoe
        public float hoeTillCost = 0.5F;
        public float hoeHarvestCost = 5.0F;

        // Weapons (Sword & Spear)
        public float swordDischargeThreshold = 100.0F;
        public float spearDischargeThreshold = 10.0F;
    }

    /* ========================================================================
     * WORLD GENERATION & METEORITES CONFIGURATION
     * ======================================================================== */
    public static class WorldGenConfig {
        public static final int OVERWORLD_SALT = 12345678;
        public static final int END_SALT = 87654321;

        public static final int DEFAULT_OVERWORLD_SPACING = 100;
        public static final int DEFAULT_OVERWORLD_SEPARATION = 60;
        public static final int DEFAULT_END_SPACING = 20;
        public static final int DEFAULT_END_SEPARATION = 16;

        public DimensionConfig meteoritesOverworld = new DimensionConfig(
                defaultOverworldStructures(),
                new PlacementConfig("minecraft:random_spread", DEFAULT_OVERWORLD_SPACING, DEFAULT_OVERWORLD_SEPARATION, OVERWORLD_SALT)
        );

        public DimensionConfig meteoritesEnd = new DimensionConfig(
                defaultEndStructures(),
                new PlacementConfig("minecraft:random_spread", DEFAULT_END_SPACING, DEFAULT_END_SEPARATION, END_SALT)
        );

        public static class DimensionConfig {
            public List<StructureWeight> structures;
            public PlacementConfig placement;

            public DimensionConfig(List<StructureWeight> structures, PlacementConfig placement) {
                this.structures = structures;
                this.placement = placement;
            }
        }

        public static class StructureWeight {
            public String structure;
            public int weight;

            public StructureWeight(String structure, int weight) {
                this.structure = structure;
                this.weight = weight;
            }
        }

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

        private static List<StructureWeight> defaultOverworldStructures() {
            List<StructureWeight> list = new ArrayList<>();
            list.add(new StructureWeight("vibranium:meteorite_small", 5));
            list.add(new StructureWeight("vibranium:meteorite_medium", 3));
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
}