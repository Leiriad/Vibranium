package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.init.VibraniumBlocks;

public class VibraniumGrassBlock extends GrassBlock {

    public VibraniumGrassBlock(Properties properties) {

        super(properties);
    }
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);

        BlockPos above = pos.above();
        BlockState plant = world.getBlockState(above);
        BlockPos targetPos = pos.offset(
                random.nextInt(3) - 1,
                random.nextInt(5) - 3,
                random.nextInt(3) - 1
        );
       
        BlockState targetState = world.getBlockState(targetPos);
        
        //Vibranium_Grass_block spreads
        spreadsToTarget(world, random, targetState, targetPos);

        //Vibranium_Grass_Block make plants grow faster with a risk of destruction
        extracted(world, random, plant, above);
    }

    private static void extracted(ServerLevel world, RandomSource random, BlockState plant, BlockPos above) {
        // If plant on block
        if (plant.getBlock() instanceof BonemealableBlock growable) {

            // Random bonus chance behaviour either light and constant or big
            if (random.nextFloat() < 0.10f) {
                if (growable.isValidBonemealTarget(world, above, plant)) {
                    growable.performBonemeal(world, random, above, plant);
                }
            }

            if (random.nextFloat() < 0.45f) {
                if (growable.isValidBonemealTarget(world, above, plant)) {
                    growable.performBonemeal(world, random, above, plant);
                }
            }

            //Or grows too quickly and dies
            if (random.nextFloat() < 0.05f) {
                world.destroyBlock(above, true);
            }
        }
    }

    private void spreadsToTarget(ServerLevel world, RandomSource random, BlockState targetState, BlockPos targetPos) {
        if (targetState.is(VibraniumBlocks.VIBRANIUM_SOIL)) {

            //Converts the Vibranium_soil blocks around to vibranium_grass
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