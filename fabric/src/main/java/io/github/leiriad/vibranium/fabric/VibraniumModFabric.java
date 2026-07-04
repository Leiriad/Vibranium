package io.github.leiriad.vibranium.fabric;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.fabric.block.entity.VibraniumEntitiesFabric;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import team.reborn.energy.api.EnergyStorage;

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

        // --- REGISTRATION FOR REINFORCED VIBRANIUM GLASS (ENERGY OUTPUT) ---
        EnergyStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, context) -> {

            // Scan around the glass block in a 3x3x3 area to locate the Reactor Core
            for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if (level.getBlockEntity(checkPos) instanceof ReactorCoreEntity core) {

                    // Instantiate a SnapshotParticipant to handle rollbacks during Fabric transactions
                    SnapshotParticipant<Integer> energyParticipant = new SnapshotParticipant<>() {
                        @Override
                        protected Integer createSnapshot() {
                            // Capture the current energy level before modification
                            return core.getEnergy();
                        }

                        @Override
                        protected void readSnapshot(Integer snapshot) {
                            // If the transaction aborts, rollback the core's energy level
                            int difference = snapshot - core.getEnergy();
                            if (difference > 0) {
                                // Re-inject the extracted energy that was canceled
                                core.extractEnergy(-difference, false);
                            }
                        }
                    };

                    // Return the correct TeamReborn EnergyStorage implementation
                    return new EnergyStorage() {
                        @Override
                        public long insert(long maxAmount, TransactionContext transaction) {
                            // The reactor generates power, it cannot receive energy from external sources
                            return 0L;
                        }

                        @Override
                        public long extract(long maxAmount, TransactionContext transaction) {
                            if (maxAmount <= 0) {
                                return 0L;
                            }

                            // Simulate extraction first to see how much we can actually pull
                            int energyExtracted = core.extractEnergy((int) maxAmount, true);
                            if (energyExtracted > 0) {
                                // Register the rollback behavior inside Fabric's transaction manager
                                energyParticipant.updateSnapshots(transaction);

                                // Perform the actual extraction immediately on the core
                                return (long) core.extractEnergy(energyExtracted, false);
                            }
                            return 0L;
                        }

                        @Override
                        public long getAmount() {
                            return core.getEnergy();
                        }

                        @Override
                        public long getCapacity() {
                            return 100000L; // Max energy matching core specification
                        }

                        @Override
                        public boolean supportsInsertion() {
                            return false;
                        }

                        @Override
                        public boolean supportsExtraction() {
                            return true;
                        }
                    };
                }
            }
            return null;
        }, VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get());
    }
}