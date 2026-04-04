package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.init.VibraniumBlocks;
import vibranium.utils.VibraniumBlockActions;

import static vibranium.block.VibraniumCommonDirtProperties.baseVibraniumDirtSettings;

public class VibraniumGrassBlock extends GrassBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return baseVibraniumDirtSettings();
    }
    public VibraniumGrassBlock(Properties properties) {
        super(properties);
    }
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        
        //Vibranium_Grass_block spreads
        spreadsToTarget(world, random, pos);

        //Vibranium_Grass_Block make plants grow faster with a risk of destruction
        VibraniumBlockActions.fertilizes(world, random, pos);
    }

    private void spreadsToTarget(ServerLevel world, RandomSource random, BlockPos pos) {

        BlockPos targetPos = pos.offset(
                random.nextInt(3) - 1,
                random.nextInt(5) - 3,
                random.nextInt(3) - 1
        );

        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.is(VibraniumBlocks.VIBRANIUM_DIRT)) {

            //Converts the Vibranium_Dirt blocks around to vibranium_grass
            world.setBlockAndUpdate(targetPos, this.defaultBlockState());

        } else if (targetState.is(Blocks.DIRT)) {

            // Turn the normal grass_block around to vibranium_grass (very rare)
            if (random.nextFloat() < 0.02f) {

                world.setBlockAndUpdate(targetPos, this.defaultBlockState());

            } else {
                // turns the normal dirt block around into normal grass
                world.setBlockAndUpdate(targetPos, Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }

    }

}