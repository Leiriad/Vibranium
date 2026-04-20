package io.github.leiriad.vibranium.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

public class BigPurpleDripleaf extends BigDripleafBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.BIG_DRIPLEAF)
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, world, pos) -> true)
                .lightLevel((state) -> 1);
    }
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public BigPurpleDripleaf(Properties properties) {
        super(properties);
    }


    public static void placeWithRandomHeight(LevelAccessor levelAccessor, RandomSource randomSource, BlockPos blockPos, Direction direction) {
        int i = Mth.nextInt(randomSource, 2, 5);
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        int j = 0;

        while (j < i && canPlaceAt(levelAccessor, mutableBlockPos, levelAccessor.getBlockState(mutableBlockPos))) {
            j++;
            mutableBlockPos.move(Direction.UP);
        }

        int k = blockPos.getY() + j - 1;
        mutableBlockPos.setY(blockPos.getY());

        while (mutableBlockPos.getY() < k) {
            BigPurpleDripleafStem.place(levelAccessor, mutableBlockPos, levelAccessor.getFluidState(mutableBlockPos), direction);
            mutableBlockPos.move(Direction.UP);
        }

        place(levelAccessor, mutableBlockPos, levelAccessor.getFluidState(mutableBlockPos), direction);
    }
    protected static boolean canPlaceAt(LevelHeightAccessor levelHeightAccessor, BlockPos blockPos, BlockState blockState) {
        return !levelHeightAccessor.isOutsideBuildHeight(blockPos) && canReplace(blockState);
    }
    private static boolean canReplace(BlockState blockState) {
        return blockState.isAir() || blockState.is(Blocks.WATER) || blockState.is(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF);
    }

    protected static boolean place(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState, Direction direction) {
        BlockState blockState = VibraniumBlocks.BIG_PURPLE_DRIPLEAF.defaultBlockState().setValue(WATERLOGGED, fluidState.isSourceOfType(Fluids.WATER)).setValue(FACING, direction);
        return levelAccessor.setBlock(blockPos, blockState, 3);
    }
    @Override
    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        return blockState2.is(this) || blockState2.is(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM) || blockState2.is(BlockTags.BIG_DRIPLEAF_PLACEABLE);
    }
    @Override
    protected BlockState updateShape(
            BlockState blockState,
            LevelReader levelReader,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos blockPos,
            Direction direction,
            BlockPos blockPos2,
            BlockState blockState2,
            RandomSource randomSource
    ) {
        if (direction == Direction.DOWN && !blockState.canSurvive(levelReader, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if ((Boolean)blockState.getValue(WATERLOGGED)) {
                scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
            }

            if (direction == Direction.UP && blockState2.is(this)) {
                // We manually copy properties to the Stem block to keep the plant's orientation
                return VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.defaultBlockState()
                        .setValue(FACING, blockState.getValue(FACING))
                        .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED));
            }

            return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
        }
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().below());
        FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
        boolean bl = blockState.is(VibraniumBlocks.BIG_PURPLE_DRIPLEAF) || blockState.is(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM);
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.isSourceOfType(Fluids.WATER))
                .setValue(FACING, bl ? (Direction)blockState.getValue(FACING) : blockPlaceContext.getHorizontalDirection().getOpposite());
    }
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        BlockPos blockPos2 = blockPos.above();
        BlockState blockState2 = serverLevel.getBlockState(blockPos2);
        if (canPlaceAt(serverLevel, blockPos2, blockState2)) {
            Direction direction = blockState.getValue(FACING);
            BigPurpleDripleafStem.place(serverLevel, blockPos, blockState.getFluidState(), direction);
            place(serverLevel, blockPos2, blockState2.getFluidState(), direction);
        }
    }
}
