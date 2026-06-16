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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.redstone.Orientation;

import java.util.function.Supplier;

public class HotWaterLiquidBlock extends LiquidBlock {
    public static final IntegerProperty TEMPERATURE = IntegerProperty.create("temperature", 0, 300);
    public HotWaterLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid.get(), properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0).setValue(TEMPERATURE, 300));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Enforce the inclusion of the LEVEL property so the block state definition matches vanilla liquid requirements
        super.createBlockStateDefinition(builder);
        builder.add(TEMPERATURE);
    }
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean bool) {
        //adds damage to player inside block
        int temp = state.getValue(TEMPERATURE);

        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            // The hotter the more damages
            float damage = (temp > 200) ? 2.0F : 1.0F;

            if (level.getGameTime() % 20 == 0) {
                living.hurt(level.damageSources().inFire(), damage);
            }
            // No burning if water already cold
            if (temp > 100 && !living.fireImmune()) {
                living.igniteForTicks(20);
            }
        }
        super.entityInside(state, level, pos, entity, applier, bool);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Ice melting process
        if (state.getValue(TEMPERATURE) > 50) {
            BlockPos randomNeighbor = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
            if (level.getBlockState(randomNeighbor).is(Blocks.ICE)) {
                level.setBlockAndUpdate(randomNeighbor, Blocks.WATER.defaultBlockState());
                // Water temperature goes down
                level.setBlockAndUpdate(pos, state.setValue(TEMPERATURE, Math.max(0, state.getValue(TEMPERATURE) - 20)));
                return;
            }
        }

        // Usual cool down
        int currentTemp = state.getValue(TEMPERATURE);
        if (currentTemp > 20) {
            if (random.nextInt(3) == 0) {
                level.setBlockAndUpdate(pos, state.setValue(TEMPERATURE, Math.max(20, currentTemp - 10)));
                level.levelEvent(2000, pos, 0);
            }
        } else {
            // If completely cold, turns into vanilla water
            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            return; //Block has changed do not call super
        }

        // Call super if block is still hot water
        super.randomTick(state, level, pos, random);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);

        // Check if block is still hot
        if (state.getValue(TEMPERATURE) > 50) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);

                // If neighbor is ice
                if (neighborState.is(Blocks.ICE)) {
                    level.setBlockAndUpdate(neighborPos, Blocks.WATER.defaultBlockState());

                    // Fizz sound effect
                    level.levelEvent(1501, neighborPos, 0);
                }
            }
        }
    }
}
