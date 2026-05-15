package io.github.leiriad.vibranium.neoforge.block.entity;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.entity.ReactorControlPanelEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class VibraniumEntitiesNeoforge extends VibraniumEntities {
    public static void init (){
        REACTOR_CORE_ENTITY = (RegistrySupplier<BlockEntityType<ReactorCoreEntity>>) register("reactor_core", ()-> new BlockEntityType<>(ReactorCoreEntity::new, VibraniumBlocks.REACTOR_CORE.get()));
        REACTOR_CONTROL_PANEL_ENTITY = (RegistrySupplier<BlockEntityType<ReactorControlPanelEntity>>) register("reactor_control_panel", ()-> new BlockEntityType<>(ReactorControlPanelEntity::new, VibraniumBlocks.REACTOR_CONTROL_PANEL.get()));
        registerModEntities();
    }
}
