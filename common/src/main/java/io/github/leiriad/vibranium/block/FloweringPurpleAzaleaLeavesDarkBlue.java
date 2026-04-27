package io.github.leiriad.vibranium.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;


public class FloweringPurpleAzaleaLeavesDarkBlue extends LeavesBlock{
    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public static final MapCodec<FloweringPurpleAzaleaLeavesDarkBlue> CODEC = simpleCodec(FloweringPurpleAzaleaLeavesDarkBlue::new);

    public FloweringPurpleAzaleaLeavesDarkBlue(Properties properties) {
        super(0.05f, properties);
    }

    @Override
    public MapCodec<? extends FloweringPurpleAzaleaLeavesDarkBlue> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource randomSource) {

    }
}
