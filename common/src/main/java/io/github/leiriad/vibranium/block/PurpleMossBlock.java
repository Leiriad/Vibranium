package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.VibraniumMod;
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
import net.minecraft.world.level.material.MapColor;

import java.util.Optional;

public class PurpleMossBlock extends Block implements BonemealableBlock {
    //PROPERTIES
    private static final ResourceKey<ConfiguredFeature <?,?>> PURPLE_MOSS_PATCHES = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "purple_moss_patch"));
    public static final MapCodec<Block> CODEC = simpleCodec(PurpleMossBlock::new);
    ///Copying all vanilla moss properties
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).mapColor(MapColor.COLOR_PURPLE);
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PurpleMossBlock(Properties properties) {
        super(properties);
    }

    //ACTIONS
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
                    registry.get(PURPLE_MOSS_PATCHES);

            // If feature exists run it above the moss block
            feature.ifPresent(f -> {
                f.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, blockPos.above());
            });
        });
    }
}




