package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class SmallPurpleDripleaf extends SmallDripleafBlock {

    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_DRIPLEAF);
    }
    public SmallPurpleDripleaf(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
            BlockPos blockPos2 = blockPos.above();
            serverLevel.setBlock(blockPos2, serverLevel.getFluidState(blockPos2).createLegacyBlock(), 18);
            BigPurpleDripleaf.placeWithRandomHeight(serverLevel, randomSource, blockPos, blockState.getValue(FACING));
        } else {
            BlockPos blockPos2 = blockPos.below();
            this.performBonemeal(serverLevel, randomSource, blockPos2, serverLevel.getBlockState(blockPos2));
        }
    }

}
