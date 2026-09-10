package io.github.leiriad.vibranium.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface VibraniumAbilityItem {
    /**
     * Called when the player presses the dedicated ability key (V).
     */
    void onAbilityKeyPressed(Player player, ItemStack stack);
}