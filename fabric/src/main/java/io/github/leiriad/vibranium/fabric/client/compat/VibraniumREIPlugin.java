package io.github.leiriad.vibranium.fabric.client.compat;

import dev.architectury.event.EventResult;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.init.VibraniumItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

public class VibraniumREIPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<?> BUCKET_CATEGORY =
            CategoryIdentifier.of("roughlyenoughitems", "plugins/bucket");
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.registerVisibilityPredicate(category -> {

            if (category.getCategoryIdentifier().getPath().contains("bucket")) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }
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

        if (VibraniumItems.HOT_WATER_BUCKET.get() != null) {
            registry.removeEntry(EntryStacks.of(VibraniumItems.HOT_WATER_BUCKET.get()));
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
