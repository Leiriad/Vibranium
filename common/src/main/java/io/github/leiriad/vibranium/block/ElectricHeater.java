package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.ElectricHeaterEntity;
import io.github.leiriad.vibranium.entity.ElectricLampEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ElectricHeater extends BaseEntityBlock {
    public static final MapCodec<ElectricHeater> CODEC = simpleCodec(ElectricHeater::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE_UP  = Block.box(0, 0, 0, 16, 1, 16);  // Floor
    private static final VoxelShape SHAPE_DOWN    = Block.box(0, 15, 0, 16, 16, 16); // Ceiling
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 15, 16, 16, 16); // North
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 1);   // South
    private static final VoxelShape SHAPE_WEST  = Block.box(15, 0, 0, 16, 16, 16); // West
    private static final VoxelShape SHAPE_EAST  = Block.box(0, 0, 0, 1, 16, 16);   // East

    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricHeaterEntity(pos, state);
    }

    @Override
    public MapCodec<ElectricHeater> codec() {
        return CODEC;
    }

    public ElectricHeater(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
        };
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }
    // Handles redstone signal changes to allow redstone powering
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ElectricHeaterEntity heaterEntity) {
                heaterEntity.checkRedstonePower(level, pos);
            }
        }
    }
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (lvl, pos, st, entity) -> {
            if (entity instanceof ElectricHeaterEntity lamp) {
                ElectricHeaterEntity.tick(lvl, pos, st, lamp);
            }
        };
    }
}
