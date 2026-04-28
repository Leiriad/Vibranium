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
import io.github.leiriad.vibranium.feature.VibraniumPurpleAzaleaFeatures;
import net.minecraft.world.level.material.MapColor;

import java.util.Optional;

public class FloweringPurpleAzalea extends PurpleAzalea {
    public static final MapCodec<AzaleaBlock> CODEC = simpleCodec(FloweringPurpleAzalea::new);
    public static final TreeGrower FLOWERING_PURPLE_AZALEA_TREE_GROWER = new TreeGrower(
            "flowering_purple_azalea",
            Optional.empty(),
            Optional.of(VibraniumPurpleAzaleaFeatures.FLOWERING_PURPLE_AZALEA_TREE_KEY),
            Optional.empty());
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 5);
    }

    public FloweringPurpleAzalea(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    public MapCodec<AzaleaBlock> codec() {
        return CODEC;
    }
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        FLOWERING_PURPLE_AZALEA_TREE_GROWER.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), blockPos, blockState, randomSource);
    }

}