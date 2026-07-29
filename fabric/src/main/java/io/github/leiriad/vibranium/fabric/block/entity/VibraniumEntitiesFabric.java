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
        FLUID_TANK_ENTITY = (RegistrySupplier<BlockEntityType< FluidTankEntity>>) register("fluid_tank", ()-> FabricBlockEntityTypeBuilder.create(FluidTankEntity::new, VibraniumBlocks.FLUID_TANK.get()).build());
        REINFORCED_VIBRANIUM_GLASS_ENTITY = (RegistrySupplier<BlockEntityType<ReinforcedVibraniumGlassEntity>>) register("reinforced_vibranium_glass", ()-> FabricBlockEntityTypeBuilder.create(ReinforcedVibraniumGlassEntity::new, VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()).build());
        registerModEntities();
    }
}
