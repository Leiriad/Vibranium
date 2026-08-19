package io.github.leiriad.vibranium.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class VibraniumEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static RegistrySupplier<BlockEntityType<ReactorCoreEntity>> REACTOR_CORE_ENTITY;
    public static RegistrySupplier<BlockEntityType<ReactorControlPanelEntity>> REACTOR_CONTROL_PANEL_ENTITY;
    public static RegistrySupplier<BlockEntityType<ReactorHatchEntity>> REACTOR_HATCH_ENTITY;
    public static RegistrySupplier<BlockEntityType<ReactorPumpEntity>> REACTOR_PUMP_ENTITY;
    public static RegistrySupplier<BlockEntityType<ReactorOutletEntity>> REACTOR_OUTLET_ENTITY;
    public static RegistrySupplier<BlockEntityType<FluidTankEntity>> FLUID_TANK_ENTITY;
    public static RegistrySupplier<BlockEntityType<ElectricWireEntity>> ELECTRIC_WIRE_ENTITY;
    public static RegistrySupplier<BlockEntityType<ReinforcedVibraniumGlassEntity>> REINFORCED_VIBRANIUM_GLASS_ENTITY;
    public static RegistrySupplier<BlockEntityType<ElectricLampEntity>> ELECTRIC_LAMP_ENTITY;
    public static RegistrySupplier<BlockEntityType<ElectricHeaterEntity>> ELECTRIC_HEATER_ENTITY;


    public static void registerModEntities(){
        BLOCK_ENTITIES.register();
    }
    public static <T extends BlockEntityType<?>> RegistrySupplier<?> register(String name, Supplier <T> supplier){
        return BLOCK_ENTITIES.register(Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, name), supplier);
    }
}
