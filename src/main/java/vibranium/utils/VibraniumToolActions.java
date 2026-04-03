package vibranium.utils;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.init.VibraniumBlocks;

public class VibraniumToolActions {

    private static BlockState getPathState(BlockState state){
        if (state.is(VibraniumBlocks.VIBRANIUM_DIRT)) {
            return VibraniumBlocks.VIBRANIUM_PATH.defaultBlockState();
        }

        if (state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {
            return VibraniumBlocks.VIBRANIUM_PATH.defaultBlockState();
        }
        return null;
    }
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClientSide()) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);


            //Shovel Actions
            if (!(stack.getItem() instanceof ShovelItem)) return InteractionResult.PASS;

            BlockPos pos = hit.getBlockPos();
            BlockState state = world.getBlockState(pos);

            BlockState result = VibraniumToolActions.getPathState(state);

            if (result != null) {
                world.setBlock(pos, result, 11);
                return InteractionResult.SUCCESS;
            }

            //Hoe Actions


            return InteractionResult.PASS;
        });
    }
}
