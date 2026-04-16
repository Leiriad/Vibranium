package vibranium.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import vibranium.utils.MeteoriteFeature;
import vibranium.Vibranium;

public class VibraniumMeteorite {

    public static Feature<NoneFeatureConfiguration> METEORITE;

    public static void registerFeatures() {
        METEORITE = register("meteorite", new MeteoriteFeature(NoneFeatureConfiguration.CODEC));
    }

    private static Feature<NoneFeatureConfiguration> register(String name, Feature<NoneFeatureConfiguration> feature) {
        ResourceKey<Feature<?>> key = keyOfFeature(name);
        return Registry.register(BuiltInRegistries.FEATURE, key, feature);
    }

    private static ResourceKey<Feature<?>> keyOfFeature(String name) {
        return ResourceKey.create(
                Registries.FEATURE,
                Identifier.fromNamespaceAndPath(Vibranium.MOD_ID, name)
        );
    }
}