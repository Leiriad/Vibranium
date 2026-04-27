package io.github.leiriad.vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

public class PottedPurpleAzalea extends FlowerPotBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return  BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_AZALEA)
                .emissiveRendering((state, level, pos) -> true)
                .hasPostProcess((state, level, pos) -> true)
                .lightLevel((state) -> 2);
    }
    public PottedPurpleAzalea(Properties properties) {
        super(VibraniumBlocks.PURPLE_AZALEA, properties);
    }



}
