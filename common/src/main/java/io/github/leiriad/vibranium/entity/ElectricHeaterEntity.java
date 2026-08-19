package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.BaseElectricLampBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ElectricHeaterEntity extends BlockEntity {
    private int energyStored = 0;
    private static final int CAPACITY = 1; // Max energy buffer
    private static final int CONSUMPTION_PER_TICK = 20; // Energy consumed per tick while lit
    private boolean poweredByRedstone = false;

    public ElectricHeaterEntity(BlockPos pos, BlockState state) {
        super(VibraniumEntities.ELECTRIC_HEATER_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricHeaterEntity lamp) {
        if (level.isClientSide()) return;

        boolean shouldBeLit = false;

        // 1. Check if powered by redstone
        if (lamp.poweredByRedstone) {
            shouldBeLit = true;
        }
        // 2. Check if powered by energy buffer
        else if (lamp.energyStored >= CONSUMPTION_PER_TICK) {
            lamp.energyStored -= CONSUMPTION_PER_TICK;
            shouldBeLit = true;
            lamp.setChanged();
        }
        // If energy buffer is empty, try to consume whatever is left
        else if (lamp.energyStored > 0) {
            lamp.energyStored = 0;
            shouldBeLit = true;
            lamp.setChanged();
        }

        // Update block state lit property if it changed
        if (state.getValue(BaseElectricLampBlock.LIT) != shouldBeLit) {
            level.setBlock(pos, state.setValue(BaseElectricLampBlock.LIT, shouldBeLit), 3);
        }
    }

    // Called when neighbors change to evaluate redstone signal
    public void checkRedstonePower(Level level, BlockPos pos) {
        boolean hasSignal = level.hasNeighborSignal(pos);
        if (this.poweredByRedstone != hasSignal) {
            this.poweredByRedstone = hasSignal;
            this.setChanged();
        }
    }

    // Energy acceptance method compatible with the EnergyApiHelper
    public int insertEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(CAPACITY - energyStored, maxReceive);
        if (!simulate && energyReceived > 0) {
            energyStored += energyReceived;
            this.setChanged();
        }
        return energyReceived;
    }

    public int getEnergyStored() {
        return this.energyStored;
    }
    public int getCapacity() {
        return CAPACITY;
    }

    // --- NBT SAVE & LOAD (To keep energy when reloading chunks) ---

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("Energy", this.energyStored);
        valueOutput.putBoolean("RedstonePowered", this.poweredByRedstone);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.energyStored = valueInput.getInt("Energy").orElse(0);
        this.poweredByRedstone = valueInput.getBooleanOr("RedstonePowered", false);
    }

    // --- NETWORK SYNCHRONIZATION ---

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}