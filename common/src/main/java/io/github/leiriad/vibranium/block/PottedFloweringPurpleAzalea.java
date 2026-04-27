package io.github.leiriad.vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

public class PottedFloweringPurpleAzalea extends FlowerPotBlock {
    public static Properties getProperties(Properties settings){
        return  Properties.ofFullCopy(Blocks.POTTED_FLOWERING_AZALEA)
                .emissiveRendering((state, level, pos) -> true)
                .hasPostProcess((state, level, pos) -> true)
                .lightLevel((state) -> 2);
    }
    public PottedFloweringPurpleAzalea(Properties properties) {
        super(VibraniumBlocks.FLOWERING_PURPLE_AZALEA, properties);
    }



}
