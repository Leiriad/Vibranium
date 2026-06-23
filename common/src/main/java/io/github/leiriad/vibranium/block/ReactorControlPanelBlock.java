package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.leiriad.vibranium.entity.ReactorControlPanelEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ReactorControlPanelBlock extends BaseEntityBlock {
    //PROPERTIES
    public static final MapCodec<ReactorControlPanelBlock> CODEC = simpleCodec(ReactorControlPanelBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static Properties getProperties(Properties of) {
        return Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .instrument(NoteBlockInstrument.CHIME)
                .noOcclusion();
    }

    //CONSTRUCTOR
    public ReactorControlPanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    //METHODS
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReactorControlPanelEntity(pos, state);
    }
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ReactorControlPanelEntity panel) {
                ReactorCoreEntity core = panel.findNearbyCore();

                if (core != null) {
                    MenuRegistry.openExtendedMenu((ServerPlayer) player, panel, buf -> {
                        buf.writeBlockPos(core.getBlockPos());
                    });
                    return InteractionResult.SUCCESS;
                }
            }
        }
        //Client opening
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
