package vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PurpleVine extends VineBlock {
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.VINE);
    }
    public PurpleVine(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
