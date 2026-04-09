package vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vibranium.init.VibraniumBlocks;

public class PurpleCaveVines extends CaveVinesBlock {
    ///Copies vanilla Cave Vines properties
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return  BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES);
    }

    ///PurpleCaveVines contructor
    public PurpleCaveVines(Properties properties) {
        super(properties);
    }

    ///Tells the vine which body to use for its growth
    @Override
    protected Block getBodyBlock() {
        return VibraniumBlocks.PURPLE_CAVE_VINES_PLANT;
    }
}
