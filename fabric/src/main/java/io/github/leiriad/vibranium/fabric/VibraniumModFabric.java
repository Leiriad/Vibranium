package io.github.leiriad.vibranium.fabric;

import net.fabricmc.api.ModInitializer;

import io.github.leiriad.vibranium.VibraniumMod;

public final class VibraniumModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        VibraniumMod.init();
    }
}
