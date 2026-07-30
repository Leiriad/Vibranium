package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.HotWaterLiquidBlock;
import io.github.leiriad.vibranium.block.ReactorOutletBlock;
import io.github.leiriad.vibranium.block.ReactorPumpBlock;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ReactorOutletEntity extends FluidTankEntity {
    // CONSTRUCTOR
    public ReactorOutletEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public ReactorOutletEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.REACTOR_OUTLET_ENTITY.get(), pos, state);
    }
    //METHODS
    public static void tick(Level level, BlockPos pos, BlockState state, ReactorOutletEntity entity) {
        if (level.isClientSide()) return;

        if (entity.getFluidAmount() > 0) {
            Direction facing = state.getValue(ReactorOutletBlock.FACING);
            BlockPos targetPos = pos.relative(facing);

            // 1. Verify ejection validity (air or water column/basin)
            if (!isRejectionValid(level, targetPos)) {
                return; // Invalid target, fluid remains stored in the outlet
            }

            BlockState targetState = level.getBlockState(targetPos);
            FluidState targetFluid = level.getFluidState(targetPos);

            // 2. Handle ejection logic based on target block state
            if (targetState.isAir()) {
                // Spawn initial hot water source block at maximum temperature (300°C)
                BlockState hotWaterState = VibraniumFluids.HOT_WATER_BLOCK.get().defaultBlockState()
                        .setValue(HotWaterLiquidBlock.TEMPERATURE, 300);

                level.setBlockAndUpdate(targetPos, hotWaterState);
                entity.drain(100);
                entity.setChanged();
            }
            else if (targetState.is(VibraniumFluids.HOT_WATER_BLOCK.get()) || targetFluid.getType().equals(VibraniumFluids.HOT_WATER_FLOWING.get())) {
                // Refresh/maintain maximum temperature for existing hot water
                if (targetState.hasProperty(HotWaterLiquidBlock.TEMPERATURE) && targetState.getValue(HotWaterLiquidBlock.TEMPERATURE) < 300) {
                    level.setBlockAndUpdate(targetPos, targetState.setValue(HotWaterLiquidBlock.TEMPERATURE, 300));
                }
                entity.drain(100);
                entity.setChanged();
            }
            else if (targetFluid.is(Fluids.WATER) || targetFluid.is(Fluids.FLOWING_WATER)) {
                // Thermal dissipation inside vanilla water
                entity.drain(100);
                entity.setChanged();

                // Visual steam/bubble effects during dissipation
                if (level.getGameTime() % 10 == 0 && level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP,
                            targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                            4, 0.1, 0.1, 0.1, 0.05);
                    serverLevel.playSound(null, targetPos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.4F, 1.2F);
                }
            }
        }
    }

    private static boolean isRejectionValid(Level level, BlockPos targetPos) {
        BlockState targetState = level.getBlockState(targetPos);
        FluidState targetFluid = level.getFluidState(targetPos);

        // CASE 1: Open air ejection is always allowed
        if (targetState.isAir()) {
            return true;
        }

        // CASE 2: Check if the target is any type of water (Vanilla or Custom Hot Water)
        boolean isTargetWater = targetFluid.is(Fluids.WATER) ||
                targetFluid.is(Fluids.FLOWING_WATER) ||
                targetState.is(VibraniumFluids.HOT_WATER_BLOCK.get()) ||
                targetFluid.getType().equals(VibraniumFluids.HOT_WATER_FLOWING.get());

        if (isTargetWater) {
            int connectedWaterCount = 0;

            // Scan all 6 adjacent directions to ensure it is not an isolated single water block
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = targetPos.relative(dir);
                FluidState neighborFluid = level.getFluidState(neighborPos);
                BlockState neighborState = level.getBlockState(neighborPos);

                boolean isNeighborWater = neighborFluid.is(Fluids.WATER) ||
                        neighborFluid.is(Fluids.FLOWING_WATER) ||
                        neighborState.is(VibraniumFluids.HOT_WATER_BLOCK.get()) ||
                        neighborFluid.getType().equals(VibraniumFluids.HOT_WATER_FLOWING.get());

                if (isNeighborWater) {
                    connectedWaterCount++;
                }
            }

            // Valid only if at least one adjacent block is also water (forming a column or basin)
            return connectedWaterCount > 0;
        }

        // Solid or invalid block
        return false;
    }
}
