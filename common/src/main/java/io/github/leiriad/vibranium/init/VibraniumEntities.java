package io.github.leiriad.vibranium.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.entity.ReactorControlPanelEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class VibraniumEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    public static RegistrySupplier<BlockEntityType<ReactorCoreEntity>> REACTOR_CORE_ENTITY;
    public static RegistrySupplier<BlockEntityType<ReactorControlPanelEntity>> REACTOR_CONTROL_PANEL_ENTITY;

    public static void registerModEntities(){
        BLOCK_ENTITIES.register();
    }
    public static <T extends BlockEntityType<?>> RegistrySupplier<?> register(String name, Supplier <T> supplier){
        return BLOCK_ENTITIES.register(Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, name), supplier);
    }
}
