package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.BaseElectricWireBlock;
import io.github.leiriad.vibranium.block.KillSwitchBlock;
import io.github.leiriad.vibranium.init.EnergyApiHelper;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
    private final int capacity = maxTransfer; // Capacity buffer for the wire
    private static final Map<Direction, Boolean> connectionCache = new EnumMap<>(Direction.class);

    // Track whether this cable has a valid path to a machine source
    private static boolean isConnectedToMachineSource = false;

    public ElectricWireEntity(BlockPos pos, BlockState state) {
        super(VibraniumEntities.ELECTRIC_WIRE_ENTITY.get(), pos, state);
    }

    /**
     * Determines if the wire should connect to a specific direction.
     * Evaluates lever/kill-switch states and combines them with the block's connection rules.
     */
    public boolean shouldConnectTo(Direction checkDir) {
        return connectionCache.computeIfAbsent(checkDir, dir -> {
            if (level == null) return false;

            BlockPos target = worldPosition.relative(dir);
            BlockState targetState = level.getBlockState(target);

            // Check if target is a KillSwitch: MUST be NOT powered (OFF) to allow connection
            if (targetState.getBlock() instanceof KillSwitchBlock) {
                return targetState.hasProperty(BlockStateProperties.POWERED)
                        && !targetState.getValue(BlockStateProperties.POWERED);
            }

            // Check if target is a regular lever: MUST be ON to allow connection
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
     * ENERGY LOGIC
     * Pull energy from sources and push it to adjacent devices
     */
    public static void tick(Level level, BlockPos pos, BlockState state, ElectricWireEntity wire) {
        if (level.isClientSide()) return;
        connectionCache.clear();
        // Helper to check if a direction has an active connection based on block properties
        Predicate<Direction> hasConnection = (dir) -> {
            BooleanProperty prop = BaseElectricWireBlock.PROPERTY_BY_DIRECTION.get(dir);
            return prop != null && state.hasProperty(prop) && state.getValue(prop);
        };

        // SOURCE CHECK:
        // Direct check against real sources / switches
        boolean hasSourceAccess = false;

        for (Direction direction : Direction.values()) {
            if (!hasConnection.test(direction)) continue;
            if (!wire.shouldConnectTo(direction)) continue;

            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);

            if (targetState.getBlock() instanceof LeverBlock) {
                boolean isPowered = targetState.hasProperty(BlockStateProperties.POWERED) && targetState.getValue(BlockStateProperties.POWERED);
                boolean canPass = (targetState.getBlock() instanceof KillSwitchBlock) ? !isPowered : isPowered;

                if (canPass) {
                    BlockPos beyondPos = targetPos.relative(direction);
                    if (EnergyApiHelper.isEnergySource(level, beyondPos, direction)) {
                        hasSourceAccess = true;
                        break;
                    }
                }
                continue;
            }

            if (EnergyApiHelper.isEnergySource(level, targetPos, direction)) {
                hasSourceAccess = true;
                break;
            }
        }

        // If no direct source, inherit from adjacent wire neighbors ONLY if they are connected and permitted
        if (!hasSourceAccess) {
            for (Direction direction : Direction.values()) {
                if (!hasConnection.test(direction)) continue;
                if (!wire.shouldConnectTo(direction)) continue;

                BlockPos targetPos = pos.relative(direction);
                BlockEntity targetEntity = level.getBlockEntity(targetPos);
                if (targetEntity instanceof ElectricWireEntity neighborWire) {
                    if (neighborWire.isConnectedToMachineSource) {
                        hasSourceAccess = true;
                        break;
                    }
                }
            }
        }

        // Check diagonal / corner connections if still no access
        if (!hasSourceAccess && state.getBlock() instanceof BaseElectricWireBlock wireBlock) {
            Direction attachedFace = wireBlock.getAttachedFace(state);

            for (Direction dir : Direction.values()) {
                BooleanProperty prop = BaseElectricWireBlock.PROPERTY_BY_DIRECTION.get(dir);
                if (prop != null && state.hasProperty(prop) && !state.getValue(prop)) continue;

                BlockPos diagonalPos = pos.relative(attachedFace).relative(dir);
                BlockState diagonalState = level.getBlockState(diagonalPos);

                if (diagonalState.getBlock() instanceof BaseElectricWireBlock diagonalWireBlock) {
                    if (diagonalWireBlock.getAttachedFace(diagonalState) == dir.getOpposite()) {
                        BlockEntity diagonalEntity = level.getBlockEntity(diagonalPos);
                        if (diagonalEntity instanceof ElectricWireEntity neighborWire && neighborWire.isConnectedToMachineSource) {
                            hasSourceAccess = true;
                            break;
                        }
                    }
                }
            }
        }
        if (isConnectedToMachineSource != hasSourceAccess) {
            isConnectedToMachineSource = hasSourceAccess;
            wire.setChanged();
        }

       // PULL ENERGY from adjacent valid sources if we have access
        if (wire.energyStored < wire.capacity) {
            for (Direction direction : Direction.values()) {
                if (wire.energyStored >= wire.capacity) break;
                if (!hasConnection.test(direction)) continue;
                if (!wire.shouldConnectTo(direction)) continue;

                BlockPos targetPos = pos.relative(direction);
                BlockState targetState = level.getBlockState(targetPos);

                if (targetState.getBlock() instanceof LeverBlock) {
                    BlockPos beyondPos = targetPos.relative(direction);
                    boolean isPowered = targetState.hasProperty(BlockStateProperties.POWERED) && targetState.getValue(BlockStateProperties.POWERED);

                    if (targetState.getBlock() instanceof KillSwitchBlock) {
                        if (!isPowered) {
                            int extracted = EnergyApiHelper.pullEnergy(level, beyondPos, direction, Math.min(wire.capacity - wire.energyStored, wire.maxTransfer));
                            if (extracted > 0) {
                                wire.energyStored += extracted;
                                wire.setChanged();
                            }
                        }
                    } else {
                        if (isPowered) {
                            int extracted = EnergyApiHelper.pullEnergy(level, beyondPos, direction, Math.min(wire.capacity - wire.energyStored, wire.maxTransfer));
                            if (extracted > 0) {
                                wire.energyStored += extracted;
                                wire.setChanged();
                            }
                        }
                    }
                    continue;
                }
                if (!level.isLoaded(targetPos)) return;
                int extracted = EnergyApiHelper.pullEnergy(level, targetPos, direction, Math.min(wire.capacity - wire.energyStored, wire.maxTransfer));
                if (extracted > 0) {
                    wire.energyStored += extracted;
                    wire.setChanged();
                }
            }
        }


        // PUSH ENERGY to connected adjacent blocks
        if (wire.energyStored > 0) {
            for (Direction direction : Direction.values()) {
                if (wire.energyStored <= 0) break;
                if (!hasConnection.test(direction)) continue;
                if (!wire.shouldConnectTo(direction)) continue;

                BlockPos targetPos = pos.relative(direction);
                BlockState targetState = level.getBlockState(targetPos);

                if (targetState.getBlock() instanceof LeverBlock) {
                    BlockPos beyondPos = targetPos.relative(direction);
                    boolean isPowered = targetState.hasProperty(BlockStateProperties.POWERED) && targetState.getValue(BlockStateProperties.POWERED);

                    boolean canTransfer = (targetState.getBlock() instanceof KillSwitchBlock) ? !isPowered : isPowered;

                    if (canTransfer) {
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
                            if (!level.isLoaded(targetPos)) return;
                            int distributed = EnergyApiHelper.distributeEnergy(level, beyondPos, direction, Math.min(wire.energyStored, wire.maxTransfer));
                            if (distributed > 0) {
                                wire.energyStored -= distributed;
                                wire.setChanged();
                            }
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
                    if (!level.isLoaded(targetPos)) return;
                    int distributed = EnergyApiHelper.distributeEnergy(level, targetPos, direction, Math.min(wire.energyStored, wire.maxTransfer));
                    if (distributed > 0) {
                        wire.energyStored -= distributed;
                        wire.setChanged();
                    }
                }
            }

            // PUSH ENERGY through outer-corners (diagonal adjacent wires) respecting connection rules
            if (state.getBlock() instanceof BaseElectricWireBlock wireBlock) {
                Direction attachedFace = wireBlock.getAttachedFace(state);

                for (Direction dir : Direction.values()) {
                    if (wire.energyStored <= 0) break;

                    BooleanProperty prop = BaseElectricWireBlock.PROPERTY_BY_DIRECTION.get(dir);
                    if (prop != null && state.hasProperty(prop) && !state.getValue(prop)) continue;
                    if (!wire.shouldConnectTo(dir)) continue; // Ensure corner traversal respects switches/levers!

                    BlockPos diagonalPos = pos.relative(attachedFace).relative(dir);
                    BlockState diagonalState = level.getBlockState(diagonalPos);

                    if (diagonalState.getBlock() instanceof BaseElectricWireBlock diagonalWireBlock) {
                        if (diagonalWireBlock.getAttachedFace(diagonalState) == dir.getOpposite()) {
                            BlockEntity diagonalEntity = level.getBlockEntity(diagonalPos);
                            if (diagonalEntity instanceof ElectricWireEntity targetWire) {
                                if (wire.energyStored > targetWire.energyStored) {
                                    int transferAmount = Math.min(wire.energyStored - targetWire.energyStored, wire.maxTransfer) / 2;
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

        // Update source connection state and trigger purge if connection was lost
        boolean lostSource = wire.isConnectedToMachineSource && !hasSourceAccess;

        if (wire.isConnectedToMachineSource != hasSourceAccess) {
            wire.isConnectedToMachineSource = hasSourceAccess;
            wire.setChanged();
        }

        if (lostSource) {
            wire.purgeEnergyNetwork();
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
            energyReceived += 0; // standard update
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
     * Instantly clears this cable's energy and propagates the purge strictly through valid connected paths downstream.
     */
    public void purgeEnergyNetwork() {
        if (this.energyStored > 0) {
            this.energyStored = 0;
            this.setChanged();

            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        // Propagate purge only to valid connected neighbors that are now cut off from source
        for (Direction direction : Direction.values()) {
            // If the connection is blocked by a closed switch/lever in this direction, do not propagate past it!
            if (!this.shouldConnectTo(direction)) continue;

            BlockPos neighborPos = worldPosition.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            // Do not cross back through levers or switches during a purge traversal
            if (neighborState.getBlock() instanceof LeverBlock || neighborState.getBlock() instanceof KillSwitchBlock) {
                continue;
            }

            if (level != null) {
                BlockEntity neighborEntity = level.getBlockEntity(neighborPos);

                if (neighborEntity instanceof ElectricWireEntity neighborWire) {
                    if (neighborWire.isConnectedToMachineSource || neighborWire.getEnergyStored() > 0) {
                        neighborWire.isConnectedToMachineSource = false;
                        neighborWire.purgeEnergyNetwork();
                    }
                }
            }
        }
    }

    /**
     * Clears the connection cache when a neighbor updates.
     */
    public void invalidateConnectionCache() {
        connectionCache.clear();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // --- NBT SAVE & LOAD ---

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