package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.entity.VibraniumBlockEntity;
import io.github.leiriad.vibranium.sound.VibraniumSoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VibraniumBlock extends BaseEntityBlock {
    //PROPERTIES
    public static final MapCodec<VibraniumBlock> CODEC = simpleCodec(VibraniumBlock::new);
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 10);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_BLUE)
                .sound(VibraniumSoundType.VIBRANIUM_BLOCK)
                .strength(50.0F, 1200.0F) // High blast resistance
                // Linear dynamic light emission from 0 to 15 based on a 10-step charge
                .lightLevel((state) -> {
                    int charge = state.getValue(CHARGE);
                    return Math.round((charge / 10.0F) * 15.0F);
                })
                .requiresCorrectToolForDrops();
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public VibraniumBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VibraniumBlockEntity(pos, state);
    }

    // Accumulate charge when hit by projectiles
    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide() && level.getBlockEntity(hit.getBlockPos()) instanceof VibraniumBlockEntity vibraniumEntity) {
            addCharge(vibraniumEntity,1,level,new BlockPos(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()));
        }
    }

    // Accumulate charge when stepped on or fallen upon
    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (!level.isClientSide() && fallDistance > 2.0D && level.getBlockEntity(pos) instanceof VibraniumBlockEntity vibraniumEntity) {
            int addedCharge = Math.min(4, (int) (fallDistance / 3.0D));
            addCharge(vibraniumEntity,addedCharge,level,pos);
        }
        // Absorb fall damage entirely (Lore-accurate shock absorption)
        entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
    }

    // Accumulate charge when struck by a player (left-click / attack)
    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof VibraniumBlockEntity vibraniumEntity) {
            // Adds 1 charge point per hit
            addCharge(vibraniumEntity,1,level,pos);
        }
        super.attack(state, level, pos, player);
    }

    // Manual trigger via right-click with empty hand or sneaking
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof VibraniumBlockEntity vibraniumEntity) {
            if (vibraniumEntity.getCharge() > 0) {
                vibraniumEntity.releaseKineticEnergy((ServerLevel) level, player);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    // Redstone trigger: Releases shockwave immediately if charged
    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide() && level.hasNeighborSignal(blockPos)) {
            if (level.getBlockEntity(blockPos) instanceof VibraniumBlockEntity vibraniumEntity && vibraniumEntity.getCharge() > 0) {
                vibraniumEntity.releaseKineticEnergy((ServerLevel) level, null);
            }
        }
    }

    //Plays charge sound
    private void addCharge(VibraniumBlockEntity vibraniumEntity, int charge, Level level, BlockPos pos) {
        vibraniumEntity.addCharge(charge);

        // Play a metallic vibration feedback sound
        level.playSound(
                null,
                pos,
                SoundEvents.ANVIL_PLACE,
                SoundSource.BLOCKS,
                0.3F,
                1.5F + (vibraniumEntity.getCharge() * 0.1F) // Pitch rises with charge level
        );
    }

    // Ensures the block drops a standard stackable item with zero stored charge
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        // Returns the default uncharged block item stack
        return List.of(new ItemStack(this));
    }
}
