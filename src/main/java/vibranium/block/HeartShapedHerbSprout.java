package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import vibranium.init.VibraniumBlocks;

import javax.swing.text.html.BlockView;

public class HeartShapedHerbSprout extends BushBlock implements BonemealableBlock {
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.RED_TULIP);
    }
    public HeartShapedHerbSprout(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(level.getBlockState(pos.below()).is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)){
            if (random.nextInt(20) == 0) {
                if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && level.isEmptyBlock(pos.above())) {
                    this.performBonemeal(level, random, pos, state);
                }
            }
        }
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos groundPos = pos.below();
        BlockState groundState = level.getBlockState(groundPos);

        return groundState.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return super.isValidBonemealTarget(levelReader, pos, state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return super.isBonemealSuccess(level, randomSource, blockPos, blockState);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, VibraniumBlocks.PURPLE_TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), VibraniumBlocks.PURPLE_TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }
}
