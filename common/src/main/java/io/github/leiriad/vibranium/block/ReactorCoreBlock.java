package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;


public class ReactorCoreBlock extends BaseEntityBlock {

    //PROPERTIES
    public static final MapCodec<ReactorCoreBlock> CODEC = simpleCodec(ReactorCoreBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .mapColor(MapColor.COLOR_CYAN)
                .instrument(NoteBlockInstrument.CHIME)
                .emissiveRendering((state, world, pos) -> true)
                .hasPostProcess((state, world, pos) -> true)
                .lightLevel((state) -> 7);
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public ReactorCoreBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    //TICKER
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ReactorCoreEntity reactor) {
                ReactorCoreEntity.tick(level1, pos, state1, reactor);
            }
        };
    }

    //METHODS
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReactorCoreEntity(blockPos, blockState);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LIT);
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
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable Orientation orientation, boolean bl) {
        super.neighborChanged(blockState, level, blockPos, block, orientation, bl);

        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(blockPos);
            if (be instanceof ReactorCoreEntity core) {
                //Makes reactor scan its neighbors
                core.scanForComponents(level, blockPos);
                core.updateFluidLevels();
            }
        }
    }
    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity == null ? false : blockEntity.triggerEvent(id, param);
    }
}
