package io.github.leiriad.vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public abstract class BaseElectricWireBlock extends Block {

    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    public static final EnumProperty<Direction> VERTICAL_ATTACHMENT = EnumProperty.create("vertical_attachment", Direction.class, Direction.Plane.HORIZONTAL);

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    protected static final double THICKNESS = 1.0;
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public BaseElectricWireBlock(Properties properties) {
        super(properties);
    }

    public abstract Direction getAttachedFace(BlockState state);

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction attachedFace = getAttachedFace(state);
        BlockPos supportingPos = pos.relative(attachedFace);
        BlockState supportingState = level.getBlockState(supportingPos);
        return supportingState.isFaceSturdy(level, supportingPos, attachedFace.getOpposite());
    }

    // =========================================================================
    // CONNECTION DETECTION
    // =========================================================================

    protected boolean canConnectTo(BlockState state, Direction direction) {
        return state.getBlock() instanceof BaseElectricWireBlock;
    }

    protected boolean shouldConnectTo(LevelReader level, BlockPos pos, Direction connectionDir, Direction attachedFace) {
        // A cable never extends through the block it is attached to
        if (connectionDir == attachedFace) return false;

        BlockState neighborState = level.getBlockState(pos.relative(connectionDir));
        if (!(neighborState.getBlock() instanceof BaseElectricWireBlock neighborWire)) {
            return false;
        }

        Direction neighborAttachedFace = neighborWire.getAttachedFace(neighborState);

        // =========================================================================
        // 1. WALL CABLE (Horizontal attached face: NORTH, SOUTH, EAST, WEST)
        // =========================================================================
        if (attachedFace.getAxis().isHorizontal()) {

            // UP connection
            if (connectionDir == Direction.UP) {
                // Connects if the neighbor above is either on the same wall or attached to the ceiling
                return neighborAttachedFace == attachedFace || neighborAttachedFace == Direction.UP;
            }

            // DOWN connection
            if (connectionDir == Direction.DOWN) {
                // Connects if the neighbor below is either on the same wall or attached to the floor
                return neighborAttachedFace == attachedFace || neighborAttachedFace == Direction.DOWN;
            }

            // Horizontal connection on the same wall
            if (connectionDir.getAxis().isHorizontal()) {
                return neighborAttachedFace == attachedFace;
            }
        }

        // =========================================================================
        // 2. CEILING CABLE (attachedFace == UP)
        // =========================================================================
        if (attachedFace == Direction.UP) {

            // Vertical connection to a wall cable below
            if (connectionDir == Direction.DOWN) {
                BlockState belowState = level.getBlockState(pos.below());
                return belowState.getBlock() instanceof BaseElectricWireBlock belowWire
                        && belowWire.getAttachedFace(belowState).getAxis().isHorizontal();
            }

            // Horizontal connection ONLY with another ceiling cable
            if (connectionDir.getAxis().isHorizontal()) {
                return neighborAttachedFace == Direction.UP;
            }
        }

        // =========================================================================
        // 3. FLOOR CABLE (attachedFace == DOWN)
        // =========================================================================
        if (attachedFace == Direction.DOWN) {

            // Vertical connection to a wall cable above
            if (connectionDir == Direction.UP) {
                BlockState aboveState = level.getBlockState(pos.above());
                return aboveState.getBlock() instanceof BaseElectricWireBlock aboveWire
                        && aboveWire.getAttachedFace(aboveState).getAxis().isHorizontal();
            }

            // Horizontal connection ONLY with another floor cable
            if (connectionDir.getAxis().isHorizontal()) {
                return neighborAttachedFace == Direction.DOWN;
            }
        }
        if (attachedFace == Direction.DOWN) {
            if (connectionDir.getAxis().isHorizontal()) {

                // if block is under (outer corner)
                if (neighborState.getBlock() instanceof BaseElectricWireBlock neighborDownWire) {
                    if (neighborWire.getAttachedFace(neighborState) == connectionDir.getOpposite()) {
                        return true;
                    }
                }
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
        DyeColor currentColor = state.getValue(COLOR);
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        Direction attachedFace = getAttachedFace(state);

        // Update horizontal connections on the same plane
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null) {
                BlockState neighbor = level.getBlockState(pos.relative(dir));
                boolean connect = neighbor.getBlock() instanceof BaseElectricWireBlock neighborWire
                        && neighborWire.getAttachedFace(neighbor) == attachedFace;
                state = state.setValue(prop, connect);
            }
        }

        // Reapply vertical bend attachment if it is an ElectricWireBlock (floor/ceiling)
        if (this instanceof ElectricWireBlock wireBlock) {
            state = wireBlock.applyVerticalAttachment(level, pos, state);
        }

        return state.setValue(COLOR, currentColor);
    }

    // =========================================================================
    // HITBOX / VOXELSHAPE RENDERING
    // =========================================================================

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction attachedFace = getAttachedFace(state);
        VoxelShape shape = getCoreShape(attachedFace);

        for (Direction connectionDir : Direction.values()) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(connectionDir);
            if (prop != null && state.getValue(prop)) {
                shape = Shapes.or(shape, getExtensionShape(state, attachedFace, connectionDir, level, pos));
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

    private VoxelShape getExtensionShape(BlockState state, Direction face, Direction connection, BlockGetter level, BlockPos pos) {

        // =========================================================================
        // 1. WALL CABLES (NORTH, SOUTH, EAST, WEST)
        // =========================================================================
        if (face.getAxis().isHorizontal()) {

            // --- Vertical UP Connection ---
            if (connection == Direction.UP) {
                BlockState aboveState = level.getBlockState(pos.above());

                boolean isCeilingAbove = aboveState.getBlock() instanceof BaseElectricWireBlock neighborWire
                        && neighborWire.getAttachedFace(aboveState) == Direction.UP;

                double minY = isCeilingAbove ? 0.0 : 7.0;

                return switch (face) {
                    case NORTH -> Block.box(7.0, minY, 0.0, 9.0, 16.0, 1.0);
                    case SOUTH -> Block.box(7.0, minY, 15.0, 9.0, 16.0, 16.0);
                    case WEST  -> Block.box(0.0, minY, 7.0, 1.0, 16.0, 9.0);
                    case EAST  -> Block.box(15.0, minY, 7.0, 16.0, 16.0, 9.0);
                    default -> Shapes.empty();
                };
            }

            // --- Vertical DOWN Connection ---
            if (connection == Direction.DOWN) {
                BlockState belowState = level.getBlockState(pos.below());

                boolean isFloorBelow = belowState.getBlock() instanceof BaseElectricWireBlock neighborWire
                        && neighborWire.getAttachedFace(belowState) == Direction.DOWN;

                double maxY = isFloorBelow ? 16.0 : 9.0;

                return switch (face) {
                    case NORTH -> Block.box(7.0, 0.0, 0.0, 9.0, maxY, 1.0);
                    case SOUTH -> Block.box(7.0, 0.0, 15.0, 9.0, maxY, 16.0);
                    case WEST  -> Block.box(0.0, 0.0, 7.0, 1.0, maxY, 9.0);
                    case EAST  -> Block.box(15.0, 0.0, 7.0, 16.0, maxY, 9.0);
                    default -> Shapes.empty();
                };
            }

            // --- Horizontal Connections on wall ---
            if (connection.getAxis().isHorizontal() && connection != face && connection != face.getOpposite()) {
                return switch (face) {
                    case NORTH -> connection == Direction.EAST ? Block.box(7.0, 7.0, 0.0, 16.0, 9.0, 1.0) : Block.box(0.0, 7.0, 0.0, 9.0, 9.0, 1.0);
                    case SOUTH -> connection == Direction.EAST ? Block.box(7.0, 7.0, 15.0, 16.0, 9.0, 16.0) : Block.box(0.0, 7.0, 15.0, 9.0, 9.0, 16.0);
                    case WEST  -> connection == Direction.NORTH ? Block.box(0.0, 7.0, 0.0, 1.0, 9.0, 9.0) : Block.box(0.0, 7.0, 7.0, 1.0, 9.0, 16.0);
                    case EAST  -> connection == Direction.NORTH ? Block.box(15.0, 7.0, 0.0, 16.0, 9.0, 9.0) : Block.box(15.0, 7.0, 7.0, 16.0, 9.0, 16.0);
                    default -> Shapes.empty();
                };
            }


        }

        // =========================================================================
        // 2. CEILING CABLE (attachedFace == UP)
        // =========================================================================
        if (face == Direction.UP) {
            if (connection == Direction.DOWN) {
                BlockState belowState = level.getBlockState(pos.below());

                if (belowState.getBlock() instanceof BaseElectricWireBlock neighborWire) {
                    Direction wallFace = neighborWire.getAttachedFace(belowState);

                    return switch (wallFace) {
                        case EAST -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.WEST));
                            double minX = hasOpposite ? 0.0 : 7.0;
                            yield Shapes.or(
                                    Block.box(minX, 15.0, 7.0, 16.0, 16.0, 9.0),
                                    Block.box(15.0, 0.0, 7.0, 16.0, 16.0, 9.0)
                            );
                        }
                        case WEST -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.EAST));
                            double maxX = hasOpposite ? 16.0 : 9.0;
                            yield Shapes.or(
                                    Block.box(0.0, 15.0, 7.0, maxX, 16.0, 9.0),
                                    Block.box(0.0, 0.0, 7.0, 1.0, 16.0, 9.0)
                            );
                        }
                        case NORTH -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.SOUTH));
                            double maxZ = hasOpposite ? 16.0 : 9.0;
                            yield Shapes.or(
                                    Block.box(7.0, 15.0, 0.0, 9.0, 16.0, maxZ),
                                    Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 1.0)
                            );
                        }
                        case SOUTH -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.NORTH));
                            double minZ = hasOpposite ? 0.0 : 7.0;
                            yield Shapes.or(
                                    Block.box(7.0, 15.0, minZ, 9.0, 16.0, 16.0),
                                    Block.box(7.0, 0.0, 15.0, 9.0, 16.0, 16.0)
                            );
                        }
                        default -> Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
                    };
                }
            }

            // Horizontal connection (ceiling)
            if (connection.getAxis().isHorizontal()) {
                return switch (connection) {
                    case NORTH -> Block.box(7.0, 16.0 - THICKNESS, 0.0, 9.0, 16.0, 9.0);
                    case SOUTH -> Block.box(7.0, 16.0 - THICKNESS, 7.0, 9.0, 16.0, 16.0);
                    case WEST  -> Block.box(0.0, 16.0 - THICKNESS, 7.0, 9.0, 16.0, 9.0);
                    case EAST  -> Block.box(7.0, 16.0 - THICKNESS, 7.0, 16.0, 16.0, 9.0);
                    default -> Shapes.empty();
                };
            }
        }

        // =========================================================================
        // 3. FLOOR CABLE (attachedFace == DOWN)
        // =========================================================================
        if (face == Direction.DOWN) {
            if (connection == Direction.UP) {
                BlockState aboveState = level.getBlockState(pos.above());

                if (aboveState.getBlock() instanceof BaseElectricWireBlock neighborWire) {
                    Direction wallFace = neighborWire.getAttachedFace(aboveState);

                    return switch (wallFace) {
                        case EAST -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.WEST));
                            double minX = hasOpposite ? 0.0 : 7.0;
                            yield Shapes.or(
                                    Block.box(minX, 0.0, 7.0, 16.0, 1.0, 9.0),
                                    Block.box(15.0, 0.0, 7.0, 16.0, 16.0, 9.0)
                            );
                        }
                        case WEST -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.EAST));
                            double maxX = hasOpposite ? 16.0 : 9.0;
                            yield Shapes.or(
                                    Block.box(0.0, 0.0, 7.0, maxX, 1.0, 9.0),
                                    Block.box(0.0, 0.0, 7.0, 1.0, 16.0, 9.0)
                            );
                        }
                        case NORTH -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.SOUTH));
                            double maxZ = hasOpposite ? 16.0 : 9.0;
                            yield Shapes.or(
                                    Block.box(7.0, 0.0, 0.0, 9.0, 1.0, maxZ),
                                    Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 1.0)
                            );
                        }
                        case SOUTH -> {
                            boolean hasOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(Direction.NORTH));
                            double minZ = hasOpposite ? 0.0 : 7.0;
                            yield Shapes.or(
                                    Block.box(7.0, 0.0, minZ, 9.0, 1.0, 16.0),
                                    Block.box(7.0, 0.0, 15.0, 9.0, 16.0, 16.0)
                            );
                        }
                        default -> Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
                    };
                }
            }

            // Horizontal connection (floor)
            if (connection.getAxis().isHorizontal()) {
                return switch (connection) {
                    case NORTH -> Block.box(7.0, 0.0, 0.0, 9.0, THICKNESS, 9.0);
                    case SOUTH -> Block.box(7.0, 0.0, 7.0, 9.0, THICKNESS, 16.0);
                    case WEST  -> Block.box(0.0, 0.0, 7.0, 9.0, THICKNESS, 9.0);
                    case EAST  -> Block.box(7.0, 0.0, 7.0, 16.0, THICKNESS, 9.0);
                    default -> Shapes.empty();
                };
            }
        }

        return Shapes.empty();
    }
}