package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.BaseElectricWireBlock;
import io.github.leiriad.vibranium.init.EnergyApiHelper;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

public class ElectricWireEntity extends BlockEntity {
    private int energyStored = 0;
    public static final int maxTransfer = 5000; // Max transfer rate per operation
    private final int capacity = maxTransfer; // Capacity buffer for the wire (each câble keeps a single tranfert unit in stock to avoid keeping too much energy in the network)
    private final Map<Direction, Boolean> connectionCache = new EnumMap<>(Direction.class);

    public ElectricWireEntity(BlockPos pos, BlockState state) {
        super(VibraniumEntities.ELECTRIC_WIRE_ENTITY.get(), pos, state);
    }

    /**
     * Determines if the wire should connect to a specific direction.
     * Evaluates lever power state (ON required) and combines it with the block's connection rules.
     */
    public boolean shouldConnectTo(Direction connectionDir) {
        return connectionCache.computeIfAbsent(connectionDir, dir -> {
            if (level == null) return false;

            BlockPos target = worldPosition.relative(dir);
            BlockState targetState = level.getBlockState(target);

            // Check if target is a lever: MUST be ON to allow connection/flow
            if (targetState.getBlock() instanceof LeverBlock) {
                return targetState.hasProperty(BlockStateProperties.POWERED)
                        && targetState.getValue(BlockStateProperties.POWERED);
            }

            // Otherwise, ask the Block's core logic rule
            BlockState state = getBlockState();
            if (state.getBlock() instanceof BaseElectricWireBlock wireBlock) {
                Direction attachedFace = wireBlock.getAttachedFace(state);
                return wireBlock.shouldConnectTo(level, worldPosition, dir, attachedFace);
            }

            return false;
        });
    }
    /**
    /* ENERGY LOGIC
    /* Pull energy from sources and push it to adjacent devices
     */
    public static void tick(Level level, BlockPos pos, BlockState state, ElectricWireEntity wire) {
        if (level.isClientSide()) return;

        // Helper to check if a direction has an active connection based on block properties
        Predicate<Direction> hasConnection = (dir) -> {
            BooleanProperty prop = BaseElectricWireBlock.PROPERTY_BY_DIRECTION.get(dir);
            return prop != null && state.hasProperty(prop) && state.getValue(prop);
        };

        // SOURCE CHECK
        boolean isConnectedToSource = false;
        for (Direction direction : Direction.values()) {
            if (!hasConnection.test(direction)) continue;
            if (!wire.shouldConnectTo(direction)) continue;
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);
            BlockEntity targetEntity = level.getBlockEntity(targetPos);

            //If other link is behind lever, look behind
            if (targetState.getBlock() instanceof LeverBlock) {
                BlockPos beyondPos = targetPos.relative(direction);
                BlockEntity beyondEntity = level.getBlockEntity(beyondPos);

                if (EnergyApiHelper.isEnergyMachine(level, beyondPos, direction) ||
                        (beyondEntity instanceof ElectricWireEntity wireNeighbor && wireNeighbor.getEnergyStored() > 0)) {
                    isConnectedToSource = true;
                    break;
                }
                continue;
            }

            //else
            if (EnergyApiHelper.isEnergyMachine(level, targetPos, direction) ||
                    (targetEntity instanceof ElectricWireEntity wireNeighbor && wireNeighbor.getEnergyStored() > 0)) {
                isConnectedToSource = true;
                break;
            }
        }

        // If no source is connected, instantly empty the buffer to 0 and stop
        if (!isConnectedToSource) return;

        // PULL ENERGY from adjacent valid sources
        if (wire.energyStored < wire.capacity) {
            for (Direction direction : Direction.values()) {
                if (wire.energyStored >= wire.capacity) break;
                if (!hasConnection.test(direction)) continue;

                BlockPos targetPos = pos.relative(direction);
                int extracted = EnergyApiHelper.pullEnergy(level, targetPos, direction, Math.min(wire.capacity - wire.energyStored, wire.maxTransfer));
                if (extracted > 0) {
                    wire.energyStored += extracted;
                    wire.setChanged();
                }
            }
        }

        // PUSH ENERGY to connected adjacent blocks (wires or machines)
        if (wire.energyStored <= 0) return;

        for (Direction direction : Direction.values()) {
            if (wire.energyStored <= 0) break;
            if (!hasConnection.test(direction)) continue;
            if (!wire.shouldConnectTo(direction)) continue;

            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);

