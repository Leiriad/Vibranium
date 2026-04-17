package vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

public class PurpleVine extends VineBlock {
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.of()
                .noCollision()
                .instabreak()
                .randomTicks()
                .sound(SoundType.VINE)
                .pushReaction(PushReaction.DESTROY);
    }
    public PurpleVine(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, false).setValue(NORTH, false)
                .setValue(EAST, false).setValue(SOUTH, false)
                .setValue(WEST, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Il faut impérativement ajouter toutes les faces du VineBlock
        builder.add(UP, NORTH, EAST, SOUTH, WEST);
    }
}
