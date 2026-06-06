package io.github.leiriad.vibranium.fabric;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.fabric.block.entity.VibraniumEntitiesFabric;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;


public final class VibraniumModFabric implements ModInitializer {


    ///Registers item in inventory
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        VibraniumMod.init();
        VibraniumToolEventsFabric.register();
        VibraniumEntitiesFabric.init();

        // Register the Fluid API lookup for Fabric
        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            if (blockEntity instanceof FluidTankEntity tank) {
                return new SingleVariantStorage<FluidVariant>() {
                    @Override
                    protected FluidVariant getBlankVariant() { return FluidVariant.blank(); }

                    @Override
                    protected long getCapacity(FluidVariant variant) { return tank.getCapacity(); }

                    @Override
                    protected void onFinalCommit() {
                        tank.setFluid(getResource().getFluid(), getAmount());
                    }
                };
            }
            return null;
        }, VibraniumEntities.FLUID_TANK_ENTITY.get());

    }

}
