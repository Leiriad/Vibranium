package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import io.github.leiriad.vibranium.feature.VibraniumPurpleAzaleaFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.MapColor;

import java.util.Optional;

public class FloweringPurpleAzalea extends PurpleAzalea {
    //PROPERTIES
    private static String BLOCKNAME = "flowering_purple_azalea";
    private static final ResourceKey<ConfiguredFeature<?, ?>> TREE_KEY = VibraniumPurpleAzaleaFeatures.FLOWERING_PURPLE_AZALEA_TREE_KEY;
    public static final MapCodec<AzaleaBlock> CODEC = simpleCodec(FloweringPurpleAzalea::new);
    private static final TreeGrower TREE_GROWER = new TreeGrower(
            BLOCKNAME,
            Optional.empty(),
            Optional.of(TREE_KEY),
            Optional.empty());
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 5);
    }
    @Override
    public MapCodec<AzaleaBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public FloweringPurpleAzalea(BlockBehaviour.Properties properties) {
        super(properties);
    }

    //ACTIONS
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        TREE_GROWER.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), blockPos, blockState, randomSource);
    }

}