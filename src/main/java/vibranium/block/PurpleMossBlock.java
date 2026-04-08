package vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PurpleMossBlock extends Block {
    ///Copying all vanilla moss properties
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK);
    }
    ///purple_moss constructor
    public PurpleMossBlock(Properties properties) {
        super(properties);
    }
}
