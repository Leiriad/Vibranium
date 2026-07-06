package io.github.leiriad.vibranium.neoforge;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.client.render.FluidTankRenderer;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import io.github.leiriad.vibranium.neoforge.block.entity.VibraniumEntitiesNeoforge;
import io.github.leiriad.vibranium.neoforge.client.NeoForgeVibraniumScreen;
import io.github.leiriad.vibranium.screen.ReactorControlPanelScreen;
import io.github.leiriad.vibranium.screen.ReactorHatchScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

@Mod(VibraniumMod.MOD_ID)
public final class VibraniumModNeoForge {
    public VibraniumModNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        VibraniumMod.init();
        VibraniumEntitiesNeoforge.init();

        //Register config screen
        if (FMLEnvironment.getDist().isClient()) {
            ModLoadingContext.get().registerExtensionPoint(
                    IConfigScreenFactory.class,
                    () -> (minecraft, parent) -> NeoForgeVibraniumScreen.create(parent)
            );
        }

        // Add listeners to the NeoForge mod event bus
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::registerScreens);
    }

    // Expose the fluid tank to NeoForge's Fluid system & the Reactor to the energy system
    private void registerCapabilities(RegisterCapabilitiesEvent event) {

        // --- REGISTRATION FOR FLUID TANK COMPONENT ---
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

        // --- REGISTRATION FOR REINFORCED VIBRANIUM GLASS (ENERGY OUTPUT) ---
        event.registerBlock(
                Capabilities.Energy.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    // Scan around the glass block in a 3x3x3 area to locate the Reactor Core
                    for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                        if (level.getBlockEntity(checkPos) instanceof ReactorCoreEntity core) {

                            // Return the corrected NeoForge EnergyHandler implementation
                            return new EnergyHandler() {
                                @Override
                                public long getAmountAsLong() {
                                    return core.getEnergy();
                                }

                                @Override
                                public long getCapacityAsLong() {
                                    return 100000L; // Max energy matching core specification
                                }

                                @Override
                                public int insert(int amount, TransactionContext transactionContext) {
                                    // The reactor generates power, it cannot receive energy from external sources
                                    return 0;
                                }

                                @Override
                                public int extract(int amount, TransactionContext transactionContext) {
                                    if (amount <= 0) {
                                        return 0;
                                    }

                                    // Simulate extraction first to see how much we can actually pull
                                    int energyExtracted = core.extractEnergy(amount, true);
                                    if (energyExtracted > 0) {

                                        // Instantiate a SnapshotJournal to safely register rollback logic into NeoForge's system
                                        new net.neoforged.neoforge.transfer.transaction.SnapshotJournal<Integer>() {
                                            @Override
                                            protected Integer createSnapshot() {
                                                // Capture the current energy level before modification
                                                return core.getEnergy();
                                            }

                                            @Override
                                            protected void revertToSnapshot(Integer snapshot) {
                                                // If the transaction aborts, we restore the energy back into the core
                                                // Note: Ensure your core handles changes or you can use a forced addition if needed
                                                int difference = snapshot - core.getEnergy();
                                                if (difference > 0) {
                                                    // Re-inject the extracted energy that was canceled
                                                    core.extractEnergy(-difference, false);
                                                }
                                            }
                                        }.updateSnapshots(transactionContext); // Bind this rollback rule to the transaction context

                                        // Apply the energy change directly to the core immediately
                                        return core.extractEnergy(energyExtracted, false);
                                    }
                                    return 0;
                                }
                            };
                        }
                    }
                    return null;
                },
                VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()
        );
    }

    // Register the BER for NeoForge
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VibraniumEntities.FLUID_TANK_ENTITY.get(), FluidTankRenderer::new);
    }

    //Register screens
    private void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), ReactorControlPanelScreen::new);
        event.register(VibraniumMenus.REACTOR_HATCH_MENU.get(), ReactorHatchScreen::new);
    }

}