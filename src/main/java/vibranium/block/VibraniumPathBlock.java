package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.init.VibraniumBlocks;

public class VibraniumPathBlock extends DirtPathBlock {

    public VibraniumPathBlock(Properties properties) {

        super(properties);
    }
    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, world, pos)) {
            world.setBlockAndUpdate(pos, VibraniumBlocks.VIBRANIUM_DIRT.defaultBlockState());
        }
    }
}