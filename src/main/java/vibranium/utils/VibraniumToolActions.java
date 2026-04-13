package vibranium.utils;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.init.VibraniumBlocks;

public class VibraniumToolActions {
    ///Vibranium path block self-managed transition to dirt/grass
    private static BlockState getPathState(BlockState state){
        if (state.is(VibraniumBlocks.VIBRANIUM_DIRT)
                || state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {
            return VibraniumBlocks.VIBRANIUM_PATH.defaultBlockState();
        }
        return null;
    }
    ///Vibranium farmland block self-managed transition to dirt/grass
    public static BlockState getFarmlandState(BlockState state) {
        if (state.is(VibraniumBlocks.VIBRANIUM_DIRT)
                || state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {
            return VibraniumBlocks.VIBRANIUM_FARMLAND.defaultBlockState();
        }
        return null;
    }
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
