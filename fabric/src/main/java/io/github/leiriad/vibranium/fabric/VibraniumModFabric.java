package io.github.leiriad.vibranium.fabric;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.fabric.block.entity.VibraniumEntitiesFabric;
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
        }, VibraniumEntitiesFabric.FLUID_TANK_ENTITY.get());

        // --- REGISTRATION FOR THE REACTOR CORE ---
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {

            // Cast the generic BlockEntity to your specific ReactorCoreEntity
            if (blockEntity instanceof ReactorCoreEntity coreEntity) {

                // Check if the reactor structure is functional before exposing energy
                if (!coreEntity.isReactorFunctioningCorrectly()) {
                    return null;
                }

                // Create the energy storage participant for the Core itself
                SnapshotParticipant<Integer> coreParticipant = new SnapshotParticipant<>() {
                    @Override
                    protected Integer createSnapshot() {
                        return coreEntity.getEnergyStored();
                    }

                    @Override
                    protected void readSnapshot(Integer snapshotValue) {
                        coreEntity.setEnergy(snapshotValue);
                    }
                };

                return new EnergyStorage() {
                    @Override
                    public long insert(long maxAmount, TransactionContext transaction) {
                        // The core itself only outputs energy generated, no insertion allowed
                        return 0L;
                    }

                    @Override
                    public long extract(long maxAmount, TransactionContext transaction) {
                        if (maxAmount <= 0) return 0L;

                        int maxExtract = (int) Math.min(Integer.MAX_VALUE, maxAmount);
                        int simulatedExtract = coreEntity.extractEnergy(maxExtract, true);

                        if (simulatedExtract > 0) {
                            coreParticipant.updateSnapshots(transaction);
                            coreEntity.extractEnergy(simulatedExtract, false);
                            return (long) simulatedExtract;
                        }
                        return 0L;
                    }

                    @Override
                    public long getAmount() {
                        return coreEntity.getEnergyStored();
                    }

                    @Override
                    public long getCapacity() {
                        return coreEntity.getMaxEnergyStored();
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

            return null; // Safe fallback if the block entity is not a ReactorCoreEntity
        }, VibraniumEntitiesFabric.REACTOR_CORE_ENTITY.get());

        // --- REGISTRATION FOR REINFORCED VIBRANIUM GLASS (UNIVERSAL ENERGY PROVIDER) ---
        EnergyStorage.SIDED.registerForBlockEntities((glassEntity, direction) -> {
            // 1. Retrieve the world and position directly from the block entity
            net.minecraft.world.level.Level level = glassEntity.getLevel();
            BlockPos pos = glassEntity.getBlockPos();

            if (level == null) {
                return null;
            }

            // 2. Scan a 3x3x3 area around the glass to locate the Reactor Core
            for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if (level.getBlockEntity(checkPos) instanceof ReactorCoreEntity core) {

                    // Check if the reactor structure is functional
                    if (!core.isReactorFunctioningCorrectly()) {
                        return null;
                    }

                    // Create a participant to handle transaction rollbacks safely
                    SnapshotParticipant<Integer> energyParticipant = new SnapshotParticipant<>() {
                        @Override
                        protected Integer createSnapshot() {
                            // Save the current energy state of the core
                            return core.getEnergyStored();
                        }

                        @Override
                        protected void readSnapshot(Integer snapshotValue) {
                            // Restore the exact energy state if the transaction is aborted
                            core.setEnergy(snapshotValue);
                        }
                    };

                    return new EnergyStorage() {
                        @Override
                        public long insert(long maxAmount, TransactionContext transaction) {
                            // The glass only outputs energy and cannot receive input
                            return 0L;
                        }

                        @Override
                        public long extract(long maxAmount, TransactionContext transaction) {
                            if (maxAmount <= 0) return 0L;

                            int maxExtract = (int) Math.min(Integer.MAX_VALUE, maxAmount);

                            // Step 1: Simulate the energy extraction from the core
                            int simulatedExtract = core.extractEnergy(maxExtract, true);

                            if (simulatedExtract > 0) {
                                // Step 2: Register our participant in the current Fabric transaction
                                energyParticipant.updateSnapshots(transaction);

                                // Step 3: Perform the actual energy extraction (write)
                                core.extractEnergy(simulatedExtract, false);

                                return (long) simulatedExtract;
                            }
                            return 0L;
                        }

                        @Override
                        public long getAmount() {
                            return core.getEnergyStored();
                        }

                        @Override
                        public long getCapacity() {
                            return core.getMaxEnergyStored();
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
            return null; // No functional reactor core found adjacent to this glass
        }, VibraniumEntitiesFabric.REINFORCED_VIBRANIUM_GLASS_ENTITY.get());
    }
}