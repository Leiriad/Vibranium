package io.github.leiriad.vibranium.fabric.block.entity;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.entity.ReactorControlPanelEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class VibraniumEntitiesFabric extends VibraniumEntities {
	public static void init(){
        //registers block entity types
        REACTOR_CORE_ENTITY = (RegistrySupplier<BlockEntityType<ReactorCoreEntity>>) register("reactor_core", ()-> FabricBlockEntityTypeBuilder.create(ReactorCoreEntity::new, VibraniumBlocks.REACTOR_CORE.get()).build());
        REACTOR_CONTROL_PANEL_ENTITY = (RegistrySupplier<BlockEntityType<ReactorControlPanelEntity>>) register("reactor_control_panel", ()-> FabricBlockEntityTypeBuilder.create(ReactorControlPanelEntity::new, VibraniumBlocks.REACTOR_CONTROL_PANEL.get()).build());
        registerModEntities();
    }
}
