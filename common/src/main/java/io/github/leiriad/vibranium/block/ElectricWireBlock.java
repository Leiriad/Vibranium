package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ElectricWireBlock extends BaseElectricWireBlock {
    public static final MapCodec<ElectricWireBlock> CODEC = simpleCodec(ElectricWireBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings) {
        return Properties.ofFullCopy(Blocks.TRIPWIRE);
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTORS
    public ElectricWireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.DOWN)
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
                        .setValue(VERTICAL_ATTACHMENT, Direction.NORTH)
                        .setValue(COLOR, DyeColor.WHITE)
        );
    }


    //METHODS
    @Override
    public Direction getAttachedFace(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, VERTICAL_ATTACHMENT, COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        // If the player clicks on top of a ground block, clicked face is UP, so attachment face is DOWN
        Direction attachedFace = context.getClickedFace().getOpposite();
        BlockState state = this.defaultBlockState().setValue(FACING, attachedFace);

        if (!state.canSurvive(level, pos)) {
            for (Direction face : Direction.values()) {
                state = state.setValue(FACING, face);
                if (state.canSurvive(level, pos)) {
                    attachedFace = face;
                    break;
                }
            }
            if (!state.canSurvive(level, pos)) {
                return null;
            }
        }

        // Simple horizontal connections (same plane only)
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            var prop = PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null) {
                BlockState neighborState = level.getBlockState(pos.relative(dir));
                boolean canConnect = neighborState.getBlock() instanceof BaseElectricWireBlock neighborWire
                        && neighborWire.getAttachedFace(neighborState) == attachedFace;
                state = state.setValue(prop, canConnect);
            }
        }

        // Apply vertical bend attachment
        return applyVerticalAttachment(level, pos, state);
    }

    public BlockState applyVerticalAttachment(LevelReader level, BlockPos pos, BlockState state) {
        Direction attachedFace = getAttachedFace(state);

        // Ceiling -> looks down
        if (attachedFace == Direction.UP) {
            BlockState belowState = level.getBlockState(pos.below());
            if (belowState.getBlock() instanceof BaseElectricWireBlock belowWire) {
                Direction wallFace = belowWire.getAttachedFace(belowState);
                if (wallFace.getAxis().isHorizontal()) {
                    return state.setValue(DOWN, true).setValue(VERTICAL_ATTACHMENT, wallFace.getOpposite());
                }
            }
            return state.setValue(DOWN, false);
        }

        // Ground -> looks up
        if (attachedFace == Direction.DOWN) {
            BlockState aboveState = level.getBlockState(pos.above());
            if (aboveState.getBlock() instanceof BaseElectricWireBlock aboveWire) {
                Direction wallFace = aboveWire.getAttachedFace(aboveState);
                if (wallFace.getAxis().isHorizontal()) {
                    return state.setValue(UP, true).setValue(VERTICAL_ATTACHMENT, wallFace.getOpposite());
                }
            }
            return state.setValue(UP, false);
        }

        return state;
    }
}