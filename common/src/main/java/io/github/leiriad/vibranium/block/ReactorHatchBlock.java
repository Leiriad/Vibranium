package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.entity.ReactorHatchEntity;
import io.github.leiriad.vibranium.menu.ReactorHatchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;


public class ReactorHatchBlock extends BaseEntityBlock implements WorldlyContainerHolder {

    //PROPERTIES
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<ReactorHatchBlock> CODEC = simpleCodec(ReactorHatchBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public static Properties getProperties (Properties settings){
        return Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .instrument(NoteBlockInstrument.CHIME).requiresCorrectToolForDrops();
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public ReactorHatchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(LIT, false)
        );
    }

    //METHODS
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReactorHatchEntity(blockPos, blockState);
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
    public WorldlyContainer getContainer(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        BlockEntity be = levelAccessor.getBlockEntity(blockPos);
        if (be instanceof ReactorHatchEntity hatch) {
            return hatch;
        }
        return null;
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS; // Once client has clicked the server manages
        }

        // Get Hatch entity
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ReactorHatchEntity hatch)) {
            return InteractionResult.FAIL;
        }

        // Check if core is linked
        if (hatch.getConnectedCore(level) == null) {
            player.displayClientMessage(Component.translatable("message.vibranium.hatch_no_core"), true);
            return InteractionResult.FAIL;
        }

        // Open menu with hatch location
        MenuRegistry.openExtendedMenu(
                (ServerPlayer) player,
                new ExtendedMenuProvider() {
                    @Override
                    public void saveExtraData(FriendlyByteBuf buf) {
                        buf.writeBlockPos(pos);
                    }

                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("block.vibranium.reactor_hatch");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
                        return new ReactorHatchMenu(id, playerInv, pos);
                    }
                }
        );

        return InteractionResult.CONSUME;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ReactorHatchEntity hatch) {
                ReactorHatchEntity.tick(level1, pos, state1, hatch);
            }
        };
    }
    /// Make sure the BlockEntity is retained when changing properties like LIT
    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ReactorHatchEntity hatch) {
            if (!level.isClientSide()) {
                Containers.dropContents(level, pos, hatch.inventory);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
