package io.github.leiriad.vibranium.fabric.datagen;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import io.github.leiriad.vibranium.feature.VibraniumPurpleAzaleaFeatures;

public class VibraniumDataGenerator implements DataGeneratorEntrypoint {
	///DataGen initializer
	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		// tree registration
		registryBuilder.add(Registries.CONFIGURED_FEATURE, VibraniumPurpleAzaleaFeatures::bootstrap);
	}
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		try {
			Class.forName(VibraniumBlocks.class.getName());
		} catch (ClassNotFoundException e) { e.printStackTrace(); }
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		//Add world generation data
		pack.addProvider(VibraniumWorldGenerator::new);
		//Adds Loot tables
		pack.addProvider(VibraniumLootTableProvider::new);
		//Adds translations
		pack.addProvider((output, registries) -> new VibraniumLanguageProvider(output, registries));
		pack.addProvider((output, registries) -> new VibraniumLanguageProvider(output, registries, "fr_fr"));
		pack.addProvider((output, registries) -> new VibraniumLanguageProvider(output, registries, "es_es"));
	}
}
