package io.github.leiriad.vibranium.fabric;

import io.github.leiriad.vibranium.VibraniumMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.levelgen.GenerationStep;


import static io.github.leiriad.vibranium.VibraniumMod.METEORITE_END_KEY;
import static io.github.leiriad.vibranium.VibraniumMod.METEORITE_PLACED_KEY;


public final class VibraniumModFabric implements ModInitializer {


    ///Registers item in inventory
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        VibraniumMod.init();
        VibraniumToolEventsFabric.register();
    }

}
