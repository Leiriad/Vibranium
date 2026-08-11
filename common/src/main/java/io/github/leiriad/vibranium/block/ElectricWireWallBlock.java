package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ElectricWireWallBlock extends BaseElectricWireBlock {
    public static final MapCodec<ElectricWireWallBlock> CODEC = simpleCodec(ElectricWireWallBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings) {
        return Properties.ofFullCopy(Blocks.TRIPWIRE);
    }

    public ElectricWireWallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
                        .setValue(CORNER_TYPE, CornerType.NONE)
                        .setValue(COLOR, DyeColor.WHITE)
        );
    }

    @Override
    public Direction getAttachedFace(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, CORNER_TYPE, COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isVertical()) {
            return null;
        }

        Direction attachedFace = clickedFace.getOpposite();
        BlockState state = this.defaultBlockState().setValue(FACING, attachedFace);

        if (!state.canSurvive(level, pos)) {
            return null;
        }

        for (Direction dir : Direction.values()) {
            var prop = PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null) {
                state = state.setValue(prop, this.shouldConnectTo(level, pos, dir, attachedFace));
            }
        }

        return state;
    }
}