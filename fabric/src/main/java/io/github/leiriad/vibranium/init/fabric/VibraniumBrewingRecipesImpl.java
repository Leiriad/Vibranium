package io.github.leiriad.vibranium.init.fabric;

import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class VibraniumBrewingRecipesImpl {
    public static void registerPotionToItemRecipe(Supplier<ItemStack> inputPotion, Supplier<ItemStack> ingredient, Supplier<ItemStack> output) {
        // Calls the implementation in your original package
        io.github.leiriad.vibranium.fabric.init.VibraniumBrewingRecipesImpl.registerPotionToItemRecipe(inputPotion, ingredient, output);
    }
}
