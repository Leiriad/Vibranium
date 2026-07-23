package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;


public class VibraniumGlass extends Block {

    //PROPERTIES
    public static final MapCodec<Block> CODEC = simpleCodec(VibraniumGlass::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.GLASS).mapColor(MapColor.COLOR_PINK);
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public VibraniumGlass(Properties properties) {
        super(properties);
    }

    //METHODS
    /// Skip rendering adjacent faces if the neighboring block is also VibraniumGlass
    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        if (adjacentState.is(this)) {
            return true;
        }
        return super.skipRendering(state, adjacentState, direction);
    }
}
