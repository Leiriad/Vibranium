package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import io.github.leiriad.vibranium.utils.VibraniumBlockActions;

import static io.github.leiriad.vibranium.block.VibraniumCommonDirtProperties.baseVibraniumDirtSettings;

public class VibraniumDirt extends Block {
    //PROPERTIES
    public static final MapCodec<Block> CODEC = simpleCodec(VibraniumDirt::new);
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return baseVibraniumDirtSettings().sound(SoundType.GRAVEL);
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public VibraniumDirt(Properties properties) {
        super(properties);
    }

    //ACTIONS
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        //vibranium_dirt can turn into grass
        VibraniumBlockActions.turnsToVibraniumGrass(world, pos, random);
        //vibranium_dirt fertilizes
        VibraniumBlockActions.fertilizes(world,random,pos);
    }
}