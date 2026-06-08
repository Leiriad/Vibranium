package io.github.leiriad.vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class MilkLiquidBlock extends LiquidBlock {
    public MilkLiquidBlock(Supplier<? extends FlowingFluid> fluid, Block.Properties properties) {
        super(fluid.get(), properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Enforce the inclusion of the LEVEL property so the block state definition matches vanilla liquid requirements
        super.createBlockStateDefinition(builder);
    }
}
