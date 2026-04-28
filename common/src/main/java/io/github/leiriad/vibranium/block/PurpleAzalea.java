package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import io.github.leiriad.vibranium.feature.VibraniumPurpleAzaleaFeatures;

import java.util.Optional;

public class PurpleAzalea extends AzaleaBlock {
    public static final MapCodec<AzaleaBlock> CODEC = simpleCodec(PurpleAzalea::new);
    public static final TreeGrower PURPLE_AZALEA_TREE_GROWER = new TreeGrower(
            "purple_azalea",
            Optional.empty(),
            Optional.of(VibraniumPurpleAzaleaFeatures.PURPLE_AZALEA_TREE_KEY),
            Optional.empty());;

    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 5);
    }

    public PurpleAzalea(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    public MapCodec<AzaleaBlock> codec() {
        return CODEC;
    }
    //To make your own Azalea tree
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        PURPLE_AZALEA_TREE_GROWER.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), blockPos, blockState, randomSource);
    }

}
