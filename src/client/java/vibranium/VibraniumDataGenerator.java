package vibranium;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import vibranium.init.VibraniumLootTableProvider;

public class VibraniumDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		// On ajoute notre générateur de Loot Tables au pack
		pack.addProvider(VibraniumLootTableProvider::new);
	}
}
