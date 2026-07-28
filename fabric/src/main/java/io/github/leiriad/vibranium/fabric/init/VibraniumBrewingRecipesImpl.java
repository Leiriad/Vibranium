package io.github.leiriad.vibranium.fabric.init;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class VibraniumBrewingRecipesImpl {

    public static void registerPotionToItemRecipe(
            Supplier<ItemStack> inputPotion,
            Supplier<ItemStack> ingredient,
            Supplier<ItemStack> output
    ) {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            ItemStack inputStack = inputPotion.get();
            ItemStack ingredientStack = ingredient.get();
            ItemStack outputStack = output.get();

            //Adds a mix from Potions.WATER + ingredient -> output item
            builder.registerItemRecipe(
                    inputStack.getItem(),
                    Ingredient.of(ingredientStack.getItem()),
                    outputStack.getItem()
            );
        });
    }
}
