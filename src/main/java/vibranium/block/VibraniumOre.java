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
                .instrument(NoteBlockInstrument.CHIME)
                .emissiveRendering((state, world, pos) -> true)
                .hasPostProcess((state, world, pos) -> true);
    }

    public VibraniumOre(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource source) {
        VibraniumBlockActions.showVibraniumParticles(world,source,pos);
        VibraniumBlockActions.showReversePortalParticles(world, source, pos);
    }
}
