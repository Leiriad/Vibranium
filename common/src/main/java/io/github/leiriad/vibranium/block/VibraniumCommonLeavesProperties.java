package io.github.leiriad.vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class VibraniumCommonLeavesProperties {
    protected static BlockBehaviour.Properties baseVibraniumLeavesSettings(boolean isFlowering) {
        if(isFlowering){
            return BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWERING_AZALEA_LEAVES)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .emissiveRendering((state, world, pos) -> true)
                    .lightLevel((state) -> 5)
                    .hasPostProcess((state, world, pos) -> true);
        }
        else{
            return BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA_LEAVES)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .emissiveRendering((state, world, pos) -> true)
                    .lightLevel((state) -> 5)
                    .hasPostProcess((state, world, pos) -> true);
        }
    }
}
