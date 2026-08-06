package io.github.leiriad.vibranium.utils;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

///Component to create kinetic charge (int)
public class VibraniumDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(VibraniumMod.MOD_ID, Registries.DATA_COMPONENT_TYPE);
    public static final RegistrySupplier<DataComponentType<Float>> KINETIC_CHARGE =
            DATA_COMPONENTS.register("kinetic_charge",
                    () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).build());
    public static final Supplier<DataComponentType<Boolean>> RESONANCE_MODE =
            DATA_COMPONENTS.register("resonance_mode",
                    () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());

    public static void register() {
        DATA_COMPONENTS.register();
    }
}
