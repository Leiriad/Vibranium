package io.github.leiriad.vibranium.block;

import io.github.leiriad.vibranium.entity.ElectricWireEntity;
import io.github.leiriad.vibranium.init.EnergyApiHelper;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public abstract class BaseElectricWireBlock extends BaseEntityBlock {

    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;


    public enum CornerType implements net.minecraft.util.StringRepresentable {
        NONE("none"),
        FLOOR("floor"),
        CEILING("ceiling");

        private final String name;
        CornerType(String name) { this.name = name; }
        @Override public String getSerializedName() { return this.name; }
    }

    public static final EnumProperty<CornerType> CORNER_TYPE = EnumProperty.create("corner_type", CornerType.class);
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    protected static final double THICKNESS = 1.0;
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    //CONSTRUCTOR
    public BaseElectricWireBlock(Properties properties) {
        super(properties);
    }

    //ENTITY
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricWireEntity(pos, state);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(blockEntityType, VibraniumEntities.ELECTRIC_WIRE_ENTITY.get(), ElectricWireEntity::tick);
    }

    //SHAPE
    public abstract Direction getAttachedFace(BlockState state);

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction attachedFace = getAttachedFace(state);
        BlockPos supportingPos = pos.relative(attachedFace);
        BlockState supportingState = level.getBlockState(supportingPos);
        return supportingState.isFaceSturdy(level, supportingPos, attachedFace.getOpposite());
    }

    protected boolean shouldConnectTo(LevelReader level, BlockPos pos, Direction connectionDir, Direction attachedFace) {
        if (connectionDir == attachedFace) return false;

        // Detection in the adjacent block
        BlockPos directPos = pos.relative(connectionDir);
        BlockState directState = level.getBlockState(directPos);

        if (directState.getBlock() instanceof BaseElectricWireBlock targetWire) {
            Direction targetAttached = targetWire.getAttachedFace(directState);

            // Alignment on the same face (e.g., 2 wires on the floor side by side)
            if (targetAttached == attachedFace) {
                return true;
            }

            // Inner Corner 1: Horizontal wire (floor/ceiling) to a wall wire in the neighboring block
            if (attachedFace.getAxis().isVertical() && targetAttached.getAxis().isHorizontal()) {
                // Support verification for the horizontal part of the corner:
                // The support is under the horizontal part (down if the wire is on the floor, up if the wire is on the ceiling)
                BlockPos supportPos = directPos.relative(attachedFace);
                BlockState supportState = level.getBlockState(supportPos);

                // The block must be solid / full (no air, fluid, etc.)
                if (supportState.isRedstoneConductor(level, supportPos)) {
                    return true;
                }
            }

            // Inner Corner 2: Wall wire to horizontal wire (floor/ceiling)
            if (attachedFace.getAxis().isHorizontal() && targetAttached.getAxis().isVertical()) {
                if ((connectionDir == Direction.DOWN && targetAttached == Direction.DOWN) ||
                        (connectionDir == Direction.UP && targetAttached == Direction.UP)) {
                    // For this direction, we also verify that the block receiving the horizontal bar has a solid support behind it
                    BlockPos supportPos = directPos.relative(targetAttached);
                    BlockState supportState = level.getBlockState(supportPos);

                    if (supportState.isRedstoneConductor(level, supportPos)) {
                        return true;
                    }
                }
            }
        }

        // Detection in same bloc - Outer corner (Two wires on the external faces of the same support block)
        BlockPos diagonalPos = pos.relative(attachedFace).relative(connectionDir);
        BlockState diagonalState = level.getBlockState(diagonalPos);

        if (diagonalState.getBlock() instanceof BaseElectricWireBlock diagonalWire) {
            if (diagonalWire.getAttachedFace(diagonalState) == connectionDir.getOpposite()) {
                return true;
            }
        }

        //Detection of other electrical entities
        BlockPos targetPos = pos.relative(connectionDir);
        if (level instanceof Level l) {
            if (EnergyApiHelper.isEnergyMachine(l, targetPos, connectionDir)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        DyeColor currentColor = state.getValue(COLOR);
        Direction attachedFace = getAttachedFace(state);

        // Update connection booleans
        for (Direction dir : Direction.values()) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null) {
                state = state.setValue(prop, shouldConnectTo(level, pos, dir, attachedFace));
            }
        }

        // Inner corner management on the wall wire
        if (attachedFace.getAxis().isHorizontal() && state.hasProperty(CORNER_TYPE)) {
            CornerType cornerType = CornerType.NONE;

            BlockPos frontPos = pos.relative(attachedFace.getOpposite());
            BlockState frontState = level.getBlockState(frontPos);

            if (frontState.getBlock() instanceof BaseElectricWireBlock frontWire) {
                Direction frontAttached = frontWire.getAttachedFace(frontState);

                if (frontAttached == Direction.DOWN) {
                    cornerType = CornerType.FLOOR;
                    // FORCE EXTENSION DOWNWARDS TO COMPLETE THE VERTICAL STEM OF THE L SHAPE
                    state = state.setValue(DOWN, true);
                } else if (frontAttached == Direction.UP) {
                    cornerType = CornerType.CEILING;
                    // FORCE EXTENSION UPWARDS TO COMPLETE THE VERTICAL STEM OF THE L SHAPE
                    state = state.setValue(UP, true);
                }
            }

            state = state.setValue(CORNER_TYPE, cornerType);
        }

        return state.setValue(COLOR, currentColor);
    }


    // HITBOXES
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction attachedFace = getAttachedFace(state);
        VoxelShape shape = getCoreShape(attachedFace);

        for (Direction connectionDir : Direction.values()) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(connectionDir);
            if (prop != null && state.hasProperty(prop) && state.getValue(prop)) {
                shape = Shapes.or(shape, getExtensionShape(state, attachedFace, connectionDir));
            }
        }

        if (attachedFace.getAxis().isHorizontal() && state.hasProperty(CORNER_TYPE)) {
            CornerType corner = state.getValue(CORNER_TYPE);
            if (corner != CornerType.NONE) {
                shape = Shapes.or(shape, getInnerCornerArmShape(attachedFace, corner));
            }
        }

        return shape;
    }

    private static VoxelShape getCoreShape(Direction face) {
        return switch (face) {
            case DOWN  -> Block.box(7.0, 0.0, 7.0, 9.0, THICKNESS, 9.0);
            case UP    -> Block.box(7.0, 16.0 - THICKNESS, 7.0, 9.0, 16.0, 9.0);
            case NORTH -> Block.box(7.0, 7.0, 0.0, 9.0, 9.0, THICKNESS);
            case SOUTH -> Block.box(7.0, 7.0, 16.0 - THICKNESS, 9.0, 9.0, 16.0);
            case WEST  -> Block.box(0.0, 7.0, 7.0, THICKNESS, 9.0, 9.0);
            case EAST  -> Block.box(16.0 - THICKNESS, 7.0, 7.0, 16.0, 9.0, 9.0);
        };
    }

    private static VoxelShape getInnerCornerArmShape(Direction attachedFace, CornerType corner) {
        double yMin = (corner == CornerType.FLOOR) ? 0.0 : 16.0 - THICKNESS;
        double yMax = (corner == CornerType.FLOOR) ? THICKNESS : 16.0;

        return switch (attachedFace) {
            case NORTH -> Block.box(7.0, yMin, 0.0, 9.0, yMax, 16.0);
            case SOUTH -> Block.box(7.0, yMin, 0.0, 9.0, yMax, 16.0);
            case WEST  -> Block.box(0.0, yMin, 7.0, 16.0, yMax, 9.0);
            case EAST  -> Block.box(0.0, yMin, 7.0, 16.0, yMax, 9.0);
            default    -> Shapes.empty();
        };
    }

    private VoxelShape getExtensionShape(BlockState state, Direction face, Direction connection) {
        // Horizontal wire (FLoor/ceiling)
        if (face.getAxis().isVertical() && connection.getAxis().isHorizontal()) {
            double yMin = (face == Direction.DOWN) ? 0.0 : 16.0 - THICKNESS;
            double yMax = (face == Direction.DOWN) ? THICKNESS : 16.0;

            return switch (connection) {
                case NORTH -> Block.box(7.0, yMin, 0.0, 9.0, yMax, 7.0);
                case SOUTH -> Block.box(7.0, yMin, 9.0, 9.0, yMax, 16.0);
                case WEST  -> Block.box(0.0, yMin, 7.0, 7.0, yMax, 9.0);
                case EAST  -> Block.box(9.0, yMin, 7.0, 16.0, yMax, 9.0);
                default -> Shapes.empty();
            };
        }

        // Wall wire (vertical)
        if (face.getAxis().isHorizontal()) {
            if (connection == Direction.DOWN) {
                return switch (face) {
                    case NORTH -> Block.box(7.0, 0.0, 0.0, 9.0, 7.0, THICKNESS);
                    case SOUTH -> Block.box(7.0, 0.0, 16.0 - THICKNESS, 9.0, 7.0, 16.0);
                    case WEST  -> Block.box(0.0, 0.0, 7.0, THICKNESS, 7.0, 9.0);
                    case EAST  -> Block.box(16.0 - THICKNESS, 0.0, 7.0, 16.0, 7.0, 9.0);
                    default -> Shapes.empty();
                };
            }
            if (connection == Direction.UP) {
                return switch (face) {
                    case NORTH -> Block.box(7.0, 9.0, 0.0, 9.0, 16.0, THICKNESS);
                    case SOUTH -> Block.box(7.0, 9.0, 16.0 - THICKNESS, 9.0, 16.0, 16.0);
                    case WEST  -> Block.box(0.0, 9.0, 7.0, THICKNESS, 9.0, 16.0);
                    case EAST  -> Block.box(16.0 - THICKNESS, 9.0, 7.0, 16.0, 16.0, 9.0);
                    default -> Shapes.empty();
                };
            }
            if (connection.getAxis().isHorizontal() && connection != face && connection != face.getOpposite()) {
                return switch (face) {
                    case NORTH -> (connection == Direction.EAST)
                            ? Block.box(9.0, 7.0, 0.0, 16.0, 9.0, THICKNESS)
                            : Block.box(0.0, 7.0, 0.0, 7.0, 9.0, THICKNESS);
                    case SOUTH -> (connection == Direction.EAST)
                            ? Block.box(9.0, 7.0, 16.0 - THICKNESS, 16.0, 9.0, 16.0)
                            : Block.box(0.0, 7.0, 16.0 - THICKNESS, 7.0, 9.0, 16.0);
                    case WEST -> (connection == Direction.SOUTH)
                            ? Block.box(0.0, 7.0, 9.0, THICKNESS, 9.0, 16.0)
                            : Block.box(0.0, 7.0, 0.0, THICKNESS, 9.0, 7.0);
                    case EAST -> (connection == Direction.SOUTH)
                            ? Block.box(16.0 - THICKNESS, 7.0, 9.0, 16.0, 9.0, 16.0)
                            : Block.box(16.0 - THICKNESS, 7.0, 0.0, 16.0, 9.0, 7.0);
                    default -> Shapes.empty();
                };
            }
        }

        return Shapes.empty();
    }
}