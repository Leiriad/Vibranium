package io.github.leiriad.vibranium.neoforge.block.entity;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.entity.*;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class VibraniumEntitiesNeoForge extends VibraniumEntities {
    public static void init (){
        REACTOR_CORE_ENTITY = (RegistrySupplier<BlockEntityType<ReactorCoreEntity>>) register("reactor_core", ()-> new BlockEntityType<>(ReactorCoreEntity::new, VibraniumBlocks.REACTOR_CORE.get()));
        REACTOR_CONTROL_PANEL_ENTITY = (RegistrySupplier<BlockEntityType<ReactorControlPanelEntity>>) register("reactor_control_panel", ()-> new BlockEntityType<>(ReactorControlPanelEntity::new, VibraniumBlocks.REACTOR_CONTROL_PANEL.get()));
        REACTOR_HATCH_ENTITY = (RegistrySupplier<BlockEntityType<ReactorHatchEntity>>) register("reactor_hatch", ()-> new BlockEntityType<>(ReactorHatchEntity::new, VibraniumBlocks.REACTOR_HATCH.get()));
        REACTOR_PUMP_ENTITY = (RegistrySupplier<BlockEntityType<ReactorPumpEntity>>) register("reactor_pump", ()-> new BlockEntityType<>(ReactorPumpEntity::new, VibraniumBlocks.REACTOR_PUMP.get()));
        FLUID_TANK_ENTITY = (RegistrySupplier<BlockEntityType<FluidTankEntity>>) register("fluid_tank", ()-> new BlockEntityType<>(FluidTankEntity::new, VibraniumBlocks.FLUID_TANK.get()));
        REINFORCED_VIBRANIUM_GLASS_ENTITY = (RegistrySupplier<BlockEntityType<ReinforcedVibraniumGlassEntity>>) register("reinforced_vibranium_glass", ()-> new BlockEntityType<>(ReinforcedVibraniumGlassEntity::new, VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()));
        registerModEntities();
    }
}
