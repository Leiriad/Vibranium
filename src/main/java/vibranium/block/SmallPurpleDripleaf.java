package vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SmallPurpleDripleaf extends SmallDripleafBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_DRIPLEAF);
    }
    public SmallPurpleDripleaf(Properties properties) {
        super(properties);
    }
}
