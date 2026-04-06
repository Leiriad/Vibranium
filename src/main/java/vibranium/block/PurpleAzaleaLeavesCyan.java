package vibranium.block;

import net.minecraft.world.level.block.Block;


public class PurpleAzaleaLeavesCyan extends Block {
    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public PurpleAzaleaLeavesCyan(Properties properties) {
        super(properties);
    }
}
