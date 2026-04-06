package vibranium.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import vibranium.Vibranium;
import vibranium.init.VibraniumBlocks;

import java.util.List;

public class VibraniumConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> PURPLE_AZALEA_TREE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Vibranium.MOD_ID, "purple_azalea_tree"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, PURPLE_AZALEA_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE),

                new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        )
                .dirt(BlockStateProvider.simple(Blocks.ROOTED_DIRT))
                .decorators(List.of(VibraniumGradientDecorator.INSTANCE))
                .build());
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
