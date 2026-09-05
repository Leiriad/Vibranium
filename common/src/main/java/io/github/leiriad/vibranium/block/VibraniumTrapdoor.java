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
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class VibraniumTrapdoor extends TrapDoorBlock implements VibraniumKineticBlock {
    public static final MapCodec<VibraniumTrapdoor> CODEC = simpleCodec(properties -> new VibraniumTrapdoor(properties));

    public static Properties getProperties(Properties settings) {
        return Properties.ofFullCopy(Blocks.IRON_TRAPDOOR).mapColor(MapColor.COLOR_BLUE)
                .sound(VibraniumSoundType.VIBRANIUM_GRATE)
                .strength(24F, 532.0F)
                .requiresCorrectToolForDrops();
    }

    @Override
    public MapCodec<? extends TrapDoorBlock> codec() {
        return CODEC;
    }

    public VibraniumTrapdoor(Properties properties) {
        super(VibraniumBlockSetType.VIBRANIUM, properties);
    }

    // Triggered when player left-clicks / attacks the trapdoor without the required pickaxe
    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && !isDiamondPickaxe(player.getMainHandItem())) {
            triggerKineticReaction((ServerLevel) level, pos, player, 1.2, new Vec3(0, 1.0, 0));
        }
        super.attack(state, level, pos, player);
    }

    // Triggered when player right-clicks / interacts with the trapdoor without the required pickaxe
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (isDiamondPickaxe(player.getItemInHand(hand))) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!level.isClientSide()) {
            triggerKineticReaction((ServerLevel) level, pos, player, 1.0, new Vec3(0, 1.0, 0));
        }
        return InteractionResult.SUCCESS;
    }
}