package io.github.leiriad.vibranium.block;

import net.minecraft.world.level.block.Block;


public class FloweringPurpleAzaleaLeavesViolet extends Block {

    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public FloweringPurpleAzaleaLeavesViolet(Properties properties) {
        super(properties);
    }
}
