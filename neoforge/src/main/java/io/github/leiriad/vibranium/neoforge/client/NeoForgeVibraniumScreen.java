package io.github.leiriad.vibranium.neoforge.client;

import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NeoForgeVibraniumScreen {

    public static Screen create(Screen parent) {
        // Initialize the base GUI screen
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.vibranium.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // ==================== OVERWORLD TAB ====================
        ConfigCategory overworldCategory = builder.getOrCreateCategory(
                Component.translatable("text.vibranium.config.category.overworld")
        );

        // Link spacing to the common config instance
        overworldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.spacing"),
                        VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.spacing)
                .setDefaultValue(4)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.spacing = newValue)
                .build());

        overworldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.separation"),
                        VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.separation)
                .setDefaultValue(2)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.separation = newValue)
                .build());

        // --- Dynamic Weights Section ---
        overworldCategory.addEntry(entryBuilder.startTextDescription(
                        Component.translatable("text.vibranium.config.section.weights"))
                .build());

        // Loop through each structure in the config list for Overworld
        for (var entry : VibraniumConfigManager.INSTANCE.meteoritesOverworld.structures) {
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

        // Link spacing to the common config instance for the End dimension
        // FIX: Pointed to meteoritesEnd instead of meteoritesOverworld
        endCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.spacing"),
                        VibraniumConfigManager.INSTANCE.meteoritesEnd.placement.spacing)
                .setDefaultValue(4)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesEnd.placement.spacing = newValue)
                .build());

        endCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.vibranium.config.option.separation"),
                        VibraniumConfigManager.INSTANCE.meteoritesEnd.placement.separation)
                .setDefaultValue(2)
                .setSaveConsumer(newValue -> VibraniumConfigManager.INSTANCE.meteoritesEnd.placement.separation = newValue)
                .build());

        // --- Dynamic Weights Section ---
        endCategory.addEntry(entryBuilder.startTextDescription(
                        Component.translatable("text.vibranium.config.section.weights"))
                .build());

        // Loop through each structure in the config list for The End
        for (var entry : VibraniumConfigManager.INSTANCE.meteoritesEnd.structures) {
            String structureName = entry.structure.substring(entry.structure.indexOf(":") + 1);

            endCategory.addEntry(entryBuilder.startIntField(
                            Component.translatable("text.vibranium.config.weight_for", structureName),
                            entry.weight)
                    .setDefaultValue(1)
                    .setSaveConsumer(newValue -> entry.weight = newValue)
                    .build());
        }

        // Save everything back to the common JSON file when clicking save
        builder.setSavingRunnable(VibraniumConfigManager::saveConfig);

        return builder.build();
    }
}