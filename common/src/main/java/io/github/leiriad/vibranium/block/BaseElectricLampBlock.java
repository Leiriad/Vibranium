package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.ElectricLampEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class BaseElectricLampBlock extends BaseEntityBlock {
    public static final MapCodec<BaseElectricLampBlock> CODEC = simpleCodec(BaseElectricLampBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    @Override
    public MapCodec<BaseElectricLampBlock> codec() {
        return CODEC;
    }

    public BaseElectricLampBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricLampEntity(pos, state);
    }

    // Increases light emission when lit (Level 15)
    @Override
    protected int getLightBlock(BlockState blockState) {
        if (blockState.isSolidRender()) {
            return 15;
        } else {
            return blockState.propagatesSkylightDown() ? 0 : 1;
        }
    }

    // Handles redstone signal changes to allow redstone powering
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ElectricLampEntity lampEntity) {
                lampEntity.checkRedstonePower(level, pos);
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (lvl, pos, st, entity) -> {
            if (entity instanceof ElectricLampEntity lamp) {
                ElectricLampEntity.tick(lvl, pos, st, lamp);
            }
        };
    }
}