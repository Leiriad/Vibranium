package io.github.leiriad.vibranium.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import io.github.leiriad.vibranium.feature.MeteoriteFeature;

public class VibraniumMeteorite {

    public static final Registrar<Feature<?>> FEATURES = RegistrarManager.get(VibraniumMod.MOD_ID).get(Registries.FEATURE);

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> METEORITE = FEATURES.register(
            Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "meteorite"),
            () -> new MeteoriteFeature(NoneFeatureConfiguration.CODEC)
    );

    public static void registerFeatures() {
        VibraniumMod.LOGGER.info("Worldgen features registering...");
    }

}