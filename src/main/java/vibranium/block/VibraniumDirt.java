package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.utils.VibraniumBlockActions;

public class VibraniumDirt extends Block {

    public VibraniumDirt(Properties properties) {

        super(properties);
    }
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        //vibranium_dirt can turn into grass
        VibraniumBlockActions.turnsToVibraniumGrass(world, pos, random);
        //vibranium_dirt fertilizes
        VibraniumBlockActions.fertilizes(world,random,pos);



    }


}