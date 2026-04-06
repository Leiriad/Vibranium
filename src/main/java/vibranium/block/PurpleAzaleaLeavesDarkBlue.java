package vibranium.block;

import net.minecraft.world.level.block.Block;


public class PurpleAzaleaLeavesDarkBlue extends Block {
    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public PurpleAzaleaLeavesDarkBlue(Properties properties) {
        super(properties);
    }
}
