package io.github.leiriad.vibranium.block;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;


public class PurpleAzaleaLeavesCyan extends LeavesBlock {
    //PROPERTIES
    public static Properties getProperties(Properties settings){
        return VibraniumCommonLeavesProperties.baseVibraniumLeavesSettings(false);
    }
    public static final MapCodec<? extends LeavesBlock> CODEC = simpleCodec(PurpleAzaleaLeavesCyan::new);

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PurpleAzaleaLeavesCyan(Properties properties) {
        super(0.05f, properties);
    }

    //ACTIONS
    @Override
    public void animateTick(net.minecraft.world.level.block.state.BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource randomSource) {

    }

}
