package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

}