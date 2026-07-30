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

        // Check if there is hot water in the reactor's hot water column to eject
        if (entity.getFluidAmount() > 0) {
            Direction facing = state.getValue(ReactorOutletBlock.FACING);
            BlockPos targetPos = pos.relative(facing);
            BlockState targetState = level.getBlockState(targetPos);
            FluidState targetFluid = level.getFluidState(targetPos);

            // CASE 1: Eject into open air -> Spawn custom hot water block
            if (targetState.isAir()) {
                BlockState hotWaterState = VibraniumFluids.HOT_WATER_BLOCK.get().defaultBlockState()
                        .setValue(HotWaterLiquidBlock.TEMPERATURE, 300);

                level.setBlockAndUpdate(targetPos, hotWaterState);
                entity.drain(100);
            }
            // CASE 2: Eject into existing vanilla water (e.g. kelp column) -> Thermal dissipation
            else if (targetFluid.is(Fluids.WATER)) {
                entity.drain(100);

                // Small burst effect to show dissipation into the ocean/column
                if (level.getGameTime() % 10 == 0 && level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP,
                            targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                            4, 0.1, 0.1, 0.1, 0.05);
                    serverLevel.playSound(null, targetPos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.4F, 1.2F);
                }
            }
            // CASE 3: Eject into already existing hot water -> Keep maximum temperature
            else if (targetState.is(VibraniumFluids.HOT_WATER_BLOCK.get())) {
                if (targetState.getValue(HotWaterLiquidBlock.TEMPERATURE) < 300) {
                    level.setBlockAndUpdate(targetPos, targetState.setValue(HotWaterLiquidBlock.TEMPERATURE, 300));
                }
                entity.drain(100);
                entity.setChanged();
            }
        }
    }
}
