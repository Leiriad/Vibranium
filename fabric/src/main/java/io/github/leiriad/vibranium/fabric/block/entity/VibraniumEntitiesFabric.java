package io.github.leiriad.vibranium.fabric.block.entity;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.entity.*;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class VibraniumEntitiesFabric extends VibraniumEntities {
	public static void init(){
        //registers block entity types
        REACTOR_CORE_ENTITY = (RegistrySupplier<BlockEntityType<ReactorCoreEntity>>) register("reactor_core", ()-> FabricBlockEntityTypeBuilder.create(ReactorCoreEntity::new, VibraniumBlocks.REACTOR_CORE.get()).build());
        REACTOR_CONTROL_PANEL_ENTITY = (RegistrySupplier<BlockEntityType<ReactorControlPanelEntity>>) register("reactor_control_panel", ()-> FabricBlockEntityTypeBuilder.create(ReactorControlPanelEntity::new, VibraniumBlocks.REACTOR_CONTROL_PANEL.get()).build());
        REACTOR_HATCH_ENTITY = (RegistrySupplier<BlockEntityType<ReactorHatchEntity>>) register("reactor_hatch", ()-> FabricBlockEntityTypeBuilder.create(ReactorHatchEntity::new, VibraniumBlocks.REACTOR_HATCH.get()).build());
        REACTOR_PUMP_ENTITY = (RegistrySupplier<BlockEntityType<ReactorPumpEntity>>) register("reactor_pump", ()-> FabricBlockEntityTypeBuilder.create(ReactorPumpEntity::new, VibraniumBlocks.REACTOR_PUMP.get()).build());
        REACTOR_OUTLET_ENTITY = (RegistrySupplier<BlockEntityType<ReactorOutletEntity>>) register("reactor_oulet", ()-> FabricBlockEntityTypeBuilder.create(ReactorOutletEntity::new, VibraniumBlocks.REACTOR_OUTLET.get()).build());
        FLUID_TANK_ENTITY = (RegistrySupplier<BlockEntityType< FluidTankEntity>>) register("fluid_tank", ()-> FabricBlockEntityTypeBuilder.create(FluidTankEntity::new, VibraniumBlocks.FLUID_TANK.get()).build());
        REINFORCED_VIBRANIUM_GLASS_ENTITY = (RegistrySupplier<BlockEntityType<ReinforcedVibraniumGlassEntity>>) register("reinforced_vibranium_glass", ()-> FabricBlockEntityTypeBuilder.create(ReinforcedVibraniumGlassEntity::new, VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()).build());
        ELECTRIC_WIRE_ENTITY = (RegistrySupplier<BlockEntityType<ElectricWireEntity>>) register("electric_wire", ()-> FabricBlockEntityTypeBuilder.create(ElectricWireEntity::new,
                            VibraniumBlocks.ELECTRIC_WIRE.get(),
                            VibraniumBlocks.ELECTRIC_WIRE_WALL.get()).build());
        ELECTRIC_LAMP_ENTITY = (RegistrySupplier<BlockEntityType<ElectricLampEntity>>) register("base_electric_lamp", ()-> FabricBlockEntityTypeBuilder.create(ElectricLampEntity::new, VibraniumBlocks.FLAT_ELECTRIC_LAMP.get()).build());
        ELECTRIC_HEATER_ENTITY = (RegistrySupplier<BlockEntityType<ElectricHeaterEntity>>) register("electric_heater", ()-> FabricBlockEntityTypeBuilder.create(ElectricHeaterEntity::new, VibraniumBlocks.ELECTRIC_HEATER.get()).build());

        registerModEntities();
    }
}
