package io.github.leiriad.vibranium.utils;

import net.minecraft.world.level.block.state.BlockState;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

public class VibraniumToolActions {
    ///Vibranium path block self-managed transition to dirt/grass
    public static BlockState getPathState(BlockState state){
        if (state.is(VibraniumBlocks.VIBRANIUM_DIRT)
                || state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {
            return VibraniumBlocks.VIBRANIUM_PATH.defaultBlockState();
        }
        return null;
    }
    ///Vibranium farmland block self-managed transition to dirt/grass
    public static BlockState getFarmlandState(BlockState state) {
        if (state.is(VibraniumBlocks.VIBRANIUM_DIRT)
                || state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {
            return VibraniumBlocks.VIBRANIUM_FARMLAND.defaultBlockState();
        }
        return null;
    }

}
