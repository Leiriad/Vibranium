package io.github.leiriad.vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class VibraniumCommonLeavesProperties {

    public static BlockBehaviour.Properties baseVibraniumLeavesSettings(boolean isFlowering) {

        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(
                isFlowering ? Blocks.FLOWERING_AZALEA_LEAVES : Blocks.AZALEA_LEAVES
        );
        return props
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 5);
    }

}