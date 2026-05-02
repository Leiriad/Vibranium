package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

import static io.github.leiriad.vibranium.init.VibraniumBlocks.PURPLE_TALL_GRASS;
import static io.github.leiriad.vibranium.init.VibraniumBlocks.VIBRANIUM_GRASS_BLOCK;


public class PurpleShortGrass extends BushBlock implements BonemealableBlock{
    //PROPERTIES
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 1);
    }
    public static final MapCodec<BushBlock> CODEC = simpleCodec(PurpleShortGrass::new);
    @Override
    public MapCodec<BushBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PurpleShortGrass(Properties properties) {
        super(properties.randomTicks());
    }

    //ACTIONS
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(level.getBlockState(pos.below()).is(VIBRANIUM_GRASS_BLOCK.get())){
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

        return groundState.is(VIBRANIUM_GRASS_BLOCK.get());
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
        level.setBlock(pos, PURPLE_TALL_GRASS.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), PURPLE_TALL_GRASS.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }

}
