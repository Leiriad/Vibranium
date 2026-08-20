package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.ElectricWireEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class KillSwitchBlock extends LeverBlock {
    public static final MapCodec<LeverBlock> CODEC = simpleCodec(KillSwitchBlock::new);

    @Override
    public MapCodec<LeverBlock> codec() {
        return CODEC;
    }

    public KillSwitchBlock(Properties properties) {
        super(properties);
    }

    public static Properties getProperties(Properties settings) {
        return Properties.ofFullCopy(Blocks.LEVER).requiresCorrectToolForDrops();
    }

    @Override
    public void pull(BlockState blockState, Level level, BlockPos blockPos, @Nullable Player player) {
        // First, execute the base lever behavior (changes the POWERED state, plays sound, etc.)
        super.pull(blockState, level, blockPos, player);

        // Retrieve the new state after toggling
        BlockState newState = level.getBlockState(blockPos);
        boolean isPowered = newState.getValue(POWERED);

        // If the kill switch is powered (which means our kill switch is engaged / circuit cut OFF), purge downstream cables
        if (isPowered && !level.isClientSide()) {
            for (Direction direction : Direction.values()) {
                BlockPos targetPos = blockPos.relative(direction);
                BlockEntity targetEntity = level.getBlockEntity(targetPos);
                if (targetEntity instanceof ElectricWireEntity wireEntity) {
                    wireEntity.purgeEnergyNetwork();
                }
            }
        }
    }

    //Redstone signal inversion
    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) ? 0 : 15;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) && getConnectedDirection(blockState) == direction ? 0 : 15;
    }
}