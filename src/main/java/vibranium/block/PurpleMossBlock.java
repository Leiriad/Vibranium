package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class PurpleMossBlock extends Block implements BonemealableBlock {
    ///Copying all vanilla moss properties
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK);
    }
    ///purple_moss constructor
    public PurpleMossBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return false;
    }
    ///Calls the file configured_feature/purple_moss_patch to mimic the vanilla feature of moss_block growing moss_carpets
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        //Get Configured Feature by ID
        serverLevel.registryAccess().lookup(Registries.CONFIGURED_FEATURE).ifPresent(registry -> {
                Optional<? extends Holder.Reference<ConfiguredFeature<?, ?>>> feature =
                    registry.get(ResourceKey.create(Registries.CONFIGURED_FEATURE,
                            Identifier.fromNamespaceAndPath("vibranium", "purple_moss_patch")));

            // If feature exists run it above the moss block
            feature.ifPresent(f -> {
                f.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, blockPos.above());
            });
        });
    }
}




