package vibranium.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class VibraniumWorldGenerator extends FabricDynamicRegistryProvider {
    public VibraniumWorldGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        //Creates Azalea tree Configured Feature JSON
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
    }

    @Override
    public String getName() {
        return "Vibranium World Generation";
    }
}