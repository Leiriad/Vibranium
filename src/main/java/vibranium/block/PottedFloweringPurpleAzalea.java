package vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import vibranium.init.VibraniumBlocks;

public class PottedFloweringPurpleAzalea extends FlowerPotBlock {
    public static Properties getProperties(Properties settings){
        return  Properties.ofFullCopy(Blocks.POTTED_FLOWERING_AZALEA)
                .emissiveRendering((state, world, pos) -> true)
                .lightLevel((state) -> 2)
                .hasPostProcess((state, world, pos) -> true);
    }
    public PottedFloweringPurpleAzalea(Properties properties) {
        super(VibraniumBlocks.FLOWERING_PURPLE_AZALEA, properties);
    }



}
