package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.init.VibraniumBlocks;

public class VibraniumSoil extends Block {

    public VibraniumSoil(Properties properties) {

        super(properties);
    }
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    //vibranium_soil can turn into grass
        canTurnIntoGrass(world, pos, random);
    }

    private static void canTurnIntoGrass(ServerLevel world, BlockPos pos, RandomSource random) {
        // check if neighbor block is grass
        for (BlockPos targetPos : BlockPos.betweenClosed(
                pos.offset(-1, -1, -1),
                pos.offset(1, 1, 1))) {

            BlockState neighbor = world.getBlockState(targetPos);


            if (neighbor.is(Blocks.GRASS_BLOCK)) {

                // is it is, there's a little chance block turns to grass
                if (random.nextFloat() < 0.25f) {

                    world.setBlockAndUpdate(pos,
                            VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
                    return;
                }
            }

            // if neighbor block is vibranium_grass it turns into vibranium_grass more quickly
            if (neighbor.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {

                world.setBlockAndUpdate(pos,
                        VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
                return;
            }
        }
    }
}