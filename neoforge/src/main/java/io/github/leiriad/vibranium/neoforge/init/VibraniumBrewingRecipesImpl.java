package io.github.leiriad.vibranium.neoforge.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber
public class VibraniumBrewingRecipesImpl {
    // Store Suppliers to evaluate ItemStacks AFTER registries are populated
    private record RecipeEntry(
            Supplier<ItemStack> input,
            Supplier<ItemStack> ingredient,
            Supplier<ItemStack> output
    ) {}

    private static final List<RecipeEntry> QUEUED_RECIPES = new ArrayList<>();

    public static void registerPotionToItemRecipe(
            Supplier<ItemStack> inputPotion,
            Supplier<ItemStack> ingredient,
            Supplier<ItemStack> output
    ) {
        QUEUED_RECIPES.add(new RecipeEntry(inputPotion, ingredient, output));
    }

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();

        for (RecipeEntry entry : QUEUED_RECIPES) {
            ItemStack inputStack = entry.input().get();
            ItemStack ingredientStack = entry.ingredient().get();
            ItemStack outputStack = entry.output().get();

            // Use addRecipe with Ingredients to preserve ItemStacks and DataComponents (POTION_CONTENTS)
            builder.addRecipe(
                    Ingredient.of(inputStack.getItem()),
                    Ingredient.of(ingredientStack.getItem()),
                    outputStack
            );
        }
    }
}
