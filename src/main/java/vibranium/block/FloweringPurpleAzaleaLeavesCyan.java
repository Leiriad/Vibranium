package vibranium.block;

import net.minecraft.world.level.block.Block;


public class FloweringPurpleAzaleaLeavesCyan extends Block {
    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public FloweringPurpleAzaleaLeavesCyan(Properties properties) {
        super(properties);
    }
}
