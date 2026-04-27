package io.github.leiriad.vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BigDripleafStemBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

public class BigPurpleDripleafStem extends BigDripleafStemBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.BIG_DRIPLEAF_STEM)
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> true)
                .hasPostProcess((state, level, pos) -> true)
                .lightLevel((state) -> 1);
    }
    public BigPurpleDripleafStem(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    protected static boolean place(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState, Direction direction) {
        BlockState blockState = VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM
                .defaultBlockState()
                .setValue(WATERLOGGED, fluidState.isSourceOfType(Fluids.WATER))
                .setValue(FACING, direction);
        return levelAccessor.setBlock(blockPos, blockState, 3);
    }
    @Override
    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        BlockState blockState3 = levelReader.getBlockState(blockPos.above());
        return (blockState2.is(this) || blockState2.is(BlockTags.BIG_DRIPLEAF_PLACEABLE)) && (blockState3.is(this) || blockState3.is(VibraniumBlocks.BIG_PURPLE_DRIPLEAF));
    }

}
