package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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
                        .setValue(COLOR, DyeColor.WHITE)
        );
    }

    @Override
    public Direction getAttachedFace(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN,COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

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

        for (Direction dir : Direction.values()) {
            var prop = PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null) {
                state = state.setValue(prop, shouldConnectTo(level, pos, dir, attachedFace));
            }
        }

        return state;
    }
}