package vibranium.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import vibranium.utils.VibraniumConfiguredFeatures;

public class VibraniumDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		// tree registration
		registryBuilder.add(Registries.CONFIGURED_FEATURE, VibraniumConfiguredFeatures::bootstrap);
	}
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
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
