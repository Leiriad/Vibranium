package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import io.github.leiriad.vibranium.utils.VibraniumBlockActions;

public class VibraniumOre extends Block {
    //PROPERTIES
    public static final MapCodec<Block> CODEC = simpleCodec(VibraniumOre::new);
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.BLACKSTONE)
                .strength(50f, 1200f)
                .requiresCorrectToolForDrops()
                .instrument(NoteBlockInstrument.CHIME)
                .emissiveRendering((state, world, pos) -> true)
                .hasPostProcess((state, world, pos) -> true);
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(VibraniumOre::new);
    }

    //CONSTRUCTOR
    public VibraniumOre(Properties properties) {
        super(properties);
    }

    //ACTIONS
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource source) {
        VibraniumBlockActions.showVibraniumParticles(world,source,pos);
        VibraniumBlockActions.showReversePortalParticles(world, source, pos);
    }
}
