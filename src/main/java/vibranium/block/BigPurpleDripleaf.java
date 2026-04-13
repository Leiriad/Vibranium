package vibranium.block;


import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BigPurpleDripleaf extends BigDripleafBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.BIG_DRIPLEAF);
    }
    public BigPurpleDripleaf(Properties properties) {
        super(properties);
    }
}
