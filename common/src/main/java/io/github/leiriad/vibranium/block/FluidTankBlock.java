package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.fluid.FluidHelper;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.utils.VibraniumTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
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
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    //Fill with bucket
    @Override
    protected InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // We only process logic on the logical server side
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FluidTankEntity tank)) return InteractionResult.PASS;

        // CASE: Player has a Fluid Bucket -> Fill the tank
        Fluid heldFluid = FluidHelper.getFluidFromItem(heldItem);
        if (heldFluid != Fluids.EMPTY) {
            if (tank.getStoredFluid() == Fluids.EMPTY || tank.getStoredFluid().isSame(heldFluid)) {
                long capacity = tank.getCapacity();
                if (capacity - tank.getFluidAmount() >= 1000) {
                    tank.setFluid(heldFluid, tank.getFluidAmount() + 1000);

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                        player.getInventory().add(new ItemStack(Items.BUCKET));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        // CASE: Player has an Empty Bucket -> Empty the tank
        if (heldItem.is(Items.BUCKET)) {
            Fluid storedFluid = tank.getStoredFluid();
            if (storedFluid != Fluids.EMPTY && tank.getFluidAmount() >= 1000) {
                long newAmount = tank.getFluidAmount() - 1000;
                tank.setFluid(newAmount <= 0 ? Fluids.EMPTY : storedFluid, newAmount <= 0 ? 0L : newAmount);

                ItemStack fullBucket = new ItemStack(FluidHelper.getBucketFromFluid(storedFluid));
                heldItem.shrink(1);
                player.getInventory().add(fullBucket);

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }


    //Render
    @Override
    protected net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.MODEL;
    }
}
