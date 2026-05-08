package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public class SmallPurpleDripleaf extends SmallDripleafBlock implements SimpleWaterloggedBlock {
    //PROPERTIES
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_DRIPLEAF)
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 1);
    }

    public static final MapCodec<SmallDripleafBlock> CODEC = simpleCodec(SmallPurpleDripleaf::new);
    @Override
    public MapCodec<SmallDripleafBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public SmallPurpleDripleaf(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    //ACTIONS
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, BlockStateProperties.WATERLOGGED);
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
            return state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
        return null;
    }
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        Direction currentFacing = blockState.getValue(FACING);

        if (blockState.getValue(SmallDripleafBlock.HALF) == DoubleBlockHalf.LOWER) {
            BlockPos upperPos = blockPos.above();
            serverLevel.setBlock(upperPos, serverLevel.getFluidState(upperPos).createLegacyBlock(), 18);

            BigPurpleDripleaf.placeWithRandomHeight(serverLevel, randomSource, blockPos, currentFacing);
        } else {
            BlockPos lowerPos = blockPos.below();
            this.performBonemeal(serverLevel, randomSource, lowerPos, serverLevel.getBlockState(lowerPos));
        }
    }

}
