package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.fluid.FluidHelper;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.utils.TankSegment;
import io.github.leiriad.vibranium.utils.VibraniumTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;


public class FluidTankBlock extends BaseEntityBlock {

    //PROPERTIES
    public static final MapCodec<FluidTankBlock> CODEC = simpleCodec(FluidTankBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final EnumProperty<TankSegment> SEGMENT = EnumProperty.create("segment", TankSegment.class);

    public static Properties getProperties (Properties settings){
        return Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .noOcclusion()
                .sound(SoundType.GLASS)
                .instrument(NoteBlockInstrument.CHIME);
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public FluidTankBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition()
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SEGMENT, TankSegment.SINGLE));
    }

    //TICKER
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // On retourne la méthode tick de notre BlockEntity si c'est le bon type
        return (lvl, pos, st, be) -> {
            if (be instanceof FluidTankEntity tank) {
                tank.tick(lvl, pos);
            }
        };
    }

    //METHODS
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluidTankEntity(blockPos, blockState);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(SEGMENT);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Calculate the segment state based on top and bottom neighbors at placement time
        BlockState calculatedState = calculateSegment(context.getLevel(), context.getClickedPos());

        return calculatedState.setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    /// Logic to calculate segment position based on adjacent tank blocks
    private BlockState calculateSegment(LevelReader level, BlockPos pos) {
        boolean hasTankAbove = level.getBlockState(pos.above()).is(this);
        boolean hasTankBelow = level.getBlockState(pos.below()).is(this);

        TankSegment segment;
        if (hasTankAbove && hasTankBelow) {
            segment = TankSegment.MIDDLE;
        } else if (hasTankAbove) {
            segment = TankSegment.BOTTOM;
        } else if (hasTankBelow) {
            segment = TankSegment.TOP;
        } else {
            segment = TankSegment.SINGLE;
        }

        return this.defaultBlockState().setValue(SEGMENT, segment);
    }


    //Update when changed
    @Override
    protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource randomSource
    ) {
        // Recalculate segment only when the updated neighbor is directly above or below
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return calculateSegment(levelReader, pos).setValue(FACING, state.getValue(FACING));
        }
        return super.updateShape(state, levelReader, scheduledTickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            this.notifyAdjacentReactor(level, pos);
        }
    }
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
        this.notifyAdjacentReactor(level, pos);
    }
    private void notifyAdjacentReactor(Level level, BlockPos tankPos) {
        // Scan a 3x3x3 area around the tank to find the Reactor Core
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    mutablePos.set(tankPos.getX() + x, tankPos.getY() + y, tankPos.getZ() + z);

                    BlockEntity be = level.getBlockEntity(mutablePos);
                    if (be instanceof ReactorCoreEntity core) {
                        // Force the reactor to rescan and recalculate capacities immediately
                        core.scanForComponents(level, mutablePos);
                        core.updateFluidLevels();
                        return; // Core found and updated, we can stop searching
                    }
                }
            }
        }
    }

    ///Fill with bucket
    @Override
    protected InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // We only process logic on the logical server side
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FluidTankEntity tank)) return InteractionResult.PASS;

        // CASE: Player has a Fluid Bucket -> Fill the tank (or the column above)
        Fluid heldFluid = FluidHelper.getFluidFromItem(heldItem);
        if (heldFluid != Fluids.EMPTY) {
            // Use our cascade fill method! It returns how much fluid was actually accepted.
            long accepted = tank.fill(1000, heldFluid);

            if (accepted > 0) {
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                    player.getInventory().add(new ItemStack(Items.BUCKET));
                }
                return InteractionResult.SUCCESS;
            }
        }

        // CASE: Player has an Empty Bucket -> Empty the tank
        if (heldItem.is(Items.BUCKET)) {
            Fluid storedFluid = tank.getStoredFluid();
            if (storedFluid != Fluids.EMPTY && tank.getFluidAmount() >= 1000) {
                // Use our drain method to ensure NBT updates properly
                tank.drain(1000);

                ItemStack fullBucket = new ItemStack(FluidHelper.getBucketFromFluid(storedFluid));
                heldItem.shrink(1);
                player.getInventory().add(fullBucket);

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    ///Render
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
