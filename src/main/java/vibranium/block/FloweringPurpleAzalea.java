package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.utils.VibraniumConfiguredFeatures;

import java.util.Optional;

public class FloweringPurpleAzalea extends PurpleAzalea {
    public static final TreeGrower FLOWERING_PURPLE_AZALEA_TREE_GROWER = new TreeGrower(
            "flowering_purple_azalea",
            Optional.empty(),
            Optional.of(VibraniumConfiguredFeatures.FLOWERING_PURPLE_AZALEA_TREE_KEY),
            Optional.empty());;
    public FloweringPurpleAzalea(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        FLOWERING_PURPLE_AZALEA_TREE_GROWER.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), blockPos, blockState, randomSource);
    }
}