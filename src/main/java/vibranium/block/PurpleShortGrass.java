package vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class PurpleShortGrass extends TallGrassBlock {
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS);
    }
    public PurpleShortGrass(Properties properties) {
        super(properties);
    }
}
