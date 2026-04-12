package vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vibranium.init.VibraniumBlocks;

public class PottedPurpleAzalea extends FlowerPotBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return  BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_AZALEA);
    }
    public PottedPurpleAzalea(Properties properties) {
        super(VibraniumBlocks.PURPLE_AZALEA, properties);
    }



}
