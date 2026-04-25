package io.github.leiriad.vibranium.fabric;

import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;


public class VibraniumToolEventsFabric {
    ///Player actions on blocs
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {

            if (world.isClientSide()) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);
            Item item = stack.getItem();

            BlockPos pos = hit.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // Shovel
            if (item instanceof ShovelItem) {
                BlockState result = VibraniumToolActions.getPathState(state);

                if (result != null) {
                    world.setBlock(pos, result, 11);
                    return InteractionResult.SUCCESS;
                }
            }

            // Hoe
            if (item instanceof HoeItem) {
                BlockState result = VibraniumToolActions.getFarmlandState(state);

                if (result != null) {
                    world.setBlock(pos, result, 11);
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.PASS;
        });
    }
}
