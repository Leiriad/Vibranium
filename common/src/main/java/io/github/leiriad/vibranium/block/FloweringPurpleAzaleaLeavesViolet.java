package io.github.leiriad.vibranium.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;


public class FloweringPurpleAzaleaLeavesViolet extends LeavesBlock {

    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public static final MapCodec<FloweringPurpleAzaleaLeavesViolet> CODEC = simpleCodec(FloweringPurpleAzaleaLeavesViolet::new);

    public FloweringPurpleAzaleaLeavesViolet(Properties properties) {
        super(0.05f, properties);
    }

    @Override
    public MapCodec<? extends FloweringPurpleAzaleaLeavesViolet> codec() {
        return (MapCodec<? extends FloweringPurpleAzaleaLeavesViolet>) CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource randomSource) {

    }
}
