package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.BaseElectricWireBlock;
import io.github.leiriad.vibranium.init.EnergyApiHelper;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ElectricWireEntity extends BlockEntity {
    private int energyStored = 0;
    public static final int maxTransfer = 5000; // Max transfer rate per operation
    private final int capacity = maxTransfer; // Capacity buffer for the wire (each câble keeps a single tranfert unit in stock to avoid keeping too much energy in the network)

    public ElectricWireEntity(BlockPos pos, BlockState state) {
        super(VibraniumEntities.ELECTRIC_WIRE_ENTITY.get(), pos, state);
    }

    // --- ENERGY LOGIC ---
    // Pull energy from sources and push it to adjacent devices
    public static void tick(Level level, BlockPos pos, BlockState state, ElectricWireEntity wire) {
        if (level.isClientSide()) return;

        // Pull energy from adjacent energy sources if the buffer has space
        if (wire.energyStored < wire.capacity) {
            for (Direction direction : Direction.values()) {
                if (wire.energyStored >= wire.capacity) break;

                // Only pull if the wire actually connects to this direction
                BooleanProperty prop = BaseElectricWireBlock.PROPERTY_BY_DIRECTION.get(direction);
                if (prop != null && state.hasProperty(prop) && !state.getValue(prop)) {
                    continue; // Skip if no connection is established on this side
                }

                BlockPos targetPos = pos.relative(direction);
                int extracted = EnergyApiHelper.pullEnergy(level, targetPos, direction, Math.min(wire.capacity - wire.energyStored, wire.maxTransfer));
                if (extracted > 0) {
                    wire.energyStored += extracted;
                    wire.setChanged();
                }
            }
        }

        // Push/Propagate energy to connected adjacent blocks (wires or machines)
        if (wire.energyStored <= 0) return;

        for (Direction direction : Direction.values()) {
            if (wire.energyStored <= 0) break;

            // Check if the wire is connected visually/logically in this direction
            BooleanProperty prop = BaseElectricWireBlock.PROPERTY_BY_DIRECTION.get(direction);
            if (prop != null && state.hasProperty(prop) && !state.getValue(prop)) {
                continue; // Skip if the corner/connection does not exist on this side
            }

            BlockPos targetPos = pos.relative(direction);
            BlockEntity targetEntity = level.getBlockEntity(targetPos);

            // If the neighbor is another wire
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
            }
            // Otherwise, it is a third-party machine or lamp
            else {
                int distributed = EnergyApiHelper.distributeEnergy(level, targetPos, direction, Math.min(wire.energyStored, wire.maxTransfer));
                if (distributed > 0) {
                    wire.energyStored -= distributed;
                    wire.setChanged();
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