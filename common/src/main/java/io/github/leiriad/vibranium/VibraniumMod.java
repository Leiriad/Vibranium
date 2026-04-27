package io.github.leiriad.vibranium;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumMeteorite;
import io.github.leiriad.vibranium.init.VibraniumTreeDecorators;

public class VibraniumMod {
	public static final String MOD_ID = "vibranium";
	//key to identify data/vibranium/worldgen/placed_feature/meteorite_placed.json
	public static final ResourceKey<PlacedFeature> METEORITE_PLACED_KEY = ResourceKey.create(
			Registries.PLACED_FEATURE,
			Identifier.fromNamespaceAndPath(MOD_ID, "meteorite_placed")
	);
	public static final ResourceKey<PlacedFeature> METEORITE_END_KEY = ResourceKey.create(
			Registries.PLACED_FEATURE,
			Identifier.fromNamespaceAndPath(MOD_ID, "meteorite_end_placed")
	);

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static void init() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Vibranium est chargé!");

		//Initialize Registry Content
		VibraniumBlocks.registerModBlocks();
		VibraniumTreeDecorators.register();
		// Register the Meteorite Feature logic
		//VibraniumMeteorite.registerFeatures();
		VibraniumBlocks.addItemsToTabs();

	}

}