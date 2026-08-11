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
    // RÈGLES DE CONNEXION
    // =========================================================================

    protected boolean shouldConnectTo(LevelReader level, BlockPos pos, Direction connectionDir, Direction attachedFace) {
        if (connectionDir == attachedFace) return false;

        BlockPos targetPos = pos.relative(connectionDir);
        BlockState targetState = level.getBlockState(targetPos);

        // --- A. CÂBLE HORIZONTAL (SOL / PLAFOND) ---
        if (attachedFace.getAxis().isVertical()) {
            if (connectionDir.getAxis().isHorizontal()) {
                if (targetState.getBlock() instanceof BaseElectricWireBlock targetWire) {
                    Direction targetAttached = targetWire.getAttachedFace(targetState);
                    if (targetAttached == attachedFace) return true;
                    if (targetAttached == connectionDir.getOpposite()) return true;
                }
                if (targetState.getBlock() instanceof BaseElectricWireBlock targetWire) {
                    if (targetWire.getAttachedFace(targetState) == connectionDir) return true;
                }
            }
        }

        // --- B. LE CÂBLE COURANT EST VERTICAL (MUR) ---
        if (attachedFace.getAxis().isHorizontal()) {

            // 1. Connexion verticale directe (Haut / Bas sur le même mur)
            if (connectionDir == Direction.UP || connectionDir == Direction.DOWN) {
                // A. Câble vertical directement au-dessus ou en-dessous sur la même paroi
                if (targetState.getBlock() instanceof BaseElectricWireBlock targetWire) {
                    if (targetWire.getAttachedFace(targetState) == attachedFace) {
                        return true;
                    }
                }
            }

            // 2. Connexions horizontales sur le même mur OU en outer corner autour du bloc
            if (connectionDir.getAxis().isHorizontal() && connectionDir != attachedFace && connectionDir != attachedFace.getOpposite()) {
                if (targetState.getBlock() instanceof BaseElectricWireBlock targetWire) {
                    Direction targetAttached = targetWire.getAttachedFace(targetState);
                    // Même mur
                    if (targetAttached == attachedFace) return true;
                    // Tour du même bloc (Outer Corner) : câble fixé sur la face 'connectionDir'
                    if (targetAttached == connectionDir) return true;
                }
            }

            // 3. Connexion à un câble horizontal (sol / plafond)
            if (connectionDir == Direction.DOWN || connectionDir == Direction.UP) {
                BlockPos adjacentPos = pos.relative(attachedFace.getOpposite());
                BlockState adjacentState = level.getBlockState(adjacentPos);
                if (adjacentState.getBlock() instanceof BaseElectricWireBlock adjacentWire) {
                    if (adjacentWire.getAttachedFace(adjacentState) == connectionDir) {
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
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        DyeColor currentColor = state.getValue(COLOR);
        Direction attachedFace = getAttachedFace(state);

        for (Direction dir : Direction.values()) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null) {
                state = state.setValue(prop, shouldConnectTo(level, pos, dir, attachedFace));
            }
        }

        if (attachedFace.getAxis().isHorizontal() && state.hasProperty(CORNER_TYPE)) {
            CornerType cornerType = CornerType.NONE;

            // Vérification au sol (câble horizontal sur la face DOWN du bloc adjacent)
            BlockPos floorPos = pos.relative(attachedFace.getOpposite());
            BlockState floorState = level.getBlockState(floorPos);
            if (floorState.getBlock() instanceof BaseElectricWireBlock floorWire && floorWire.getAttachedFace(floorState) == Direction.DOWN) {
                cornerType = CornerType.FLOOR;
            }

            // Vérification au plafond (câble horizontal sur la face UP du bloc adjacent)
            BlockPos ceilingPos = pos.relative(attachedFace.getOpposite());
            BlockState ceilingState = level.getBlockState(ceilingPos);
            if (ceilingState.getBlock() instanceof BaseElectricWireBlock ceilingWire && ceilingWire.getAttachedFace(ceilingState) == Direction.UP) {
                cornerType = CornerType.CEILING;
            }

            state = state.setValue(CORNER_TYPE, cornerType);
        }

        return state.setValue(COLOR, currentColor);
    }

    // =========================================================================
    // VOXELSHAPES / HITBOXES
    // =========================================================================

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction attachedFace = getAttachedFace(state);
        VoxelShape shape = getCoreShape(attachedFace);

        // Ajout des extensions selon les directions connectées
        for (Direction connectionDir : Direction.values()) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(connectionDir);
            if (prop != null && state.hasProperty(prop) && state.getValue(prop)) {
                shape = Shapes.or(shape, getExtensionShape(state, attachedFace, connectionDir));
            }
        }

        // Ajout de la branche horizontale de 16 pixels au sol ou au plafond pour les Inner Corners
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

    /**
     * Génère la branche horizontale de 16 pixels au sol ou au plafond formant la barre du 'L' de l'inner corner.
     */
    private static VoxelShape getInnerCornerArmShape(Direction attachedFace, CornerType corner) {
        double yMin = corner == CornerType.FLOOR ? 0.0 : 16.0 - THICKNESS;
        double yMax = corner == CornerType.FLOOR ? THICKNESS : 16.0;

        return switch (attachedFace) {
            case NORTH -> Block.box(7.0, yMin, 0.0, 9.0, yMax, 16.0);
            case SOUTH -> Block.box(7.0, yMin, 0.0, 9.0, yMax, 16.0);
            case WEST  -> Block.box(0.0, yMin, 7.0, 16.0, yMax, 9.0);
            case EAST  -> Block.box(0.0, yMin, 7.0, 16.0, yMax, 9.0);
            default    -> Shapes.empty();
        };
    }

    private VoxelShape getExtensionShape(BlockState state, Direction face, Direction connection) {
        // CÂBLE AU SOL / PLAFOND
        if (face.getAxis().isVertical() && connection.getAxis().isHorizontal()) {
            double yMin = face == Direction.DOWN ? 0.0 : 16.0 - THICKNESS;
            double yMax = face == Direction.DOWN ? THICKNESS : 16.0;
            return switch (connection) {
                case NORTH -> Block.box(7.0, yMin, 0.0, 9.0, yMax, 7.0);
                case SOUTH -> Block.box(7.0, yMin, 9.0, 9.0, yMax, 16.0);
                case WEST  -> Block.box(0.0, yMin, 7.0, 7.0, yMax, 9.0);
                case EAST  -> Block.box(9.0, yMin, 7.0, 16.0, yMax, 9.0);
                default -> Shapes.empty();
            };
        }

        // CÂBLE MURAL / VERTICAL
        if (face.getAxis().isHorizontal()) {
            // Extension vers le BAS (du centre Y=7 jusqu'au bas du bloc Y=0)
            if (connection == Direction.DOWN) {
                return switch (face) {
                    case NORTH -> Block.box(7.0, 0.0, 0.0, 9.0, 7.0, THICKNESS);
                    case SOUTH -> Block.box(7.0, 0.0, 16.0 - THICKNESS, 9.0, 7.0, 16.0);
                    case WEST  -> Block.box(0.0, 0.0, 7.0, THICKNESS, 7.0, 9.0);
                    case EAST  -> Block.box(16.0 - THICKNESS, 0.0, 7.0, 16.0, 7.0, 9.0);
                    default -> Shapes.empty();
                };
            }
            // Extension vers le HAUT (du centre Y=9 jusqu'au haut du bloc Y=16)
            if (connection == Direction.UP) {
                return switch (face) {
                    case NORTH -> Block.box(7.0, 9.0, 0.0, 9.0, 16.0, THICKNESS);
                    case SOUTH -> Block.box(7.0, 9.0, 16.0 - THICKNESS, 9.0, 16.0, 16.0);
                    case WEST  -> Block.box(0.0, 9.0, 7.0, THICKNESS, 9.0, 9.0); // Correction de la profondeur (7-9 au lieu de 9-16)
                    case EAST  -> Block.box(16.0 - THICKNESS, 9.0, 7.0, 16.0, 16.0, 9.0); // Correction de la profondeur (7-9 au lieu de 9-16)
                    default -> Shapes.empty();
                };
            }
            // Extension HORIZONTALE sur le même mur
            if (connection.getAxis().isHorizontal() && connection != face && connection != face.getOpposite()) {
                return switch (face) {
                    case NORTH -> connection == Direction.EAST ? Block.box(9.0, 7.0, 0.0, 16.0, 9.0, THICKNESS) : Block.box(0.0, 7.0, 0.0, 7.0, 9.0, THICKNESS);
                    case SOUTH -> connection == Direction.EAST ? Block.box(9.0, 7.0, 16.0 - THICKNESS, 16.0, 9.0, 16.0) : Block.box(0.0, 7.0, 16.0 - THICKNESS, 7.0, 9.0, 16.0);
                    case WEST  -> connection == Direction.NORTH ? Block.box(0.0, 7.0, 0.0, THICKNESS, 9.0, 7.0) : Block.box(0.0, 7.0, 9.0, THICKNESS, 9.0, 16.0);
                    case EAST  -> connection == Direction.NORTH ? Block.box(16.0 - THICKNESS, 7.0, 0.0, 16.0, 9.0, 7.0) : Block.box(16.0 - THICKNESS, 7.0, 9.0, 16.0, 9.0, 16.0);
                    default -> Shapes.empty();
                };
            }
        }

        return Shapes.empty();
    }
}