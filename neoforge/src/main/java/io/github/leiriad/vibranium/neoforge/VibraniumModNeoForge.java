package io.github.leiriad.vibranium.neoforge;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumStructures;
import io.github.leiriad.vibranium.neoforge.block.entity.VibraniumEntitiesNeoforge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;


@Mod(VibraniumMod.MOD_ID)
public final class VibraniumModNeoForge {
    public VibraniumModNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        VibraniumMod.init();
        VibraniumEntitiesNeoforge.init();
    }
}
