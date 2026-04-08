package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.utils.VibraniumConfiguredFeatures;

import java.util.Optional;


public class FloweringPurpleAzaleaLeavesViolet extends Block {

    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public FloweringPurpleAzaleaLeavesViolet(Properties properties) {
        super(properties);
    }
}
