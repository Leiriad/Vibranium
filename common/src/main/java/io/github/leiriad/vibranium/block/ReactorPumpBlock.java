package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.ReactorPumpEntity;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public class ReactorPumpBlock extends BaseEntityBlock {
    //PROPERTIES
    public static final MapCodec<ReactorPumpBlock> CODEC = simpleCodec(ReactorPumpBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public static Properties getProperties (Properties settings){
        return Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .instrument(NoteBlockInstrument.CHIME).requiresCorrectToolForDrops();
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    //CONSTRUCTOR
    public ReactorPumpBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
        );
    }

    //METHODS
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReactorPumpEntity(blockPos, blockState);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, VibraniumEntities.REACTOR_PUMP_ENTITY.get(), ReactorPumpEntity::tick);
    }
}
