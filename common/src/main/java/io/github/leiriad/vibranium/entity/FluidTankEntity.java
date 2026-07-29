package io.github.leiriad.vibranium.entity;

import com.mojang.datafixers.util.Pair;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.function.BiConsumer;

public class FluidTankEntity extends BlockEntity {
    //PROPERTIES
    // Capacity of the tank (10000 mB)
    public static final long capacity = 10000;
    private Fluid storedFluid = Fluids.EMPTY;
    private long fluidAmount = 0;

    //CONSTRUCTORS
    public FluidTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public FluidTankEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.FLUID_TANK_ENTITY.get(), pos, state);
    }

    // GETTERS & SETTERS
    public Fluid getStoredFluid() { return this.storedFluid; }
    public long getFluidAmount() { return this.fluidAmount; }
    public long getCapacity() { return this.capacity; }
    public void setFluid(Fluid fluid, long amount) {
        // Fallback to EMPTY if fluid is null or explicit
        this.storedFluid = fluid != null ? fluid : Fluids.EMPTY;
        this.fluidAmount = Math.min(amount, capacity);
        if (this.fluidAmount <= 0) {
            this.storedFluid = Fluids.EMPTY;
        }
        setChanged(); // Marks the block entity for saving

        // Synchronize data from server to client render thread
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    //METHODS
    public void tick(Level level, BlockPos pos) {
        if (level.isClientSide()) return;

        // Handle generic vertical column logic (falling down and overflowing up)
        this.handleColumnLogic(level, pos);

        if (getStoredFluid().isSame(VibraniumFluids.HOT_WATER_STILL.get())) {
            // The tank is hot it impacts it's neighbor blocks
            BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach(p -> {
                        BlockState voisinState = level.getBlockState(p);
                        // Make ice melt
                        if (voisinState.is(Blocks.ICE)) {
                            level.setBlockAndUpdate(p, Blocks.WATER.defaultBlockState());
                        }
                    });
        }
    }
    /**
     * Handles falling fluids and vertical overflow independently of the fluid type.
     */
    private void handleColumnLogic(Level level, BlockPos pos) {
        if (this.fluidAmount <= 0 || this.storedFluid == Fluids.EMPTY) return;

        // 1. Try to push fluid downwards (falling logic)
        BlockEntity belowBE = level.getBlockEntity(pos.below());
        if (belowBE instanceof FluidTankEntity tankBelow) {
            if (tankBelow.storedFluid == Fluids.EMPTY || tankBelow.storedFluid == this.storedFluid) {
                long accepted = tankBelow.fill(this.fluidAmount, this.storedFluid);
                if (accepted > 0) {
                    this.drain(accepted);
                }
            }
        }

        // 2. Try to push overflow upwards if this tank exceeds its capacity (overflow logic)
        if (this.fluidAmount > this.capacity) {
            BlockEntity aboveBE = level.getBlockEntity(pos.above());
            if (aboveBE instanceof FluidTankEntity tankAbove) {
                if (tankAbove.storedFluid == Fluids.EMPTY || tankAbove.storedFluid == this.storedFluid) {
                    long overflow = this.fluidAmount - this.capacity;
                    long accepted = tankAbove.fill(overflow, this.storedFluid);
                    if (accepted > 0) {
                        this.fluidAmount -= accepted;
                        this.setChanged();
                        level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
                    }
                }
            }
        }
    }
    public long fill(long amount, Fluid fluid) {
        if (amount <= 0 || fluid == Fluids.EMPTY) return 0;

        // If tank is empty accept new fluid
        if (this.storedFluid == Fluids.EMPTY) {
            this.storedFluid = fluid;
        }

        // If right fluid, handle filling and potential overflow cascading
        if (this.storedFluid == fluid) {
            long spaceLeft = this.capacity - this.fluidAmount;

            if (spaceLeft > 0) {
                // There is space in this tank
                long toFill = Math.min(amount, spaceLeft);
                this.fluidAmount += toFill;
                this.setChanged();
                if (this.level != null) this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);

                long remaining = amount - toFill;
                // If there's still fluid left after filling this tank, pass it to the top tank
                if (remaining > 0 && this.level != null) {
                    BlockEntity aboveBE = this.level.getBlockEntity(this.worldPosition.above());
                    if (aboveBE instanceof FluidTankEntity tankAbove) {
                        return toFill + tankAbove.fill(remaining, fluid);
                    }
                }
                return toFill;
            } else {
                // This tank is already full, directly try to fill the tank above
                if (this.level != null) {
                    BlockEntity aboveBE = this.level.getBlockEntity(this.worldPosition.above());
                    if (aboveBE instanceof FluidTankEntity tankAbove) {
                        if (tankAbove.getStoredFluid() == Fluids.EMPTY || tankAbove.getStoredFluid() == fluid) {
                            return tankAbove.fill(amount, fluid);
                        }
                    }
                }
            }
        }
        return 0; // Wrong fluid or column is completely full
    }
    public long drain(long maxAmount) {
        long toDrain = Math.min(this.fluidAmount, maxAmount);
        this.fluidAmount -= toDrain;
        if (this.fluidAmount <= 0) {
            this.storedFluid = Fluids.EMPTY;
        }
        this.setChanged();
        if (this.level != null) this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        return toDrain;
    }

    // NBT Data Management
    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        // Fetch the Identifier key directly from the registry using our fluid object
        Identifier fluidKey = BuiltInRegistries.FLUID.getKey(this.storedFluid);

        valueOutput.putString("FluidType", fluidKey.toString());
        valueOutput.putLong("FluidAmount", this.fluidAmount);
        // Write into the ValueOutput using lambda expressions
        this.writeFluidData(valueOutput::putString, valueOutput::putLong);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);

        // Read string from NBT, fallback to empty string if missing
        String fluidStr = valueInput.getStringOr("FluidType", "");

        if (!fluidStr.isEmpty()) {
            Identifier fluidKey = Identifier.tryParse(fluidStr);

            if (fluidKey != null) {
                Optional<Fluid> optionalFluid = BuiltInRegistries.FLUID.getOptional(fluidKey);
                this.storedFluid = optionalFluid.orElse(Fluids.EMPTY);
            } else {
                this.storedFluid = Fluids.EMPTY;
            }
        } else {
            this.storedFluid = Fluids.EMPTY;
        }

        // Read amount from NBT, fallback to 0L if missing
        this.fluidAmount = valueInput.getLongOr("FluidAmount", 0L);
    }
    /**
     * Calculates the total capacity and fluid amount of the entire vertical column this tank belongs to.
     * @return A pair containing L: Total Fluid Amount, R: Total Capacity of the column.
     */
    public Pair<Long, Long> getColumnStats() {
        long totalAmount = this.fluidAmount;
        long totalCapacity = this.capacity;

        // Scan upwards
        BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos().set(this.worldPosition);
        while (this.level != null) {
            nextPos.move(net.minecraft.core.Direction.UP);
            if (this.level.getBlockEntity(nextPos) instanceof FluidTankEntity tank) {
                totalAmount += tank.getFluidAmount();
                totalCapacity += tank.getCapacity();
            } else {
                break; // Hit a non-tank block, end of column
            }
        }

        // Scan downwards
        nextPos.set(this.worldPosition);
        while (this.level != null) {
            nextPos.move(net.minecraft.core.Direction.DOWN);
            if (this.level.getBlockEntity(nextPos) instanceof FluidTankEntity tank) {
                totalAmount += tank.getFluidAmount();
                totalCapacity += tank.getCapacity();
            } else {
                break; // Hit a non-tank block, end of column
            }
        }

        return new Pair<>(totalAmount, totalCapacity);
    }

    // --- Network Synchronization for BER ---

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Tells Minecraft to send a sync packet to the client whenever the block updates
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        // Creates the data tag that will be sent to the client through the packet
        CompoundTag tag = super.getUpdateTag(registries);
        // Write into the network CompoundTag using lambda expressions
        this.writeFluidData(tag::putString, tag::putLong);
        return tag;
    }

    // --- Common internal method to write data ---
    private void writeFluidData(BiConsumer<String, String> stringWriter,
                                BiConsumer<String, Long> longWriter) {
        Identifier fluidKey = BuiltInRegistries.FLUID.getKey(this.storedFluid);
        stringWriter.accept("FluidType", fluidKey.toString());
        longWriter.accept("FluidAmount", this.fluidAmount);
    }
}