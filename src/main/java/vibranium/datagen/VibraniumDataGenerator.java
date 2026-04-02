package vibranium.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class VibraniumDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();


		// Adds Loot tables
		pack.addProvider(VibraniumLootTableProvider::new);
		//Adds translations
		pack.addProvider((output, registries) -> new VibraniumLanguageProvider(output, registries));
		pack.addProvider((output, registries) -> new VibraniumLanguageProvider(output, registries, "fr_fr"));
		pack.addProvider((output, registries) -> new VibraniumLanguageProvider(output, registries, "es_es"));
	}
}
