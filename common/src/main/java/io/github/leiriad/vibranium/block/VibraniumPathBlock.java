package io.github.leiriad.vibranium.block;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import static io.github.leiriad.vibranium.block.VibraniumCommonDirtProperties.baseVibraniumDirtSettings;
import static io.github.leiriad.vibranium.init.VibraniumBlocks.VIBRANIUM_DIRT;

public class VibraniumPathBlock extends DirtPathBlock {
    //PROPERTIES
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return baseVibraniumDirtSettings().sound(SoundType.GRAVEL).mapColor(MapColor.TERRACOTTA_PINK);
    }

    //CONSTRUCTOR
    public VibraniumPathBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, world, pos)) {
            world.setBlockAndUpdate(pos, VIBRANIUM_DIRT.get().defaultBlockState());
        }
    }

    //ACTIONS
    ///Turns back to vibranium_dirt if a block is on top of it
    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos blockPos2, BlockState blockState2, RandomSource randomSource) {

        if (direction == Direction.UP && !state.canSurvive(world, pos)) {
            return VIBRANIUM_DIRT.get().defaultBlockState();
        }
        return super.updateShape(state, world, scheduledTickAccess, pos, direction, blockPos2, blockState2, randomSource);
    }
}