package io.github.leiriad.vibranium.neoforge.client.compat;

import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class VibraniumNeoForgeREIPlugin implements REIClientPlugin {

    @Override
    public void registerEntries(EntryRegistry registry) {
        // Remove your custom milk fluid from REI's global display grid
        if (VibraniumFluids.VANILLA_MILK_STILL.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumFluids.VANILLA_MILK_STILL.get()));
        }

        if (VibraniumFluids.HOT_WATER_STILL.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumFluids.HOT_WATER_STILL.get()));
        }

        if (VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get()));
        }

        if (VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get()));
        }

        if (VibraniumBlocks.VIBRANIUM_FARMLAND.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumBlocks.VIBRANIUM_FARMLAND.get()));
        }

        if (VibraniumBlocks.VIBRANIUM_PATH.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumBlocks.VIBRANIUM_PATH.get()));
        }
    }
}
