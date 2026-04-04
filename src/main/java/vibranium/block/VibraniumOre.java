package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import vibranium.utils.VibraniumBlockActions;

public class VibraniumOre extends Block {

    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.BLACKSTONE)
                .strength(50f, 1200f)
                .requiresCorrectToolForDrops()
                .instrument(NoteBlockInstrument.CHIME);
    }

    public VibraniumOre(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        VibraniumBlockActions.showVibraniumParticles(level,randomSource,blockPos);
        VibraniumBlockActions.showReversePortalParticles(level, randomSource, blockPos);
    }
}
