package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.ReactorPumpBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

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
        FluidState fluidState = level.getFluidState(targetPos);

        // If next block is water source
        if (fluidState.is(Fluids.WATER) && fluidState.isSource()) {
            // Force max water
            entity.fill(FluidTankEntity.capacity, Fluids.WATER);
        }
    }
}
