package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

public class PurpleVine extends VineBlock {
    //PROPERTIES
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.of()
                .noCollision()
                .instabreak()
                .randomTicks()
                .sound(SoundType.VINE)
                .pushReaction(PushReaction.DESTROY);
    }
    public static final MapCodec<VineBlock> CODEC = simpleCodec(PurpleVine::new);
    @Override
    public MapCodec<VineBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PurpleVine(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, false).setValue(NORTH, false)
                .setValue(EAST, false).setValue(SOUTH, false)
                .setValue(WEST, false));
    }

    //ACTIONS
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        //All type of positioning should be dined
        builder.add(UP, NORTH, EAST, SOUTH, WEST);
    }
}