            // If neighbor is a lever, push through it
            if (targetState.getBlock() instanceof LeverBlock) {
                BlockPos beyondPos = targetPos.relative(direction);
                BlockEntity beyondEntity = level.getBlockEntity(beyondPos);

                if (beyondEntity instanceof ElectricWireEntity targetWire) {
                    if (wire.energyStored > targetWire.energyStored) {
                        int transferAmount = Math.min(wire.energyStored - targetWire.energyStored, wire.maxTransfer) / 2;
                        if (transferAmount > 0) {
                            int accepted = targetWire.insertEnergy(transferAmount, false);
                            if (accepted > 0) {
                                wire.energyStored -= accepted;
                                wire.setChanged();
                            }
                        }
                    }
                } else {
                    int distributed = EnergyApiHelper.distributeEnergy(level, beyondPos, direction, Math.min(wire.energyStored, wire.maxTransfer));
                    if (distributed > 0) {
                        wire.energyStored -= distributed;
                        wire.setChanged();
                    }
                }
                continue;
            }

            BlockEntity targetEntity = level.getBlockEntity(targetPos);

            if (targetEntity instanceof ElectricWireEntity targetWire) {
                if (wire.energyStored > targetWire.energyStored) {
                    int transferAmount = Math.min(wire.energyStored - targetWire.energyStored, wire.maxTransfer) / 2;
                    if (transferAmount > 0) {
                        int accepted = targetWire.insertEnergy(transferAmount, false);
                        if (accepted > 0) {
                            wire.energyStored -= accepted;
                            wire.setChanged();
                        }
                    }
                }
            } else {
                int distributed = EnergyApiHelper.distributeEnergy(level, targetPos, direction, Math.min(wire.energyStored, wire.maxTransfer));
                if (distributed > 0) {
                    wire.energyStored -= distributed;
                    wire.setChanged();
                }
            }
        }

        // PUSH ENERGY through outer-corners (diagonal adjacent wires)
        if (wire.energyStored > 0 && state.getBlock() instanceof BaseElectricWireBlock wireBlock) {
            Direction attachedFace = wireBlock.getAttachedFace(state);

            for (Direction dir : Direction.values()) {
                if (wire.energyStored <= 0) break;

                // Corner connections must also respect the wire's property state
                BooleanProperty prop = BaseElectricWireBlock.PROPERTY_BY_DIRECTION.get(dir);
                if (prop != null && state.hasProperty(prop) && !state.getValue(prop)) continue;

                BlockPos diagonalPos = pos.relative(attachedFace).relative(dir);
                BlockState diagonalState = level.getBlockState(diagonalPos);

                if (diagonalState.getBlock() instanceof BaseElectricWireBlock diagonalWireBlock) {
                    if (diagonalWireBlock.getAttachedFace(diagonalState) == dir.getOpposite()) {
                        BlockEntity diagonalEntity = level.getBlockEntity(diagonalPos);
                        if (diagonalEntity instanceof ElectricWireEntity targetWire) {
                            if (wire.energyStored > targetWire.energyStored) {
                                int transferAmount = Math.min(wire.energyStored - targetWire.energyStored, wire.maxTransfer) / 2;
                                if (transferAmount > 0) {
                                    int accepted = targetWire.insertEnergy(transferAmount, false);
                                    if (accepted > 0) {
                                        wire.energyStored -= accepted;
                                        wire.setChanged();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public int getEnergyStored() {
        return this.energyStored;
    }

    public int getMaxEnergyStored() {
        return this.capacity;
    }

    public void setEnergy(int energy) {
        this.energyStored = Math.max(0, Math.min(energy, this.capacity));
        this.setChanged();
    }

    public int insertEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energyStored, Math.min(maxTransfer, maxReceive));
        if (!simulate && energyReceived > 0) {
            energyStored += energyReceived;
            this.setChanged();
        }
        return energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energyStored, Math.min(maxTransfer, maxExtract));
        if (!simulate && energyExtracted > 0) {
            energyStored -= energyExtracted;
            this.setChanged();
        }
        return energyExtracted;
    }

    /**
     * Clears the connection cache when a neighbor updates.
     * Call this method from your Block's neighborChanged override.
     */
    public void invalidateConnectionCache() {
        connectionCache.clear();

        // Notify the client/server of the state change if needed
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // --- NBT SAVE & LOAD (To keep energy when reloading chunks) ---

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("Energy", this.energyStored);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.energyStored = valueInput.getInt("Energy").orElse(0);
    }

    // --- NETWORK SYNCHRONIZATION ---

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}