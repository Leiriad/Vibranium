package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlackGravel extends FallingBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL);
    }
    public BlackGravel(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return null;
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 0;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Override
    public boolean isEnabled(FeatureFlagSet featureFlagSet) {
        return super.isEnabled(featureFlagSet);
    }

    @Override
    public void onLand(Level level, BlockPos blockPos, BlockState blockState, BlockState blockState2, FallingBlockEntity fallingBlockEntity) {
        super.onLand(level, blockPos, blockState, blockState2, fallingBlockEntity);
    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos blockPos, FallingBlockEntity fallingBlockEntity) {
        super.onBrokenAfterFall(level, blockPos, fallingBlockEntity);
    }

    @Override
    public DamageSource getFallDamageSource(Entity entity) {
        return super.getFallDamageSource(entity);
    }
}
