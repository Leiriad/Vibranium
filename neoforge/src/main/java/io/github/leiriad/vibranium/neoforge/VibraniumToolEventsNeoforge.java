package io.github.leiriad.vibranium.neoforge;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = VibraniumMod.MOD_ID)
public class VibraniumToolEventsNeoforge {
    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        if (world.isClientSide()) return;

        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = event.getItemStack();

        // Shovel action
        if (stack.getItem() instanceof ShovelItem) {
            BlockState result = VibraniumToolActions.getPathState(state);
            if (result != null) {
                world.setBlock(pos, result, 11);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true); // Arrête l'action pour éviter de creuser l'herbe par exemple
            }
        }

        // Hoe action
        if (stack.getItem() instanceof HoeItem) {
            BlockState result = VibraniumToolActions.getFarmlandState(state);
            if (result != null) {
                world.setBlock(pos, result, 11);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}

