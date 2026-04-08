package vibranium.block;

import net.minecraft.world.level.block.Block;


public class FloweringPurpleAzaleaLeavesDarkBlue extends Block {
    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public FloweringPurpleAzaleaLeavesDarkBlue(Properties properties) {
        super(properties);
    }
}
