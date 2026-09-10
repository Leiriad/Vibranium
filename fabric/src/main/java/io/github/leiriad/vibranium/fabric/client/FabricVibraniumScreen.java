package io.github.leiriad.vibranium.fabric.client;

import io.github.leiriad.vibranium.config.VibraniumConfig;
import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FabricVibraniumScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.vibranium.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        var clientConfig = VibraniumConfigManager.INSTANCE.client;
        var worldGenConfig = VibraniumConfigManager.INSTANCE.worldGen;
        var armorConfig = VibraniumConfigManager.INSTANCE.armor;
        var toolsConfig = VibraniumConfigManager.INSTANCE.tools;

        // ==================== CLIENT TAB ====================
        ConfigCategory clientCategory = builder.getOrCreateCategory(
                Component.translatable("text.vibranium.config.category.client")
        );

        clientCategory.addEntry(entryBuilder.startSelector(
                        Component.translatable("text.vibranium.config.option.hud_alignment"),
                        new String[]{"CENTER", "LEFT", "RIGHT"},
                        clientConfig.hudAlignment)
                .setDefaultValue("CENTER")
                .setNameProvider(value -> Component.translatable("text.vibranium.config.hud_alignment." + value.toString().toLowerCase()))
                .setSaveConsumer(newValue -> clientConfig.hudAlignment = newValue)
                .build());

        // ==================== OVERWORLD TAB ====================
        ConfigCategory overworldCategory = builder.getOrCreateCategory(
                Component.translatable("text.vibranium.config.category.overworld")
        );

        overworldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.spacing"),
                        worldGenConfig.meteoritesOverworld.placement.spacing)
                .setDefaultValue(VibraniumConfig.WorldGenConfig.DEFAULT_OVERWORLD_SPACING)
                .setSaveConsumer(newValue -> worldGenConfig.meteoritesOverworld.placement.spacing = newValue)
                .setTooltip(Component.translatable("text.vibranium.config.tooltip.spacing.line1"), Component.translatable("text.vibranium.config.tooltip.spacing.line2"))
                .build());

        overworldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.separation"),
                        worldGenConfig.meteoritesOverworld.placement.separation)
                .setDefaultValue(VibraniumConfig.WorldGenConfig.DEFAULT_OVERWORLD_SEPARATION)
                .setSaveConsumer(newValue -> worldGenConfig.meteoritesOverworld.placement.separation = newValue)
                .setTooltip(Component.translatable("text.vibranium.config.tooltip.separation.line1"), Component.translatable("text.vibranium.config.tooltip.separation.line2"))
                .build());

        overworldCategory.addEntry(entryBuilder.startTextDescription(
                        Component.translatable("text.vibranium.config.section.weights"))
                .build());

        for (var entry : worldGenConfig.meteoritesOverworld.structures) {
            String structureName = entry.structure.substring(entry.structure.indexOf(":") + 1);

            overworldCategory.addEntry(entryBuilder.startIntField(
                            Component.translatable("text.vibranium.config.weight_for", structureName),
                            entry.weight)
                    .setDefaultValue(1)
                    .setSaveConsumer(newValue -> entry.weight = newValue)
                    .build());
        }

        // ==================== THE END TAB ====================
        ConfigCategory endCategory = builder.getOrCreateCategory(
                Component.translatable("text.vibranium.config.category.end")
        );

        // Corrigé : pointe vers meteoritesEnd (et non Overworld)
        endCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.spacing"),
                        worldGenConfig.meteoritesEnd.placement.spacing)
                .setDefaultValue(VibraniumConfig.WorldGenConfig.DEFAULT_END_SPACING)
                .setSaveConsumer(newValue -> worldGenConfig.meteoritesEnd.placement.spacing = newValue)
                .setTooltip(Component.translatable("text.vibranium.config.tooltip.spacing.line1"), Component.translatable("text.vibranium.config.tooltip.spacing.line2"))
                .build());

        endCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.separation"),
                        worldGenConfig.meteoritesEnd.placement.separation)
                .setDefaultValue(VibraniumConfig.WorldGenConfig.DEFAULT_END_SEPARATION)
                .setSaveConsumer(newValue -> worldGenConfig.meteoritesEnd.placement.separation = newValue)
                .setTooltip(Component.translatable("text.vibranium.config.tooltip.separation.line1"), Component.translatable("text.vibranium.config.tooltip.separation.line2"))
                .build());

        endCategory.addEntry(entryBuilder.startTextDescription(
                        Component.translatable("text.vibranium.config.section.weights"))
                .build());

        for (var entry : worldGenConfig.meteoritesEnd.structures) {
            String structureName = entry.structure.substring(entry.structure.indexOf(":") + 1);

            endCategory.addEntry(entryBuilder.startIntField(
                            Component.translatable("text.vibranium.config.weight_for", structureName),
                            entry.weight)
                    .setDefaultValue(1)
                    .setSaveConsumer(newValue -> entry.weight = newValue)
                    .build());
        }

        // ==================== GAMEPLAY TAB (ARMOR & TOOLS) ====================
        ConfigCategory gameplayCategory = builder.getOrCreateCategory(
                Component.translatable("text.vibranium.config.category.gameplay")
        );

        // Armor settings
        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.helmet_cost"),
                        armorConfig.helmetVisionCost)
                .setDefaultValue(15.0F)
                .setSaveConsumer(newValue -> armorConfig.helmetVisionCost = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startDoubleField(
                        Component.translatable("text.vibranium.config.option.helmet_radius"),
                        armorConfig.helmetVisionRadius)
                .setDefaultValue(16.0)
                .setSaveConsumer(newValue -> armorConfig.helmetVisionRadius = newValue)
                .build());

        // --- Pickaxe ---
        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.pickaxe_burst_cost"),
                        toolsConfig.pickaxeBurstCostPerBlock)
                .setDefaultValue(2.0F)
                .setSaveConsumer(newValue -> toolsConfig.pickaxeBurstCostPerBlock = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.pickaxe_sonar_cost"),
                        toolsConfig.pickaxeSonarCost)
                .setDefaultValue(20.0F)
                .setSaveConsumer(newValue -> toolsConfig.pickaxeSonarCost = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.pickaxe_sonar_radius"),
                        toolsConfig.pickaxeSonarRadius)
                .setDefaultValue(12)
                .setSaveConsumer(newValue -> toolsConfig.pickaxeSonarRadius = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.pickaxe_sonar_duration"),
                        toolsConfig.pickaxeSonarDurationTicks)
                .setDefaultValue(200)
                .setSaveConsumer(newValue -> toolsConfig.pickaxeSonarDurationTicks = newValue)
                .build());

        // --- Axe ---
        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.axe_burst_cost"),
                        toolsConfig.axeBurstBlockCost)
                .setDefaultValue(2.0F)
                .setSaveConsumer(newValue -> toolsConfig.axeBurstBlockCost = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.axe_cleave_cost"),
                        toolsConfig.axeCleaveCost)
                .setDefaultValue(25.0F)
                .setSaveConsumer(newValue -> toolsConfig.axeCleaveCost = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.axe_cleave_distance"),
                        toolsConfig.axeCleaveDistance)
                .setDefaultValue(6)
                .setSaveConsumer(newValue -> toolsConfig.axeCleaveDistance = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.axe_cleave_damage"),
                        toolsConfig.axeCleaveDamage)
                .setDefaultValue(7.0F)
                .setSaveConsumer(newValue -> toolsConfig.axeCleaveDamage = newValue)
                .build());

        // --- Shovel ---
        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.shovel_burst_cost"),
                        toolsConfig.shovelBurstBlockCost)
                .setDefaultValue(2.0F)
                .setSaveConsumer(newValue -> toolsConfig.shovelBurstBlockCost = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.shovel_path_cost"),
                        toolsConfig.shovelPathCost)
                .setDefaultValue(0.5F)
                .setSaveConsumer(newValue -> toolsConfig.shovelPathCost = newValue)
                .build());

        // --- Hoe ---
        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.hoe_till_cost"),
                        toolsConfig.hoeTillCost)
                .setDefaultValue(0.5F)
                .setSaveConsumer(newValue -> toolsConfig.hoeTillCost = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.hoe_harvest_cost"),
                        toolsConfig.hoeHarvestCost)
                .setDefaultValue(5.0F)
                .setSaveConsumer(newValue -> toolsConfig.hoeHarvestCost = newValue)
                .build());

        // --- Weapons ---
        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.sword_discharge_threshold"),
                        toolsConfig.swordDischargeThreshold)
                .setDefaultValue(10.0F)
                .setSaveConsumer(newValue -> toolsConfig.swordDischargeThreshold = newValue)
                .build());

        gameplayCategory.addEntry(entryBuilder.startFloatField(
                        Component.translatable("text.vibranium.config.option.spear_discharge_threshold"),
                        toolsConfig.spearDischargeThreshold)
                .setDefaultValue(10.0F)
                .setSaveConsumer(newValue -> toolsConfig.spearDischargeThreshold = newValue)
                .build());

        builder.setSavingRunnable(VibraniumConfigManager::saveConfig);

        return builder.build();
    }
}