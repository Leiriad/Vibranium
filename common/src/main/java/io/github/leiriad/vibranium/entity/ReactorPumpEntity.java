package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.ReactorPumpBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class ReactorPumpEntity extends FluidTankEntity {
    // CONSTRUCTOR
    public ReactorPumpEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public ReactorPumpEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.REACTOR_PUMP_ENTITY.get(), pos, state);
    }
    //METHODS
    public static void tick(Level level, BlockPos pos, BlockState state, ReactorPumpEntity entity) {
        if (level.isClientSide()) return;

        // Check direction to find water source
        Direction facing = state.getValue(ReactorPumpBlock.FACING);
        BlockPos targetPos = pos.relative(facing);

        boolean isValidWaterSource = isBodyOfWaterLargeEnough(level, targetPos, 2);
        if (isValidWaterSource) {
            // Force max water
            long filled = entity.fill(100, Fluids.WATER);

            if (filled > 0) {
                entity.setChanged();
            }
        }
    }
    private static boolean isBodyOfWaterLargeEnough(Level level, BlockPos startPos, int minRequiredWaterBlocks) {
        FluidState fluidState = level.getFluidState(startPos);
        boolean isWater = fluidState.is(Fluids.WATER);
        boolean isSource = fluidState.isSource();
        if (!(fluidState.is(Fluids.WATER) && fluidState.isSource())) {
            return false;
        }

        int waterCount = 0;

        // Scan 6 next direction
        for (Direction dir : Direction.values()) {
            BlockPos checkPos = startPos.relative(dir);

            if (level.getFluidState(checkPos).is(Fluids.WATER)) {
                waterCount++;
            }

            // If neighbor is more than 1 block of water pump can be filled
            if (waterCount >= (minRequiredWaterBlocks - 1)) {
                return true;
            }
        }

        return false;
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
