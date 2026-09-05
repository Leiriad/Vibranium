package io.github.leiriad.vibranium.fabric.client;

import io.github.leiriad.vibranium.config.VibraniumConfig;
import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FabricVibraniumScreen {

    // Return the explicit Mojang Screen object directly
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.vibranium.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // ==================== OVERWORLD TAB ====================
        ConfigCategory overworldCategory = builder.getOrCreateCategory(
                Component.translatable("text.vibranium.config.category.overworld")
        );

        // Placement settings
        overworldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.spacing"),
                        VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.spacing)
                .setDefaultValue(VibraniumConfig.DEFAULT_OVERWORLD_SPACING)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.spacing = newValue)

                .setTooltip(Component.translatable("text.vibranium.config.tooltip.spacing.line1"),Component.translatable("text.vibranium.config.tooltip.spacing.line2"))
                .build());

        overworldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.separation"),
                        VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.separation)
                .setDefaultValue(VibraniumConfig.DEFAULT_OVERWORLD_SEPARATION)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.separation = newValue)

                .setTooltip(Component.translatable("text.vibranium.config.tooltip.separation.line1"),Component.translatable("text.vibranium.config.tooltip.separation.line2"))
                .build());

        // --- Dynamic Weights Section ---
        // Add a sub-category or a simple text separator for clarity
        overworldCategory.addEntry(entryBuilder.startTextDescription(
                        Component.translatable("text.vibranium.config.section.weights"))
                .build());

        // Loop through each structure in the config list
        for (var entry : VibraniumConfigManager.INSTANCE.meteoritesOverworld.structures) {
            // Split "vibranium:meteorite_small" to display just "meteorite_small" in the UI
            String structureName = entry.structure.substring(entry.structure.indexOf(":") + 1);

            overworldCategory.addEntry(entryBuilder.startIntField(
                            Component.translatable("text.vibranium.config.weight_for", structureName),
                            entry.weight)
                    .setDefaultValue(1) // Default fallback
                    .setSaveConsumer(newValue -> entry.weight = newValue)
                    .build());
        }

        // ==================== THE END TAB ====================
        ConfigCategory endCategory = builder.getOrCreateCategory(
                Component.translatable("text.vibranium.config.category.end")
        );

        // Placement settings
        endCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.spacing"),
                        VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.spacing)
                .setDefaultValue(VibraniumConfig.DEFAULT_END_SPACING)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.spacing = newValue)

                .setTooltip(Component.translatable("text.vibranium.config.tooltip.spacing.line1"),Component.translatable("text.vibranium.config.tooltip.spacing.line2"))
                .build());

        endCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.separation"),
                        VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.separation)
                .setDefaultValue(VibraniumConfig.DEFAULT_END_SEPARATION)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.separation = newValue)

                .setTooltip(Component.translatable("text.vibranium.config.tooltip.separation.line1"),Component.translatable("text.vibranium.config.tooltip.separation.line2"))
                .build());

        // --- Dynamic Weights Section ---
        // Add a sub-category or a simple text separator for clarity
        endCategory.addEntry(entryBuilder.startTextDescription(
                        Component.translatable("text.vibranium.config.section.weights"))
                .build());

        // Loop through each structure in the config list
        for (var entry : VibraniumConfigManager.INSTANCE.meteoritesEnd.structures) {
            String structureName = entry.structure.substring(entry.structure.indexOf(":") + 1);

            endCategory.addEntry(entryBuilder.startIntField(
                            Component.translatable("text.vibranium.config.weight_for", structureName),
                            entry.weight)
                    .setDefaultValue(1) // Default fallback
                    .setSaveConsumer(newValue -> entry.weight = newValue)
                    .build());
        }

        builder.setSavingRunnable(VibraniumConfigManager::saveConfig);

        return builder.build();
    }
}