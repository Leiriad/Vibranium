package io.github.leiriad.vibranium;

import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import io.github.leiriad.vibranium.event.VibraniumEvents;
import io.github.leiriad.vibranium.init.*;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibraniumMod {
	public static final String MOD_ID = "vibranium";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static void init() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Vibranium is loaded!");

		//Initialize Registry Content
		VibraniumDataComponents.DATA_COMPONENTS.register();
		VibraniumBlocks.registerModBlocks();
		VibraniumFluids.register();
		VibraniumItems.registerModItems();
		VibraniumCreativeTabs.registerModTabs();
		VibraniumMenus.registerModMenus();
		VibraniumSounds.init();
		VibraniumConfigManager.init();

		//Register features
		VibraniumTreeDecorators.register();

		// Register the Meteorite Structure logics
		VibraniumStructures.register();

		//Registers Brewing recipes
		VibraniumBrewingRecipes.register();

		//Creative tabs
		VibraniumBlocks.addBlocksToTabs();
		VibraniumItems.addItemsToTabs();

		//Events
		VibraniumEvents.registerEvents();
	}

}