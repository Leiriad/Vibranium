package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BigDripleafStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

import static io.github.leiriad.vibranium.init.VibraniumBlocks.BIG_PURPLE_DRIPLEAF;
import static io.github.leiriad.vibranium.init.VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM;

public class BigPurpleDripleafStem extends BigDripleafStemBlock{
    //PROPERTIES
    public static final MapCodec<BigDripleafStemBlock> CODEC = simpleCodec(BigPurpleDripleafStem::new);
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.BIG_DRIPLEAF_STEM);
    }
    @Override
    public MapCodec<BigDripleafStemBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public BigPurpleDripleafStem(Properties properties) {
        super(properties);
    }

    //ACTIONS
    protected static boolean place(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState, Direction direction) {
        BlockState blockState = BIG_PURPLE_DRIPLEAF_STEM.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidState.isSourceOfType(Fluids.WATER)).setValue(FACING, direction);
        return levelAccessor.setBlock(blockPos, blockState, 3);
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override
    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        BlockState blockState3 = levelReader.getBlockState(blockPos.above());
        return (blockState2.is(this) || blockState2.is(BlockTags.BIG_DRIPLEAF_PLACEABLE)) && (blockState3.is(this) || blockState3.is(BIG_PURPLE_DRIPLEAF.get()));
    }

}
