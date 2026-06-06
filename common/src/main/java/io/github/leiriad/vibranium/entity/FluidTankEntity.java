package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

public class FluidTankEntity extends BlockEntity {
    //PROPERTIES
    // Capacity of the tank (e.g., 8000 mB / Fabric drops units)
    private final long capacity = 8000;
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
        setChanged(); // Marks the block entity for saving

        // Synchronize data from server to client render thread
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
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
    private void writeFluidData(java.util.function.BiConsumer<String, String> stringWriter,
                                java.util.function.BiConsumer<String, Long> longWriter) {
        Identifier fluidKey = BuiltInRegistries.FLUID.getKey(this.storedFluid);
        stringWriter.accept("FluidType", fluidKey.toString());
        longWriter.accept("FluidAmount", this.fluidAmount);
    }
}
