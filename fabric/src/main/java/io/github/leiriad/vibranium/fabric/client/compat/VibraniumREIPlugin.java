package io.github.leiriad.vibranium.fabric.client.compat;

import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class VibraniumREIPlugin implements REIClientPlugin {

    @Override
    public void registerEntries(EntryRegistry registry) {
        // Hide Items / Blocks from REI's global display grid
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

        // Hide Fluids
        if (VibraniumFluids.VANILLA_MILK_STILL.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumFluids.VANILLA_MILK_STILL.get()));
        }

        if (VibraniumFluids.HOT_WATER_STILL.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumFluids.HOT_WATER_STILL.get()));
        }
    }
}
