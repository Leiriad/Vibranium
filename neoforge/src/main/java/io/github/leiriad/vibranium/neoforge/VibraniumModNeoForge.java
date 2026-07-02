package io.github.leiriad.vibranium.neoforge;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.client.render.FluidTankRenderer;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.neoforge.block.entity.VibraniumEntitiesNeoforge;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

@Mod(VibraniumMod.MOD_ID)
public final class VibraniumModNeoForge {
    public VibraniumModNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        VibraniumMod.init();
        VibraniumEntitiesNeoforge.init();

        // Add listeners to the NeoForge mod event bus
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRenderers);
    }

    // Expose the fluid tank to NeoForge's Fluid system
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                VibraniumEntities.FLUID_TANK_ENTITY.get(),
                (be, direction) -> {
                    FluidTankEntity blockEntity = (FluidTankEntity) be;

                    // Instantiate NeoForge's SnapshotJournal to seamlessly capture and restore fluid state
                    net.neoforged.neoforge.transfer.transaction.SnapshotJournal<Long> journal =
                            new net.neoforged.neoforge.transfer.transaction.SnapshotJournal<>() {
                                @Override
                                protected Long createSnapshot() {
                                    // Capture the current fluid amount as our checkpoint/rollback data
                                    return blockEntity.getFluidAmount();
                                }

                                @Override
                                protected void revertToSnapshot(Long snapshot) {
                                    // Automatically revert the tank back to the previous fluid level if aborted
                                    blockEntity.setFluid(blockEntity.getStoredFluid(), snapshot);
                                }
                            };

                    return new ResourceHandler<FluidResource>() {

                        @Override
                        public int size() {
                            return 1; // Our block only has 1 tank compartment (index 0)
                        }

                        @Override
                        public FluidResource getResource(int index) {
                            if (index != 0 || blockEntity.getStoredFluid() == Fluids.EMPTY || blockEntity.getFluidAmount() <= 0) {
                                return FluidResource.EMPTY;
                            }
                            return FluidResource.of(blockEntity.getStoredFluid());
                        }

                        @Override
                        public long getAmountAsLong(int index) {
                            return index == 0 ? blockEntity.getFluidAmount() : 0L;
                        }

                        @Override
                        public long getCapacityAsLong(int index, FluidResource resource) {
                            return index == 0 ? blockEntity.getCapacity() : 0L;
                        }

                        @Override
                        public boolean isValid(int index, FluidResource resource) {
                            return index == 0 && !resource.isEmpty();
                        }

                        @Override
                        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
                            if (index != 0 || resource.isEmpty() || amount <= 0) {
                                return 0;
                            }

                            // Verify if the tank can accept this fluid type
                            if (blockEntity.getStoredFluid() == Fluids.EMPTY || blockEntity.getStoredFluid() == resource.getFluid()) {
                                long currentAmount = blockEntity.getFluidAmount();
                                long capacity = blockEntity.getCapacity();
                                long accepted = Math.min((long) amount, capacity - currentAmount);

                                if (accepted > 0) {
                                    // Record a data checkpoint inside NeoForge's transaction system
                                    journal.updateSnapshots(transaction);

                                    // Apply the data change immediately to the common tank entity
                                    blockEntity.setFluid(resource.getFluid(), currentAmount + accepted);
                                    return (int) accepted;
                                }
                            }
                            return 0;
                        }

                        @Override
                        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
                            if (index != 0 || resource.isEmpty() || amount <= 0 || blockEntity.getStoredFluid() != resource.getFluid()) {
                                return 0;
                            }

                            long currentAmount = blockEntity.getFluidAmount();
                            long extracted = Math.min((long) amount, currentAmount);

                            if (extracted > 0) {
                                // Record a data checkpoint inside NeoForge's transaction system
                                journal.updateSnapshots(transaction);

                                // Apply the data change immediately to the common tank entity
                                blockEntity.setFluid(blockEntity.getStoredFluid(), currentAmount - extracted);
                                return (int) extracted;
                            }
                            return 0;
                        }
                    };
                }
        );
    }

    // Register the BER for NeoForge
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VibraniumEntities.FLUID_TANK_ENTITY.get(), FluidTankRenderer::new);
    }
}