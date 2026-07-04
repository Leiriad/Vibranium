package io.github.leiriad.vibranium.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class VibraniumSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(VibraniumMod.MOD_ID, Registries.SOUND_EVENT);

    // Create the identifier for the meltdown alarm sound
    public static final RegistrySupplier<SoundEvent> MELTDOWN_ALARM = SOUND_EVENTS.register(
            "meltdown_alarm",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "meltdown_alarm"))
    );

    public static void init() {
        SOUND_EVENTS.register();
    }
}
