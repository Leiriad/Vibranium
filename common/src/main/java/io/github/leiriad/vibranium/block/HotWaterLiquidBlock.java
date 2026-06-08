package io.github.leiriad.vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.redstone.Orientation;

import java.util.function.Supplier;

public class HotWaterLiquidBlock extends LiquidBlock {
    public HotWaterLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid.get(), properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Enforce the inclusion of the LEVEL property so the block state definition matches vanilla liquid requirements
        super.createBlockStateDefinition(builder);
    }
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean bool) {
        super.entityInside(state, level, pos, entity, applier, bool);
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            // Apply damages to entity
            if (level.getGameTime() % 10 == 0) {
                living.hurt(level.damageSources().hotFloor(), 1.0F);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 1 on 10 chances to get cold per tick
        if (random.nextInt(10) == 0) {
            // Check is upper block is air
            if (level.getBlockState(pos.above()).isAir()) {
                level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());

                // Or play a fizz sound
                level.levelEvent(1501, pos, 0);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving) {
        // Check if neighbor is ice
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(Blocks.ICE)) {
                level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                return;
            }
        }
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
    }
}
