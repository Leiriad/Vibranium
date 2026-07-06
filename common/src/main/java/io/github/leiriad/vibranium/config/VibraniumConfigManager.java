package io.github.leiriad.vibranium.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VibraniumConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;
    public static VibraniumConfig INSTANCE = new VibraniumConfig();

    public static void init() {
        // Fetch the global config folder provided by Architectury
        configFile = Platform.getConfigFolder().resolve("vibranium_config.json").toFile();

        if (configFile.exists()) {
            loadConfig();
        } else {
            saveConfig(); // Creates the default config if none exists
        }
    }

    public static void loadConfig() {
        try (FileReader reader = new FileReader(configFile)) {
            INSTANCE = GSON.fromJson(reader, VibraniumConfig.class);
        } catch (IOException e) {
            System.err.println("[Vibranium] Failed to load config, using default values.");
            INSTANCE = new VibraniumConfig();
        }
    }

    public static void saveConfig() {
        // Ensure the parent directories exist before writing the file
        if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            System.err.println("[Vibranium] Failed to save config file.");
        }
    }
}
