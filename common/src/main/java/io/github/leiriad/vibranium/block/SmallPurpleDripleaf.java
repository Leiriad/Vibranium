package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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

public class SmallPurpleDripleaf extends SmallDripleafBlock {
    //PROPERTIES
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_DRIPLEAF);
    }

    public static final MapCodec<SmallDripleafBlock> CODEC = simpleCodec(SmallPurpleDripleaf::new);
    @Override
    public MapCodec<SmallDripleafBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public SmallPurpleDripleaf(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
                        .setValue(FACING, Direction.NORTH)
        );
    }

    //ACTIONS
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        Direction currentFacing = blockState.getValue(FACING);

        BlockPos basePos = blockState.getValue(SmallDripleafBlock.HALF) == DoubleBlockHalf.LOWER ? blockPos : blockPos.below();
        BlockPos topPos = basePos.above();

        serverLevel.removeBlock(topPos, false);
        serverLevel.removeBlock(basePos, false);

        int height = randomSource.nextInt(3) + 2;

        BigPurpleDripleaf.placeWithSpecificHeight(serverLevel, basePos, currentFacing, height);
    }



}
