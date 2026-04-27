package io.github.leiriad.vibranium.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class PurpleAzaleaLeavesViolet extends LeavesBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public static final MapCodec<PurpleAzaleaLeavesViolet> CODEC = simpleCodec(PurpleAzaleaLeavesViolet::new);
    public PurpleAzaleaLeavesViolet(Properties properties) {
        super(0.05f, properties);
    }

    @Override
    public MapCodec<? extends PurpleAzaleaLeavesViolet> codec() {
        return CODEC;
    }
    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource randomSource) {

    }

}
