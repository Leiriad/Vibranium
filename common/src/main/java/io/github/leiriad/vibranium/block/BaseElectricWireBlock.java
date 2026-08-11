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
        if (connectionDir == attachedFace) return false;

        // 1. Détection voisin direct (Même hauteur / plan direct)
        BlockPos targetPos = pos.relative(connectionDir);
        BlockState neighborState = level.getBlockState(targetPos);

        if (neighborState.getBlock() instanceof BaseElectricWireBlock neighborWire) {
            Direction neighborAttachedFace = neighborWire.getAttachedFace(neighborState);

            if (neighborAttachedFace == attachedFace) return true; // Même face d'accroche (ex: 2 câbles au sol)
            if (neighborAttachedFace == connectionDir.getOpposite()) return true; // Câble collé sur le bloc d'en face

            if (attachedFace.getAxis().isHorizontal()) {
                if (connectionDir == Direction.UP && (neighborAttachedFace == Direction.UP || neighborAttachedFace == attachedFace)) return true;
                if (connectionDir == Direction.DOWN && (neighborAttachedFace == Direction.DOWN || neighborAttachedFace == attachedFace)) return true;
            } else if (attachedFace == Direction.DOWN && connectionDir == Direction.UP) {
                if (neighborAttachedFace.getAxis().isHorizontal()) return true;
            } else if (attachedFace == Direction.UP && connectionDir == Direction.DOWN) {
                if (neighborAttachedFace.getAxis().isHorizontal()) return true;
            }
        }

        // 2. Détection Câble Latéral sur le MÊME bloc support
        // Ex: Câble posé sur le bloc (attachedFace == DOWN) & Câble sur la face latérale du même bloc
        BlockPos supportPos = pos.relative(attachedFace);
        BlockPos lateralWirePos = supportPos.relative(connectionDir);
        BlockState lateralState = level.getBlockState(lateralWirePos);

        if (lateralState.getBlock() instanceof BaseElectricWireBlock lateralWire) {
            Direction lateralAttachedFace = lateralWire.getAttachedFace(lateralState);
            if (lateralAttachedFace == connectionDir.getOpposite()) {
                return true;
            }
        }

        // 3. Détection Marche d'escalier (Câble horizontal posé un bloc plus bas à côté)
        if (attachedFace == Direction.DOWN && connectionDir.getAxis().isHorizontal()) {
            BlockPos lowerStepPos = targetPos.below();
            BlockState lowerStepState = level.getBlockState(lowerStepPos);
            if (lowerStepState.getBlock() instanceof BaseElectricWireBlock stepWire) {
                if (stepWire.getAttachedFace(lowerStepState) == Direction.DOWN) {
                    return true;
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

        for (Direction dir : Direction.values()) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null) {
                boolean connect = shouldConnectTo(level, pos, dir, attachedFace);
                state = state.setValue(prop, connect);
            }
        }

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
            if (prop != null && state.hasProperty(prop) && state.getValue(prop)) {
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
        if (face == Direction.DOWN) {
            if (connection == Direction.UP) {
                return Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
            }
            if (connection.getAxis().isHorizontal()) {
                // Vérification coin extérieur (Support latéral)
                BlockPos sameSupportNeighborPos = pos.below().relative(connection);
                BlockState sameSupportState = level.getBlockState(sameSupportNeighborPos);
                boolean isEdgeWrap = sameSupportState.getBlock() instanceof BaseElectricWireBlock neighborWire
                        && neighborWire.getAttachedFace(sameSupportState) == connection.getOpposite();

                // Vérification marche d'escalier vers le bas
                BlockPos lowerStepPos = pos.relative(connection).below();
                BlockState lowerStepState = level.getBlockState(lowerStepPos);
                boolean isStepDown = lowerStepState.getBlock() instanceof BaseElectricWireBlock stepWire
                        && stepWire.getAttachedFace(lowerStepState) == Direction.DOWN;

                VoxelShape flatExtension = switch (connection) {
                    case NORTH -> Block.box(7.0, 0.0, 0.0, 9.0, THICKNESS, 7.0);
                    case SOUTH -> Block.box(7.0, 0.0, 9.0, 9.0, THICKNESS, 16.0);
                    case WEST  -> Block.box(0.0, 0.0, 7.0, 7.0, THICKNESS, 9.0);
                    case EAST  -> Block.box(9.0, 0.0, 7.0, 16.0, THICKNESS, 9.0);
                    default -> Shapes.empty();
                };

                // Extension verticale vers le bas (Coin de bloc support OU marche d'escalier)
                if (isEdgeWrap || isStepDown) {
                    VoxelShape verticalDrop = switch (connection) {
                        case NORTH -> Block.box(7.0, -16.0, 0.0, 9.0, 0.0, THICKNESS);
                        case SOUTH -> Block.box(7.0, -16.0, 16.0 - THICKNESS, 9.0, 0.0, 16.0);
                        case WEST  -> Block.box(0.0, -16.0, 7.0, THICKNESS, 0.0, 9.0);
                        case EAST  -> Block.box(16.0 - THICKNESS, -16.0, 7.0, 16.0, 0.0, 9.0);
                        default -> Shapes.empty();
                    };
                    return Shapes.or(flatExtension, verticalDrop);
                }

                return flatExtension;
            }
        }

        if (face == Direction.UP) {
            if (connection == Direction.DOWN) {
                return Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
            }
            if (connection.getAxis().isHorizontal()) {
                BlockPos neighborPos = pos.above().relative(connection);
                BlockState neighborState = level.getBlockState(neighborPos);
                boolean isEdgeWrap = neighborState.getBlock() instanceof BaseElectricWireBlock neighborWire
                        && neighborWire.getAttachedFace(neighborState) == connection.getOpposite();

                VoxelShape flatExtension = switch (connection) {
                    case NORTH -> Block.box(7.0, 16.0 - THICKNESS, 0.0, 9.0, 16.0, 7.0);
                    case SOUTH -> Block.box(7.0, 16.0 - THICKNESS, 9.0, 9.0, 16.0, 16.0);
                    case WEST  -> Block.box(0.0, 16.0 - THICKNESS, 7.0, 7.0, 16.0, 9.0);
                    case EAST  -> Block.box(9.0, 16.0 - THICKNESS, 7.0, 16.0, 16.0, 9.0);
                    default -> Shapes.empty();
                };

                if (isEdgeWrap) {
                    VoxelShape verticalDrop = switch (connection) {
                        case NORTH -> Block.box(7.0, 16.0, 0.0, 9.0, 32.0, THICKNESS);
                        case SOUTH -> Block.box(7.0, 16.0, 16.0 - THICKNESS, 9.0, 32.0, 16.0);
                        case WEST  -> Block.box(0.0, 16.0, 7.0, THICKNESS, 32.0, 9.0);
                        case EAST  -> Block.box(16.0 - THICKNESS, 16.0, 7.0, 16.0, 32.0, 9.0);
                        default -> Shapes.empty();
                    };
                    return Shapes.or(flatExtension, verticalDrop);
                }

                return flatExtension;
            }
        }

        if (face.getAxis().isHorizontal()) {
            if (connection == Direction.UP) {
                return switch (face) {
                    case NORTH -> Block.box(7.0, 9.0, 0.0, 9.0, 16.0, THICKNESS);
                    case SOUTH -> Block.box(7.0, 9.0, 16.0 - THICKNESS, 9.0, 16.0, 16.0);
                    case WEST  -> Block.box(0.0, 9.0, 7.0, THICKNESS, 16.0, 9.0);
                    case EAST  -> Block.box(16.0 - THICKNESS, 9.0, 7.0, 16.0, 16.0, 9.0);
                    default -> Shapes.empty();
                };
            }
            if (connection == Direction.DOWN) {
                return switch (face) {
                    case NORTH -> Block.box(7.0, 0.0, 0.0, 9.0, 7.0, THICKNESS);
                    case SOUTH -> Block.box(7.0, 0.0, 16.0 - THICKNESS, 9.0, 7.0, 16.0);
                    case WEST  -> Block.box(0.0, 0.0, 7.0, THICKNESS, 0.0, 9.0);
                    case EAST  -> Block.box(16.0 - THICKNESS, 0.0, 7.0, 16.0, 0.0, 9.0);
                    default -> Shapes.empty();
                };
            }

            if (connection.getAxis().isHorizontal() && connection != face && connection != face.getOpposite()) {
                BlockPos neighborPos = pos.relative(face).relative(connection);
                BlockState neighborState = level.getBlockState(neighborPos);
                boolean isEdgeWrap = neighborState.getBlock() instanceof BaseElectricWireBlock neighborWire
                        && neighborWire.getAttachedFace(neighborState) == connection.getOpposite();

                VoxelShape flatExtension = switch (face) {
                    case NORTH -> connection == Direction.EAST ? Block.box(9.0, 7.0, 0.0, 16.0, 9.0, THICKNESS) : Block.box(0.0, 7.0, 0.0, 7.0, 9.0, THICKNESS);
                    case SOUTH -> connection == Direction.EAST ? Block.box(9.0, 7.0, 16.0 - THICKNESS, 16.0, 9.0, 16.0) : Block.box(0.0, 7.0, 16.0 - THICKNESS, 7.0, 9.0, 16.0);
                    case WEST  -> connection == Direction.NORTH ? Block.box(0.0, 7.0, 0.0, THICKNESS, 9.0, 7.0) : Block.box(0.0, 7.0, 9.0, THICKNESS, 9.0, 16.0);
                    case EAST  -> connection == Direction.NORTH ? Block.box(16.0 - THICKNESS, 7.0, 0.0, 16.0, 9.0, 7.0) : Block.box(16.0 - THICKNESS, 7.0, 9.0, 16.0, 9.0, 16.0);
                    default -> Shapes.empty();
                };

                if (isEdgeWrap) {
                    VoxelShape edgeWrapExtension = switch (face) {
                        case NORTH -> connection == Direction.EAST ? Block.box(16.0 - THICKNESS, 7.0, -THICKNESS, 16.0, 9.0, 0.0) : Block.box(0.0, 7.0, -THICKNESS, THICKNESS, 9.0, 0.0);
                        case SOUTH -> connection == Direction.EAST ? Block.box(16.0 - THICKNESS, 7.0, 16.0, 16.0, 9.0, 16.0 + THICKNESS) : Block.box(0.0, 7.0, 16.0, THICKNESS, 9.0, 16.0 + THICKNESS);
                        case WEST  -> connection == Direction.NORTH ? Block.box(-THICKNESS, 7.0, 0.0, 0.0, 9.0, THICKNESS) : Block.box(-THICKNESS, 7.0, 16.0 - THICKNESS, 0.0, 9.0, 16.0);
                        case EAST  -> connection == Direction.NORTH ? Block.box(16.0, 7.0, 0.0, 16.0 + THICKNESS, 9.0, THICKNESS) : Block.box(16.0, 7.0, 16.0 - THICKNESS, 16.0 + THICKNESS, 9.0, 16.0);
                        default -> Shapes.empty();
                    };
                    return Shapes.or(flatExtension, edgeWrapExtension);
                }

                return flatExtension;
            }
        }

        return Shapes.empty();
    }
}