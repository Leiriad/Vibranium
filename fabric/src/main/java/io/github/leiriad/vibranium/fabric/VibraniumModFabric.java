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
                // Create the storage instance
                SingleVariantStorage<FluidVariant> storage = new SingleVariantStorage<FluidVariant>() {
                    @Override
                    protected FluidVariant getBlankVariant() {
                        return FluidVariant.blank();
                    }

                    @Override
                    protected long getCapacity(FluidVariant variant) {
                        // Convert internal mB capacity into Fabric droplets (1 mB = 81 droplets)
                        return tank.getCapacity() * 81;
                    }

                    @Override
                    protected void onFinalCommit() {
                        // Transaction successful! Convert droplets back to mB and update the common entity
                        long amountInMb = this.getAmount() / 81;
                        tank.setFluid(this.getResource().getFluid(), amountInMb);
                    }
                };

                // Inject the current common tank state into Fabric's internal fields before any operation
                storage.variant = FluidVariant.of(tank.getStoredFluid());
                storage.amount = tank.getFluidAmount() * 81;

                return storage;
            }
            return null;
        }, VibraniumEntities.FLUID_TANK_ENTITY.get());
    }
}