package vibranium;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vibranium.init.VibraniumBlocks;
import vibranium.utils.VibraniumToolActions;

public class Vibranium implements ModInitializer {
	public static final String MOD_ID = "vibranium";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Vibranium est chargé!");
		VibraniumBlocks.registerModBlocks();
		VibraniumBlocks.initialize();
		//Biome placement
		//Keys creation
		ResourceKey<PlacedFeature> vibraniumOrePlacedKey = ResourceKey.create(
				Registries.PLACED_FEATURE,
				Identifier.fromNamespaceAndPath("vibranium", "vibranium_ore")
		);

		//Use Fabric API to inject ore in world
		BiomeModifications.addFeature(
					BiomeSelectors.foundInOverworld(),
					GenerationStep.Decoration.UNDERGROUND_ORES,
					vibraniumOrePlacedKey
		);

		//Actions on blocks
		VibraniumToolActions.register();
	}
}