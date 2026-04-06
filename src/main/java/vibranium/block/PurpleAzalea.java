package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import vibranium.utils.VibraniumConfiguredFeatures;

import java.util.Optional;

public class PurpleAzalea extends AzaleaBlock {
    public static final TreeGrower PURPLE_AZALEA_TREE_GROWER = new TreeGrower(
            "purple_azalea",
            Optional.empty(),
            Optional.of(VibraniumConfiguredFeatures.PURPLE_AZALEA_TREE_KEY),
            Optional.empty());;

    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, world, pos) -> true)
                .lightLevel((state) -> 5)
                .hasPostProcess((state, world, pos) -> true);
    }

    public PurpleAzalea(BlockBehaviour.Properties properties) {
        super(properties);
    }

    //To make your own Azalea tree
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        PURPLE_AZALEA_TREE_GROWER.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), blockPos, blockState, randomSource);
    }
}
