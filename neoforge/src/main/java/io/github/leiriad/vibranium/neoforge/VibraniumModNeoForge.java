package io.github.leiriad.vibranium.neoforge;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.client.render.FluidTankRenderer;
import io.github.leiriad.vibranium.entity.ElectricLampEntity;
import io.github.leiriad.vibranium.entity.ElectricWireEntity;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import io.github.leiriad.vibranium.neoforge.block.entity.VibraniumEntitiesNeoForge;
import io.github.leiriad.vibranium.neoforge.client.NeoForgeVibraniumScreen;
import io.github.leiriad.vibranium.screen.ReactorControlPanelScreen;
import io.github.leiriad.vibranium.screen.ReactorHatchScreen;
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
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

@Mod(VibraniumMod.MOD_ID)
public final class VibraniumModNeoForge {
    public VibraniumModNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        VibraniumMod.init();
        VibraniumEntitiesNeoForge.init();

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
                VibraniumEntitiesNeoForge.FLUID_TANK_ENTITY.get(),
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
        // --- REGISTRATION FOR REACTOR CORE ---
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                VibraniumEntitiesNeoForge.REACTOR_CORE_ENTITY.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof ReactorCoreEntity coreEntity) {

                        // Create a SnapshotJournal to track energy state changes within transactions
                        SnapshotJournal<Integer> coreJournal = new SnapshotJournal<>() {
                            @Override
                            protected Integer createSnapshot() {
                                return coreEntity.getEnergyStored();
                            }

                            @Override
                            protected void revertToSnapshot(Integer snapshotValue) {
                                coreEntity.setEnergy(snapshotValue);
                            }
                        };

                        return new EnergyHandler() {
                            @Override
                            public int insert(int maxAmount, TransactionContext transaction) {
                                // The reactor core generates power, it does not support receiving energy
                                return 0;
                            }

                            @Override
                            public int extract(int maxAmount, TransactionContext transaction) {
                                if (maxAmount <= 0) return 0;

                                // If the reactor is built incorrectly, prevent energy extraction
                                if (!coreEntity.isReactorFunctioningCorrectly()) {
                                    return 0;
                                }

                                // Calculate potential extractable energy
                                int energyStored = coreEntity.getEnergyStored();
                                int toExtract = Math.min(energyStored, maxAmount);

                                if (toExtract > 0) {
                                    // Register the current state to the transaction journal before making edits
                                    coreJournal.updateSnapshots(transaction);

                                    // Apply the modification immediately (the system reverts it if the transaction aborts)
                                    coreEntity.extractEnergy(toExtract, false);
                                    return toExtract;
                                }
                                return 0;
                            }

                            @Override
                            public long getAmountAsLong() {
                                return (long) coreEntity.getEnergyStored();
                            }

                            @Override
                            public long getCapacityAsLong() {
                                return (long) coreEntity.getMaxEnergyStored();
                            }
                        };
                    }
                    return null;
                }
        );

        // --- REGISTRATION FOR REINFORCED VIBRANIUM GLASS (ENERGY OUTPUT) ---
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                VibraniumEntitiesNeoForge.REINFORCED_VIBRANIUM_GLASS_ENTITY.get(),
                (glassEntity, direction) -> {
                    // Get the level and position directly from the block entity
                    net.minecraft.world.level.Level level = glassEntity.getLevel();
                    net.minecraft.core.BlockPos pos = glassEntity.getBlockPos();

                    if (level == null) return null;

                    // Scan around the glass block in a 3x3x3 area to locate the Reactor Core
                    for (net.minecraft.core.BlockPos checkPos : net.minecraft.core.BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                        if (level.getBlockEntity(checkPos) instanceof ReactorCoreEntity core) {

                            return new EnergyHandler() {
                                @Override
                                public long getAmountAsLong() {
                                    return core.getEnergyStored();
                                }

                                @Override
                                public long getCapacityAsLong() {
                                    return 100000L;
                                }

                                @Override
                                public int insert(int amount, TransactionContext transactionContext) {
                                    return 0;
                                }

                                @Override
                                public int extract(int amount, TransactionContext transactionContext) {
                                    if (amount <= 0) return 0;

                                    int energyExtracted = core.extractEnergy(amount, true);
                                    if (energyExtracted > 0) {

                                        // Create a snapshot tracker using the core's energy
                                        new net.neoforged.neoforge.transfer.transaction.SnapshotJournal<Integer>() {
                                            @Override
                                            protected Integer createSnapshot() {
                                                return core.getEnergyStored();
                                            }

                                            @Override
                                            protected void revertToSnapshot(Integer snapshot) {
                                                core.setEnergy(snapshot);
                                            }
                                        }.updateSnapshots(transactionContext);

                                        return core.extractEnergy(energyExtracted, false);
                                    }
                                    return 0;
                                }
                            };
                        }
                    }
                    return null;
                }
        );
        // --- REGISTRATION FOR ELECTRIC WIRE (UNIVERSAL ENERGY CONDUCTOR) ---
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                VibraniumEntitiesNeoForge.ELECTRIC_WIRE_ENTITY.get(),
                (wireEntity, direction) -> {
                    if (wireEntity instanceof ElectricWireEntity wire) {

                        // Journal to handle energy rollbacks during transactions
                        net.neoforged.neoforge.transfer.transaction.SnapshotJournal<Integer> wireJournal =
                                new net.neoforged.neoforge.transfer.transaction.SnapshotJournal<>() {
                                    @Override
                                    protected Integer createSnapshot() {
                                        return wire.getEnergyStored();
                                    }

                                    @Override
                                    protected void revertToSnapshot(Integer snapshotValue) {
                                        wire.setEnergy(snapshotValue);
                                    }
                                };

                        return new EnergyHandler() {
                            @Override
                            public int insert(int maxAmount, TransactionContext transaction) {
                                if (maxAmount <= 0) return 0;

                                int inserted = wire.insertEnergy(maxAmount, true);
                                if (inserted > 0) {
                                    wireJournal.updateSnapshots(transaction);
                                    return wire.insertEnergy(inserted, false);
                                }
                                return 0;
                            }

                            @Override
                            public int extract(int maxAmount, TransactionContext transaction) {
                                if (maxAmount <= 0) return 0;

                                int extracted = wire.extractEnergy(maxAmount, true);
                                if (extracted > 0) {
                                    wireJournal.updateSnapshots(transaction);
                                    return wire.extractEnergy(extracted, false);
                                }
                                return 0;
                            }

                            @Override
                            public long getAmountAsLong() {
                                return (long) wire.getEnergyStored();
                            }

                            @Override
                            public long getCapacityAsLong() {
                                return (long) wire.getMaxEnergyStored();
                            }
                        };
                    }
                    return null;
                }
        );

        // --- REGISTRATION FOR LAMPS ---
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                VibraniumEntitiesNeoForge.ELECTRIC_LAMP_ENTITY.get(),
                (lampEntity, direction) -> {
                    if (lampEntity instanceof ElectricLampEntity lamp) {

                        // Journal to handle energy rollbacks during transactions
                        net.neoforged.neoforge.transfer.transaction.SnapshotJournal<Integer> lampJournal =
                                new net.neoforged.neoforge.transfer.transaction.SnapshotJournal<>() {
                                    @Override
                                    protected Integer createSnapshot() {
                                        return lamp.getEnergyStored();
                                    }

                                    @Override
                                    protected void revertToSnapshot(Integer snapshotValue) {
                                        // Fallback adjustment to restore previous energy state
                                        lamp.insertEnergy(snapshotValue - lamp.getEnergyStored(), false);
                                    }
                                };

                        return new EnergyHandler() {
                            @Override
                            public int insert(int maxAmount, TransactionContext transaction) {
                                if (maxAmount <= 0) return 0;

                                int inserted = lamp.insertEnergy(maxAmount, true);
                                if (inserted > 0) {
                                    lampJournal.updateSnapshots(transaction);
                                    return lamp.insertEnergy(inserted, false);
                                }
                                return 0;
                            }

                            @Override
                            public int extract(int maxAmount, TransactionContext transaction) {
                                // Lamps do not allow extracting energy back, only consuming it
                                return 0;
                            }

                            @Override
                            public long getAmountAsLong() {
                                return (long) lamp.getEnergyStored();
                            }

                            @Override
                            public long getCapacityAsLong() {
                                return 1000L;
                            }
                        };
                    }
                    return null;
                }
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