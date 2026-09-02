package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.sound.VibraniumSoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class VibraniumGrate extends Block implements SimpleWaterloggedBlock {
    //PROPERTIES
    public static final MapCodec<Block> CODEC = simpleCodec(BlackBricks::new);
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_BLACK)
                .sound(VibraniumSoundType.VIBRANIUM_GRATE)
                .strength(50.0F, 1200.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public VibraniumGrate(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false)
                .setValue(POWERED, false));
    }

    //METHODS
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, POWERED);
    }

    // Handles orientation during placement (e.g., placing on walls, ceiling, floor)
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean hasSignal = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getClickedFace())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(POWERED, hasSignal);
    }
    // Updates POWERED state when neighbor blocks change (redstone signals)
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean hasSignal = level.hasNeighborSignal(pos);
            if (hasSignal != state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
            }
        }
    }
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // Triggered when an entity walks/steps directly on the block
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        handleRepulsion(state, level, pos, entity);
        super.stepOn(level, pos, state, entity);
    }

    // Triggered when an entity is inside the block collision zone
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean bool) {
        handleRepulsion(state, level, pos, entity);
        super.entityInside(state, level, pos, entity,applier, bool);
    }

    // Directional repulsion shockwave
    private void handleRepulsion(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && state.getValue(POWERED) && entity instanceof LivingEntity livingEntity) {
            if (livingEntity.invulnerableTime <= 0) {
                Direction facing = state.getValue(FACING);
                triggerDirectionalRepulsion((ServerLevel) level, pos, facing, livingEntity);

                // Prevents instant spam execution on every single tick
                livingEntity.invulnerableTime = 10;
            }
        }
    }
    private void triggerDirectionalRepulsion(ServerLevel level, BlockPos pos, Direction facing, LivingEntity target) {
        Vec3 center = Vec3.atCenterOf(pos);

        // Visual effects (kinetic flash/sparks)
        level.sendParticles(
                ParticleTypes.SONIC_BOOM,
                center.x, center.y, center.z,
                1, 0.0, 0.0, 0.0, 0.0
        );

        level.playSound(
                null, pos,
                SoundEvents.WIND_CHARGE_BURST.value(),
                SoundSource.BLOCKS,
                0.6F, 1.8F
        );

        // Directional push calculation
        double pushForce = 0.75; // Adjusted pulse strength
        Vec3 pushVector = new Vec3(
                facing.getStepX() * pushForce,
                facing.getStepY() * pushForce + (facing == Direction.UP ? 0.2 : 0.0),
                facing.getStepZ() * pushForce
        );

        // Apply instant velocity impulse
        target.setDeltaMovement(target.getDeltaMovement().add(pushVector));
        target.hurtMarked = true; // Force movement packet sync with client
    }
}
