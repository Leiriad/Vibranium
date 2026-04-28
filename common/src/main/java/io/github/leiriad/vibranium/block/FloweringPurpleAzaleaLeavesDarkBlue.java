package io.github.leiriad.vibranium.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;


public class FloweringPurpleAzaleaLeavesDarkBlue extends LeavesBlock{
    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public static final MapCodec<FloweringPurpleAzaleaLeavesDarkBlue> CODEC = simpleCodec(FloweringPurpleAzaleaLeavesDarkBlue::new);

    public FloweringPurpleAzaleaLeavesDarkBlue(Properties properties) {
        super(0.05f, properties);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(net.minecraft.world.level.block.state.BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource randomSource) {

    }

}
