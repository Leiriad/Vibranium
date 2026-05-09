package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import io.github.leiriad.vibranium.utils.VibraniumBlockActions;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;

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

    ///Hack to force meteorites animation
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);

                if (neighborState.is(Blocks.WATER)) {
                    level.neighborChanged(neighborPos, state.getBlock(), (Orientation) null);

                    level.scheduleTick(neighborPos, Fluids.WATER, 0);
                }
            }
        }
    }
}
