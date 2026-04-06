package vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class PurpleAzaleaLeavesViolet extends Block {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public PurpleAzaleaLeavesViolet(Properties properties) {
        super(properties);
    }
}
