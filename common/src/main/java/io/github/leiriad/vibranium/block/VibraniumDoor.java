package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.sound.VibraniumSoundType;
import io.github.leiriad.vibranium.utils.VibraniumBlockSetType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class VibraniumDoor extends DoorBlock implements VibraniumKineticBlock {
    public static final MapCodec<VibraniumDoor> CODEC = simpleCodec(properties -> new VibraniumDoor(properties));

    public static Properties getProperties(Properties settings) {
        return Properties.ofFullCopy(Blocks.IRON_DOOR).mapColor(MapColor.COLOR_BLACK)
                .sound(VibraniumSoundType.VIBRANIUM_BLOCK)
                .strength(50.0F, 1200.0F)
                .requiresCorrectToolForDrops();
    }

    @Override
    public MapCodec<? extends DoorBlock> codec() {
        return CODEC;
    }

    public VibraniumDoor(Properties properties) {
        super(VibraniumBlockSetType.VIBRANIUM, properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (isDiamondPickaxe(player.getItemInHand(hand))) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!level.isClientSide()) {
            triggerKineticReaction((ServerLevel) level, pos, player, 1.0, null);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && !isDiamondPickaxe(player.getMainHandItem())) {
            triggerKineticReaction((ServerLevel) level, pos, player, 1.2, null);
        }
        super.attack(state, level, pos, player);
    }
}